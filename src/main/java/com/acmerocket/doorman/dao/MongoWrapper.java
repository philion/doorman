package com.acmerocket.doorman.dao;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acmerocket.doorman.DoormanConfiguration;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Singleton
public class MongoWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(MongoWrapper.class);
//    static {
//        initLogging();
//    }
    
	private final MongoClient client;
	private final Datastore datastore;
	private final String dbName;

	@Inject
	public MongoWrapper(DoormanConfiguration config) {
	    MongoConfig mongo = config.getMongo();
	    this.dbName = mongo.getDatabase();
	    String url = mongo.getUrl();
	    LOG.info("Configuring mongo with url={}", url);
	    this.client = new MongoClient(new MongoClientURI(url));
	    Morphia morphia = new Morphia();
	    this.datastore = morphia.createDatastore(client, this.dbName);
	}
	
//	public DB getDB() {
//	    return this.client.getDB(this.dbName);
//	}
	
//	public static final void initLogging() {
//	    // Setup morphia logging
//	    MorphiaLoggerFactory.reset(); // so it doesn't whine
//	    MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
//	    
//	    // Turn on MongoDB logging when MongoWrapper is at debug
//        if (LOG.isTraceEnabled()) {
//            LOG.info("Enabling TRACE for MongoDB API");
//            System.setProperty("DEBUG.MONGO", "true");
//            System.setProperty("DB.TRACE", "true");
//        }        
//        // NOTE: Set 'com.snupi.draco.mongo.MongoWrapper' to TRACE to enable DB trace logging
//	}

    /* (non-Javadoc)
     * @see com.snupi.draco.mongo.MongoInstance#getDatastore()
     */
    public Datastore getDatastore() {
		return this.datastore;
	}
	
	/* (non-Javadoc)
     * @see com.snupi.draco.mongo.MongoInstance#getClient()
     */
    public MongoClient getClient() {
		return this.client;
	}

	/* (non-Javadoc)
     * @see com.snupi.draco.mongo.MongoInstance#getHealth()
     */
//    public Result getHealth() {
//	    CommandResult result = this.getDB().getStats();
//		//LOG.debug("Health result: {}", result);
//		
//		if (result.ok()) {
//			return Result.healthy(result.toString());
//		}
//		else {
//			return Result.unhealthy(result.getErrorMessage());
//		}	
//	}
	
	public void ensureIndexes() {
	    LOG.info("Setting up indexes in Mongo");
	    this.datastore.ensureIndexes();
	}
	
	public static final MongoWrapper fromConfig(String fileName) {
        DoormanConfiguration config = DoormanConfiguration.load(fileName); // "src/test/resources/mongo-test.yml"
        return new MongoWrapper(config);
	}
	
	public String toString() {
        return this.getClient().getServerAddressList().get(0).toString();

	}
	
//	public static void main(String[] args) {
//	    MongoWrapper mongo = fromConfig("src/test/resources/integration.yml");
//	    Result result = mongo.getHealth();
//	    LOG.info("### {}", result);
//	}
}
