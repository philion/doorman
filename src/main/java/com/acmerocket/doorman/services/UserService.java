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
package com.acmerocket.doorman.services;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.bson.types.ObjectId;

import com.acmerocket.doorman.model.User;
import com.acmerocket.doorman.mongo.AbstractMorphiaService;
import com.acmerocket.doorman.mongo.MongoInstance;

/**
 * @author philion
 *
 */
@Singleton
public class UserService extends AbstractMorphiaService<User> {
    @Inject
    public UserService(MongoInstance mongo) {
        super(mongo);
    }
    
    public User get(String id) {
        return this.datastore().get(User.class, new ObjectId(id));
    }
}
