package com.acmerocket.doorman.test.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.ClassRule;
import org.junit.Test;

import com.acmerocket.doorman.DoormanApplication;
import com.acmerocket.doorman.DoormanConfiguration;
import com.acmerocket.doorman.model.User;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

public class UserRestTest {
    //private static final Logger LOG = LoggerFactory.getLogger(UserRestTest.class);

    @ClassRule
    public static final DropwizardAppRule<DoormanConfiguration> APP = 
        new DropwizardAppRule<>(DoormanApplication.class, ResourceHelpers.resourceFilePath("local.yml"));
        
    @Test
    public void test404() {
        Response response = target().path("/users/666").request().get();         
        //LOG.info("response: {}", response);
        assertThat(response.getStatus(), equalTo(404));
    }
    
    @Test
    public void testCreateAndGet() {
        User user = new User();
        String email = TestUtils.randomEmail();
        user.setEmail(email);
        Response response = target().path("/users").request().post(Entity.json(user));

        assertEquals(200, response.getStatus());
        // get the entity...
        String id = response.readEntity(String.class);
        
        Response getResponse = target().path("/users/" + id).request().get();  
        assertEquals(200, getResponse.getStatus());
        User newUser = getResponse.readEntity(User.class);
        assertNotNull(newUser);
        assertEquals(email, newUser.getEmail());
    }
    
    // TODO: Build abstraction for this and client creation
    private static WebTarget target() {
        return ClientBuilder.newClient().target(endpoint());
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


