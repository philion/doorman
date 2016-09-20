package com.acmerocket.doorman.test.rest;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.acmerocket.doorman.mongo.MongoConfig;
import com.acmerocket.doorman.mongo.MongoInstance;
import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class FongoWrapper implements MongoInstance {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FongoWrapper.class);    
    
    private final MongoClient client;
    private final Datastore datastore;
    private final MongoDatabase db;
    
    public FongoWrapper(MongoConfig config) {
        this(config.getDatabase());
    }
    
    private FongoWrapper(String dbName) {
        Fongo fongo = new Fongo(dbName);
        this.client = new MongoClient(fongo.getServerAddress());
        
        Morphia morphia = new Morphia();
        morphia.mapPackage("com.acmerocket.doorman.model");
        
        this.datastore = morphia.createDatastore(this.client, dbName);
        datastore.ensureIndexes();

        this.db = fongo.getDatabase(dbName);
        
        //this.initializeState();
        
        LOG.info("Initialized {}", this);
    }
    
    /**
     * Load the data from the default data files, to initialize the DB state
     */
//    private void initializeState() {
//    	DataLoader loader = new DataLoader(this);
//    	try {
//			loader.loadDbFromFiles();
//		} 
//    	catch (IOException e) {
//			LOG.error("Unable to load DB with {}", loader, e);
//		}
//	}

    @Override
    public Datastore getDatastore() {
        return this.datastore;
    }

    @Override
    public MongoClient getClient() {
        return this.client;
    }

    @Override
    public MongoDatabase getDB() {
        return this.db;
    }

    //@Override
    public void ensureIndexes() {
        // TODO: DRY
        this.datastore.ensureIndexes();
    }
    
    public String toString() {
    	return this.db.toString(); // stats?
    }
}
