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

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.VisibilityChecker.Std;
import org.codehaus.jackson.type.TypeReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.ANY;
import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.NONE;

/**
 * Represents the REST API call.
 *
 * @author Kohsuke Kawaguchi
 */
class Call {
    private final HttpURLConnection con;

    Call(HttpURLConnection con) {
        this.con = con;
    }

    /**
     * Makes an HTTP POST call where the request and response are both JSON.
     */
    public <T> T post(Object json, Class<T> responseType) throws IOException {
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type","application/json");
        con.setDoOutput(true);
        con.connect();
        OutputStream out = con.getOutputStream();
        MAPPER.writeValue(out, json);

        if (con.getResponseCode()>=400)
            throw handleErrorResponse();
        return MAPPER.readValue(con.getInputStream(),responseType);
    }

    /**
     * Makes an HTTP GET call where the response is JSON
     */
    public <T> T get(TypeReference<T> responseType) throws IOException {
        con.connect();
        if (con.getResponseCode()>=400)
            throw handleErrorResponse();
        return (T)MAPPER.readValue(con.getInputStream(),responseType);
    }

    /**
     * Makes an HTTP GET call where the response is JSON
     */
    public <T> T get(Class<T> responseType) throws IOException {
        con.connect();
        if (con.getResponseCode()>=400)
            throw handleErrorResponse();
        return (T)MAPPER.readValue(con.getInputStream(),responseType);
    }

    /**
     * Makes an HTTP DELETE call.
     */
    public void delete() throws IOException {
        con.setRequestMethod("DELETE");
        con.connect();
        if (con.getResponseCode()!=200)
            throw handleErrorResponse();
        con.getInputStream().close();
    }

    private IOException handleErrorResponse() throws IOException {
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        IOUtils.copy(con.getErrorStream(),err);
        return new IOException("API called failed. "+con.getResponseCode()+" "+con.getResponseMessage()+"\n"+err);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setVisibilityChecker(new Std(NONE, NONE, NONE, NONE, ANY));
        MAPPER.getDeserializationConfig().set(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
