package com.acmerocket.doorman;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.acmerocket.doorman.mongo.MongoConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class DoormanConfiguration extends Configuration {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DoormanConfiguration.class);

    @JsonProperty @NotNull @Valid 
    private MongoConfig mongo;

	/**
	 * @return
	 */
	public MongoConfig getMongo() {
		return this.mongo;
	}
}
