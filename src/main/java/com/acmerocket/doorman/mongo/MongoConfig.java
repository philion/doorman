package com.acmerocket.doorman.mongo;

import java.lang.reflect.Constructor;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class MongoConfig {
    private static final Logger LOG = LoggerFactory.getLogger(MongoConfig.class);

    @NotEmpty @JsonProperty 
    private String url;
    
    @JsonProperty
    private String mongoInstance;

    public String toString() {
        return getUrl();
    }

    public String getUrl() {
        return url;
    }

    public String getDatabase() {
        String dbUrl = getUrl();
        return dbUrl.substring(dbUrl.lastIndexOf("/") + 1);
    }
    
    /**
     * @return the mongoInstance
     */
    public String getMongoInstance() {
        return this.mongoInstance;
    }

    /**
     * @param mongoInstance the mongoInstance to set
     */
    public void setMongoInstance(String mongoInstance) {
        this.mongoInstance = mongoInstance;
    }
    
    // TODO: Maybe move this to utility? Access HK2 somehow?
    // TODO: Needs more validation
    @JsonIgnore
    public MongoInstance getInstance() {
        if (this.mongoInstance == null) {
            // default
            return new MongoWrapper(this);
        }
        else {
            try {
                Class<?> clazz = Class.forName(mongoInstance);
                Constructor<?> constructor = clazz.getConstructor(this.getClass());
                return (MongoInstance) constructor.newInstance(this);
            }
            catch (Exception e) {
                LOG.error("Unable to construct MongoInstance from: {}", mongoInstance, e);
                return null;
            }
        }
    }
}