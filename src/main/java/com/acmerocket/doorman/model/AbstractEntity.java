package com.acmerocket.doorman.model;

import java.util.Date;

import org.mongodb.morphia.mapping.Mapper;

import com.acmerocket.doorman.util.EntityUtils;
import com.acmerocket.doorman.util.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractEntity implements Identifiable {
    public static final String ID = Mapper.ID_KEY;
    public static final String CREATED = "created";
    public static final String UPDATED = "updated";

    @JsonProperty protected Date created;
    @JsonProperty protected Date updated = new Date();

    public AbstractEntity() {
        // confirm @Id field
        if (EntityUtils.getIdField(this) == null) {
            throw new IllegalStateException("No fields annotated with @Id on " + this.getClass().getName());
        }
    }

    public String getId() {
        return EntityUtils.getAnnotatedId(this);
    }

    public void setId(String id) {
        EntityUtils.setId(this, id);
    }

    public Date getUpdated() {
        return this.updated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String toString() {
        return Utils.toJson(this);
    }
}