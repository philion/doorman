package com.acmerocket.doorman.mongo;

import org.mongodb.morphia.Datastore;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public interface MongoInstance {

    public Datastore getDatastore();

    public MongoClient getClient();
    
    public DB getDB();
    
    public void ensureIndexes();

}