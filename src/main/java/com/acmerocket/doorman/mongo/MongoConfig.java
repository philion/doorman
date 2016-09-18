package com.acmerocket.doorman.mongo;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class MongoConfig {
    @NotEmpty @JsonProperty private String url;

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
}