package com.acmerocket.doorman.test.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acmerocket.doorman.DoormanApplication;
import com.acmerocket.doorman.DoormanConfiguration;
import com.acmerocket.doorman.model.User;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

public class UserRestTest {
    private static final Logger LOG = LoggerFactory.getLogger(UserRestTest.class);

    @ClassRule
    public static final DropwizardAppRule<DoormanConfiguration> APP = new DropwizardAppRule<>(DoormanApplication.class,
            ResourceHelpers.resourceFilePath("fongo.yml"));
    
    //private static final JerseyClientBuilder BUILDER = new JerseyClientBuilder(APP.getEnvironment());
    
    @Test
    public void test404() {
        Response response = target().path("/users/666").request().get();         
        //LOG.info("response: {}", response);
        assertThat(response.getStatus(), equalTo(404));
    }
    
    @Test
    public void testCreateAndGet() {
        User user = new User();
        user.setEmail("sample@sample.com");
        Response response = target().path("/users").request(MediaType.TEXT_PLAIN_TYPE).post(Entity.json(user));
        //LOG.info("response: {}", response);

        assertEquals(200, response.getStatus());
        // get the entity...
        String id = response.readEntity(String.class);
        
        Response getResponse = target().path("/users/" + id).request().get();  
        assertEquals(200, getResponse.getStatus());
        User newUser = getResponse.readEntity(User.class);
        LOG.info("### {}", newUser);
    }
    
    // TODO: Build abstraction for this and client creation
    private static WebTarget target() {
        Client client = ClientBuilder.newClient();
        return client.target(endpoint());
    }
    private static String endpoint() {
        StringBuilder builder = new StringBuilder();

        // protocol first
        // TODO: Add better URL building, perhaps based on UriBuilder
        builder.append("http://");
        //builder.append(httpConfig.isSslConfigured() ? "https://" : "http://");
        
        // then hostname
        builder.append("localhost:");
        // then port
        builder.append(APP.getLocalPort());
        
        // then root
        String root = APP.getEnvironment().getApplicationContext().getContextPath();
        if (root.endsWith("/*")) {
            root = root.substring(0, root.length() - 2).trim();
        }
        builder.append(root);
        
        String endpoint = builder.toString();
        
        if (endpoint.endsWith("/")) {
            return endpoint.substring(0, endpoint.length() - 1);
        }
        else {    
            return endpoint;
        }
    }
}


