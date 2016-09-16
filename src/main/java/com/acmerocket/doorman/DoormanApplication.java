package com.acmerocket.doorman;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DoormanApplication extends Application<DoormanConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DoormanApplication().run(args);
    }

    @Override
    public String getName() {
        return "doorman";
    }

    @Override
    public void initialize(final Bootstrap<DoormanConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final DoormanConfiguration configuration, final Environment environment) {
        // TODO: implement application
    }
}
