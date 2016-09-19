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
    
    //private final CountDownLatch readyLatch = new CountDownLatch(1);

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
    	AutoConfigBundle<DoormanConfiguration> autoConf = new AutoConfigBundle<>(DoormanConfiguration.class, packageName);
    	bootstrap.addBundle(autoConf);
    }

    @Override
    public void run(final DoormanConfiguration config, final Environment environment) {
    	
    	
        // Think bundles!
        //environment.jersey().register(new UsersResource(new UserService(new MongoWrapper(config))));
        //environment.jersey().register(new UsersResource(new UserService(FongoWrapper.instance())));
        //environment.healthChecks().register("fongo", FongoWrapper.healthCheck());
    } 

    /**
     * Invoked when server is started
     */
    @Override
    public void serverStarted(Server server) {
        //this.readyLatch.countDown();
        LOG.info("Server ready: {}", this);
    }

//    /**
//     * Wait for the server to be ready
//     *
//     * @param timeout Number of milliseconds to wait for the server to come up
//     * @throws InterruptedException
//     */
//    public void waitForReady(int timeout) throws InterruptedException {
//        this.readyLatch.await(timeout, TimeUnit.MILLISECONDS);
//        LOG.debug("Releasing latch in thread={}", Thread.currentThread());
//    }
//
//    public String toString() {
//        String ready = this.readyLatch.getCount() == 0 ? "ready" : "initializing";
//        return this.getClass().getSimpleName() + "[" + ready + "]";
//    }
}
