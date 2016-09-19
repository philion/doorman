package com.acmerocket.doorman.test.rest;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.acmerocket.doorman.mongo.MongoInstance;
import com.codahale.metrics.health.HealthCheck;
import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;


public class FongoWrapper implements MongoInstance {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FongoWrapper.class);

    private static final String DB_NAME = "test";
    
    private static final FongoWrapper INSTANCE = new FongoWrapper();
    
    private final MongoClient client;
    private final Datastore datastore;
    private final MongoDatabase db;

    private FongoWrapper() {
    	this(DB_NAME);
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
	
	public static final FongoWrapper instance() {
        return INSTANCE;
	}
	
	public static HealthCheck healthCheck() {
	    return new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
	    };
	}

    @Override
    public void ensureIndexes() {
        // TODO: DRY
        this.datastore.ensureIndexes();
    }
    
    public String toString() {
    	return this.db.toString(); // stats?
    }
}
