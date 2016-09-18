package com.acmerocket.doorman.mongo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.collections4.MapUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acmerocket.doorman.model.AbstractEntity;
import com.acmerocket.doorman.model.Identifiable;
import com.acmerocket.doorman.model.Service;
import com.acmerocket.doorman.util.EntityUtils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.WriteResult;

public abstract class AbstractMorphiaService<T extends Identifiable> implements Service<T> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractMorphiaService.class);
    
    private static final Function<Field, String> FIELD_TO_STRING = new Function<Field, String>() {
        @Override
        public String apply(Field field) {
            return field.getName();
        }
    };
    
    private final Datastore datastore;
    private final Class<T> entityClazz;
    private final List<String> validFields;

    @SuppressWarnings("unchecked")
    public AbstractMorphiaService(MongoInstance mongo) {
        this.entityClazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.datastore = mongo.getDatastore();
        //LOG.info("datastore: {}", datastore);
        this.validFields = Lists.transform(Arrays.asList(entityClazz.getDeclaredFields()), FIELD_TO_STRING);
    }

    private Class<T> entityClass() {
        return entityClazz;
    }

    @Override
    public T get(String id) {
        if (id == null) {
            return null; // NOTE REALLY?
        }

        // validate ID
        return query(id).get();
    }

    protected Query<T> query(boolean validate) {
        Query<T> query = datastore.createQuery(entityClass());
        if (validate) {
            query.disableValidation();
        }
        return query;
    }

    protected Query<T> query() {
        return datastore.createQuery(entityClass());
    }

    protected Query<T> query(String id) {
        return datastore.createQuery(entityClass()).field(Mapper.ID_KEY).equal(id);
    }

    @Override
    public String create(T entity) {
        return datastore.save(entity).getId().toString();
    }

    // Override with custom entity validation as needed
    protected void validate(T entity) {}

    protected void ensureId(T entity) {
        if (entity.getId() == null) {
            EntityUtils.setId(entity, generateId(entity));
        }
    }

    /**
     * Template method with default behavior: Use ObjectId as id.
     * Override to provide specific ID behavior
     */
    protected String generateId(T entity) {
        // default ID: use ObjectId as a string
        return (new ObjectId()).toString();
    }

    @Override
    public void update(String id, T entity) {
        datastore.updateFirst(query(id), entity, false);
    }

    public void upsert(String id, T entity) {
        if (query(id).get() == null) {
            create(entity);
        } else {
            update(id, entity);
        }
    }

    @Override
    public boolean update(String id, Map<String, ?> fields) {
        if (MapUtils.isEmpty(fields)) return true; // No-op

        // Create the '_id' query, which includes the "not deleted" clause
        Query<T> query = query(id);

        // Create an update for each interesting field
        UpdateOperations<T> updateOps = datastore.createUpdateOperations(entityClass());
        int fieldCount = 0;
        for (Map.Entry<String, ?> entry : fields.entrySet()) {
            String field = entry.getKey();
            if (validFields.contains(field) /*&& !systemFields.contains(field)*/) {
                Object value = entry.getValue();
                if (value != null) {
                    updateOps.set(field, value);
                } else {
                    updateOps.unset(field);
                }
                fieldCount++;
            }
        }
        if (fieldCount == 0) {
            LOG.warn("No valid fields specified for updating {}: {}", id, fields);
            return false;
        }

        // Execute the update
        UpdateResults result = datastore.updateFirst(query, updateOps);
        return result.getUpdatedCount() == 1;
    }

    public void update(String id, String fieldName, Object value) {
        Query<T> query = query().field(AbstractEntity.ID).equal(id); // specify the entity ID
        UpdateOperations<T> updateOps = createUpdateOperations().set(fieldName, value);
        UpdateResults result = datastore.updateFirst(query, updateOps, false);
        if (result.getUpdatedCount() != 1) {
            throw new RuntimeException("Unknown id=" + id);
        }
        //else if (result.getHadError()) {
        //    LOG.warn("Unable to set {} on {}: {}", fieldName, id, result.getError());
        //}
    }

    // TODO: Return field value on success
    public void remove(String id, String fieldName) {
        Query<T> query = query().field(AbstractEntity.ID).equal(id); // specify the entity ID
        UpdateOperations<T> updateOps = createUpdateOperations().unset(fieldName); // remove the field
        UpdateResults result = datastore.updateFirst(query, updateOps, false);
        if (result.getUpdatedCount() != 1) {
            throw new RuntimeException("Unknown id=" + id);
        }
        //else if (result.getHadError()) {
        //    LOG.warn("Unable to remove {} from {}: {}", fieldName, id, result.getError());
        //}
    }

    @Override
    public void delete(String id) {
        WriteResult result = datastore.delete(entityClass(), id);
        if (result.getN() == 0) {
            LOG.trace("Cannot delete, unknown ID {}:{}", entityClass().getSimpleName(), id);
        } else {
            LOG.trace("Deleted {}:{}", entityClass().getSimpleName(), id);
        }
    }

    @Override
    public long delete(MultivaluedMap<String, String> terms) {
        return bulkDelete(MongoUtil.createQuery(query(), terms, dateQueryField()));
    }

    public long bulkDelete(Query<T> query) {
        return datastore.delete(query).getN();
    }

    @Override
    public List<T> find(MultivaluedMap<String, String> params) {
        return createQuery(params).asList();
    }

    @Override
    public long count(MultivaluedMap<String, String> params) {
        return createQuery(params).countAll();
    }

    @Override
    public Set<String> ids(MultivaluedMap<String, String> params) {
        Query<T> query = createQuery(params).retrievedFields(true, AbstractEntity.ID);
        QueryResults<T> results = find(query);

        Set<String> ids = Sets.newHashSet();
        for (T entity : results) {
            ids.add(entity.getId());
        }
        return ids;
    }

    @Override
    public boolean exists(String id) {
        //return datastore().exists(new Key<>(entityClass(), id)) != null;
        // FIXME
        return false;
    }

    public boolean touch(String id, boolean create) {
        Query<T> query = query(id);

        // Create an update for each interesting field
        UpdateOperations<T> updateOps = createUpdateOperations();
        updateOps.set("updated", new Date());

        // Execute the update
        /*UpdateResults result = */datastore.updateFirst(query, updateOps, create);
//        if (result.getHadError()) {
//            LOG.warn("Unable to touch {}: {}", id, result.getError());
//        }

        return false; // this updates the "updated" stamp only, doesn't change the state
    }

    public Query<T> createQuery(MultivaluedMap<String, String> terms) {
        return MongoUtil.createQuery(query(), terms, dateQueryField());
    }

    protected String dateQueryField() {
        return "updated";
    }

    public QueryResults<T> find(Query<T> q) {
        return q;
    }

    protected UpdateOperations<T> createUpdateOperations() {
        return datastore.createUpdateOperations(entityClass());
    }

    protected Datastore datastore() {
        return datastore;
    }

    public List<T> getAll(Iterable<String> ids) {
        if (ids == null || !ids.iterator().hasNext()) {
            return Collections.emptyList();
        }
        return query().field(AbstractEntity.ID).in(ids).asList();
    }

    public List<T> getAll() {
        return query().asList();
    }
}