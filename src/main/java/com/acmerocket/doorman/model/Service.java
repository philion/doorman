package com.acmerocket.doorman.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;


public interface Service<R extends Identifiable> {
    /**
     * Lookup a resource/entity, based on its ID
     *
     * @param id The resource ID
     * @return The found resource, or null
     */
    public R get(String id);

    /**
     * Create a resource, given the params in the supplied prototype entity
     * NOTE: Any "id" fields should be ignored!
     *
     * @param toCreate The field with which to create an entity
     * @return The ID of the created entity
     */
    public String create(R toCreate);

    /**
     * Update an entity, given the updated resource
     * NOTE: If an ID field is supplied in the resource, it MUST MATCH the
     * passed-in resource ID
     *
     * @param id      The ID of the resource
     * @param updated The updated fields
     */
    public void update(String id, R updated);

    public boolean update(String id, Map<String, ?> fields);

    public void update(String id, String name, Object value);

    public void remove(String id, String name);

    /**
     * @param id The ID of the resource to delete
     */
    public void delete(String id);

    /**
     * Find resources that match the terms
     */
    public Collection<R> find(MultivaluedMap<String, String> params);

    /**
     * Count the resources the match the terms
     */
    public long count(MultivaluedMap<String, String> params);

    /**
     * Delete entities which match the given query parameters
     */
    public long delete(MultivaluedMap<String, String> params);

    /**
     * Get a list of IDs which match provided search terms
     */
    public Set<String> ids(MultivaluedMap<String, String> params);

    /**
     * Returns true if that ID is valid and exists in the datastore
     */
    public boolean exists(String id);

    /**
     * Gets all the listed ids
     */
    public List<R> getAll(Iterable<String> ids);

    public List<R> getAll();
}