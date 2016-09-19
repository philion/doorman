package com.acmerocket.doorman.mongo;

import org.jvnet.hk2.annotations.Contract;
import org.mongodb.morphia.Datastore;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

@Contract
public interface MongoInstance {

    public Datastore getDatastore();

    public MongoClient getClient();
    
    public MongoDatabase getDB();
    
    public void ensureIndexes();

}