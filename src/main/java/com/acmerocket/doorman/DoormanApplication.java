package com.acmerocket.doorman;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acmerocket.doorman.autoconf.AutoConfigBundle;

import io.dropwizard.Application;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DoormanApplication extends Application<DoormanConfiguration> implements ServerLifecycleListener {
    private static final Logger LOG = LoggerFactory.getLogger(DoormanApplication.class);
    
    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            args = new String[]{"server", "config.yml"};
        }
        else if (args.length == 1) {
            args = new String[]{"server", args[0]};
        }
        
        LOG.info("Starting service with {}", (new File(args[1])).getAbsolutePath());
        new DoormanApplication().run(args);
    }

    @Override
    public String getName() {
        return "doorman";
    }

    @Override
    public void initialize(final Bootstrap<DoormanConfiguration> bootstrap) {
    	String packageName = this.getClass().getPackage().getName();
    	LOG.info("Initializing autoConf with package={}", packageName);
    	
    	bootstrap.addBundle(new AutoConfigBundle<>(DoormanConfiguration.class, packageName));
    }

    @Override
    public void run(final DoormanConfiguration config, final Environment environment) {
        //LOG.info("Running...");
        //String packageName = this.getClass().getPackage().getName();
        //LOG.info("Initializing with package={}", packageName);
        //environment.jersey().packages(packageName);

    } 

    /**
     * Invoked when server is started
     */
    @Override
    public void serverStarted(Server server) {
        LOG.info("Server ready: {}", this);
    }
}
