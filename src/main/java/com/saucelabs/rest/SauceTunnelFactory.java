/*
 * The MIT License
 *
 * Copyright (c) 2010, InfraDNA, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.saucelabs.rest;

import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.VisibilityChecker.Std;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.ANY;
import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.NONE;

/**
 * Secure tunnels to Sauce OnDemand.
 *
 * @author Kohsuke Kawaguchi
 */
public class SauceTunnelFactory {
    /*package*/ final Credential credential;

    /**
     * Uses the specified credential to connect to sauce tunnels.
     */
    public SauceTunnelFactory(Credential credential) {
        this.credential = credential;
    }

    /**
     * Uses {@linkplain Credential#Credential() the default credential file} to connect to sauce tunnels.
     */
    public SauceTunnelFactory() throws IOException {
        this(new Credential());
    }

    public SauceTunnel create(String... domains) throws IOException {
        return create(asList(domains));
    }

    public SauceTunnel create(Collection<String> domains) throws IOException {
        CreateTunnelResponse rsp = credential.call("tunnels")
                .post(new CreateTunnelRequest(domains), CreateTunnelResponse.class);
        if (rsp.error!=null)
            throw new IOException("Failed to create a tunnel to "+domains+" "+rsp.error);

        if (!rsp.ok)
            throw new IOException("Failed to create a tunnel but for a no apparent reason. ???");

        return new SauceTunnel(this,rsp.id);
    }

    /**
     * Lists up existing tunnels.
     *
     * @return
     *      Can be empty but never null.
     */
    public List<SauceTunnel> list() throws IOException {
        List<StatusResponse> raw = credential.call("tunnels").get(new TypeReference<List<StatusResponse>>() {});
        List<SauceTunnel> tunnels = new ArrayList<SauceTunnel>(raw.size());
        for (StatusResponse r : raw)
            tunnels.add(new SauceTunnel(this,r));
        return tunnels;
    }


    /*package*/ static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setVisibilityChecker(new Std(NONE, NONE, NONE, NONE, ANY));
        MAPPER.getDeserializationConfig().set(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
