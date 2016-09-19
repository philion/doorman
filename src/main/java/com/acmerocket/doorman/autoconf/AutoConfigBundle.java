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
package com.acmerocket.doorman.autoconf;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;

import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.jvnet.hk2.annotations.Service;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;

public class AutoConfigBundle<T extends Configuration> implements ConfiguredBundle<T> {
    private static final Logger LOG = LoggerFactory.getLogger(AutoConfigBundle.class);
    
    private final Class<T> configurationClass;
    private final Reflections reflections;

    public AutoConfigBundle(final Class<T> configurationClass, final String packageName) {
        this.configurationClass = configurationClass;
        FilterBuilder filterBuilder = new FilterBuilder();
        filterBuilder.include(FilterBuilder.prefix(packageName));

        ConfigurationBuilder reflectionCfg = new ConfigurationBuilder();
        reflectionCfg.addUrls(ClasspathHelper.forPackage(packageName));
        reflectionCfg.filterInputsBy(filterBuilder).setScanners(new SubTypesScanner(), new TypeAnnotationsScanner());
        reflections = new Reflections(reflectionCfg);
        LOG.debug("Configured with {}", reflections);
    }

//    public static <T extends Configuration> Builder<T> newBuilder() {
//        return new Builder<T>();
//    }

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
        LOG.debug("Intialzing with {}", bootstrap);
    }

    @Override
    public void run(final T configuration, final Environment environment) throws Exception {
        LOG.debug("Running with config={}", configuration);

        registerConfigurationProvider(configuration, environment);
        registerServices(environment);
        registerResources(environment);
        registerHealthChecks(environment);
    }

    private void registerConfigurationProvider(final T configuration, final Environment environment) {
        // Create binding for the config class so it is injectable.
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(configuration).to(configurationClass);
            }
        });

        // Attempt to get all the configuration parameter that are suitable for
        // injection.
        @SuppressWarnings("unchecked")
        Map<String, Object> configMap = environment.getObjectMapper().convertValue(configuration, Map.class);
        if (configMap == null) {
            configMap = Collections.<String, Object> emptyMap();
        }
        // Don't include any of the default things.
        final Set<String> blackListedConfigAttribute = new HashSet<String>();
        blackListedConfigAttribute.addAll(Arrays.asList("logging", "server", "metrics"));

        final String configNamePrefix = "config.";
        Set<AbstractActiveDescriptor<?>> configEntries = new HashSet<AbstractActiveDescriptor<?>>();
        for (String key : configMap.keySet()) {
            if (blackListedConfigAttribute.contains(key)) {
                continue;
            }
            Object o = configMap.get(key);
            AbstractActiveDescriptor<?> s = BuilderHelper.createConstantDescriptor(o, configNamePrefix + key,
                    o.getClass());
            configEntries.add(s);
        }
        if (!configEntries.isEmpty()) {
            environment.jersey().register(new ConfigBinder(configEntries));
        }

    }

    private void registerServices(final Environment environment) {
        Set<Class<?>> services = this.reflections.getTypesAnnotatedWith(Service.class, true);
        if (!services.isEmpty()) {
            environment.jersey().register(new ServiceBinder(services));
        }
    }
    
    private void registerResources(final Environment environment) {
        Set<Class<? extends Object>> resourceClasses = reflections.getTypesAnnotatedWith(Path.class);
        for (Class<?> resourceClass : resourceClasses) {
            environment.jersey().register(resourceClass);
        }
    }

    private void registerHealthChecks(final Environment env) {
        Set<Class<? extends HealthCheck>> healthCheckClasses = reflections.getSubTypesOf(HealthCheck.class);
        for (Class<? extends HealthCheck> healthCheckKlass : healthCheckClasses) {
            try {
                env.healthChecks().register(healthCheckKlass.getName(), healthCheckKlass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error("Could not create health check.", e);
            }
        }
    }
    
//    public static class Builder<T extends Configuration> {
//        private Class<T> klass;
//        private String packageName;
//        
//        public Builder<T> addPackageName(final String packageName) {
//            this.packageName = packageName;
//            return this;
//        }
//
//        public Builder<T> setConfigurationClass(final Class<T> klass) {
//            this.klass = klass;
//            return this;
//        }
//        
//        public AutoConfigBundle<T> build() {
//            return new AutoConfigBundle<T>(klass, packageName);
//        }
//    }

    class ServiceBinder extends AbstractBinder {
        final Set<Class<?>> klasses;
        public ServiceBinder(Set<Class<?>> services) {
            this.klasses = services;
        }

        @Override
        protected void configure() {
            for (Class<?> klass : this.klasses) {
                addActiveDescriptor(klass);
            }
        }
    }
    
    class ConfigBinder extends AbstractBinder {
        final Set<AbstractActiveDescriptor<?>> descriptorList;
        public ConfigBinder(Set<AbstractActiveDescriptor<?>> configEntries) {
            this.descriptorList = configEntries;
        }

        @Override
        protected void configure() {
            for (AbstractActiveDescriptor<?> d : this.descriptorList) {
                addActiveDescriptor(d);                
            }
        }
    }
}
