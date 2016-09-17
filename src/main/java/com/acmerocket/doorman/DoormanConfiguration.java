package com.acmerocket.doorman;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.acmerocket.doorman.dao.MongoConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class DoormanConfiguration extends Configuration {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DoormanConfiguration.class);

    @JsonProperty @NotNull @Valid 
    private MongoConfig mongo;
	
    public static DoormanConfiguration load(String fileName) {
//        try {
//            final ConfigurationFactory<DoormanConfiguration> configurationFactory = ConfigurationFactory.build(fileName);
//            final File file = new File(fileName);
//            if (!file.exists()) {
//                throw new FileNotFoundException("File " + file.getAbsolutePath() + " not found");
//            }
//            LOG.info("Loading configuration from {}", file.getAbsolutePath());
//            return configurationFactory.build(file);
//        } catch (Exception e) {
//            throw new ConfigurationException("Error loading config " + fileName, e);
//        }
    	return null; // FIXME
    }

	/**
	 * @return
	 */
	public MongoConfig getMongo() {
		// TODO Auto-generated method stub
		return null;
	}
}
