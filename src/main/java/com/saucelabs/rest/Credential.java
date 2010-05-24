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

import com.trilead.ssh2.Connection;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * Credential for the Sauce OnDemand service.
 *
 * @author Kohsuke Kawaguchi
 */
public class Credential {
    /**
     * User name.
     */
    private final String username;
    /**
     * API access key, which looks like a GUID.
     */
    private final String key;

    /**
     * Creates a credential by specifying the username and the key directly.
     */
    public Credential(String username, String key) {
        this.username = username;
        this.key = key;
    }

    /**
     * Loads a credential from the specified property file.
     *
     * The property file should look like the following:
     * <pre>
     * username=kohsuke
     * password=12345678-1234-1234-1234-1234567890ab
     * </pre>
     *
     * @throws IOException
     *      If the file I/O fails, such as non-existent file, incorrect format, or if the file is missing
     *      the 'username' or 'key' parameters.
     */
    public Credential(File propertyFile) throws IOException {
        Properties props = new Properties();
        FileInputStream in = new FileInputStream(propertyFile);
        try {
            props.load(in);
            this.username = props.getProperty("username");
            this.key = props.getProperty("key");
            if (username==null)
                throw new IOException(propertyFile+" didn't contain the 'username' parameter");
            if (key==null)
                throw new IOException(propertyFile+" didn't contain the 'key' parameter");
        } finally {
            in.close();
        }
    }

    /**
     * Loads the credential from the default location "~/.sauce-ondemand"
     */
    public Credential() throws IOException {
        this(getDefaultCredentialFile());
    }

    public String getUsername() {
        return username;
    }

    public String getKey() {
        return key;
    }

    /**
     * Persists this credential to the disk.
     *
     * @throws IOException
     *      If the file I/O fails.
     */
    public void saveTo(File propertyFile) throws IOException {
        Properties props = new Properties();
        props.put("username",username);
        props.put("key",key);
        FileOutputStream out = new FileOutputStream(propertyFile);
        try {
            props.store(out,"Sauce OnDemand access credential");
        } finally {
            out.close();
        }
    }

    /**
     * Gets the URL of the REST API.
     *
     * @param suffix
     *      The trailing API path portion like "tunnels".
     */
    URL getRestURL(String suffix) throws IOException {
        return new URL("https://saucelabs.com/rest/"+username+"/"+suffix);
    }

    /**
     * Connects to the rest URL.
     *
     * @param suffix
     *      See the 'suffix' parameter of {@link #getRestURL(String)}
     */
    Call call(String suffix) throws IOException {
        HttpURLConnection con = (HttpURLConnection) getRestURL(suffix).openConnection();

        // set the credential
        String userpassword = username + ":" + key;
        con.setRequestProperty("Authorization", "Basic " + new BASE64Encoder().encode(userpassword.getBytes()));

        return new Call(con);
    }

    /**
     * Authenticates the specified SSH connection with the credential.
     */
    void authenticate(Connection con) throws IOException {
        con.authenticateWithPassword(username,key);
    }

    /**
     * Location of the default credential file. "~/.sauce-ondemand"
     *
     * <p>
     * This common convention allows all the tools that interact with Sauce OnDemand REST API
     * to use the single credential, thereby simplifying the user configuration.
     */
    public static File getDefaultCredentialFile() {
        return new File(new File(System.getProperty("user.home")),".sauce-ondemand");
    }
}
