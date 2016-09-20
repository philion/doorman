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
import org.jvnet.hk2.annotations.Service;

import com.acmerocket.doorman.DoormanConfiguration;
import com.acmerocket.doorman.model.User;
import com.acmerocket.doorman.mongo.AbstractMorphiaService;

/**
 * @author philion
 *
 */
@Service @Singleton
public class UserService extends AbstractMorphiaService<User> {
    @Inject
    public UserService(DoormanConfiguration config) {
        super(config.getMongo().getInstance()); // FIXME hacky. pick a model and stick with it. reall: make HK2 runtime/test work
    }
    
    public User get(String id) {
        return this.datastore().get(User.class, new ObjectId(id));
    }
}
