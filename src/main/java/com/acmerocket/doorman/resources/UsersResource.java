/**
 * Copyright 2016 Acme Rocket Company [acmerocket.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acmerocket.doorman.resources;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acmerocket.doorman.model.User;
import com.acmerocket.doorman.services.UserService;

/**
 * @author philion
 */
@Singleton
@Path("/users")
public class UsersResource {
    private static final Logger LOG = LoggerFactory.getLogger(UsersResource.class);

    private final UserService users;
    
    @Inject
    public UsersResource(UserService service) {
        this.users = service;
    }
    
    @GET @Path("/{id}") @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("id") String id) {
        User user = null;
        try {
            user = this.users.get(id);
            
            LOG.debug("#### found {}", user);
        }
        catch (IllegalArgumentException ex) {
            throw new NotFoundException();
        }
        if (user == null) {
            throw new NotFoundException();
        }
                
        return user;
    }
    
    @POST 
    public String createUser(User user) {
        LOG.debug("POST user user={}", user);
        return this.users.create(user);
    }
}
