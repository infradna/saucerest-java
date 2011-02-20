package com.saucelabs.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JobFactory {
    /*package*/ final Credential credential;

    /**
     * Uses the specified credential to connect to sauce tunnels.
     */
    public JobFactory(Credential credential) {
        this.credential = credential;
    }

    /**
     * Uses {@linkplain Credential#Credential() the default credential file} to connect to sauce tunnels.
     */
    public JobFactory() throws IOException {
        this(new Credential());
    }

    public Job update(String id, UpdateJob job) throws IOException {
        return credential.call("v1", "jobs/" + id).put(job, Job.class);
    }

    public Job get(String id) throws IOException {
        return credential.call("v1", "jobs/" + id).get(Job.class);
    }

}
