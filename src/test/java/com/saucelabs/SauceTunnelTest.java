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
package com.saucelabs;

import com.saucelabs.rest.Credential;
import com.saucelabs.rest.SauceTunnel;
import com.saucelabs.rest.SauceTunnelFactory;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletMapping;
import org.mortbay.jetty.webapp.Configuration;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.jetty.webapp.WebXmlConfiguration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Unit test for {@link SauceTunnel}.
 */
public class SauceTunnelTest extends TestCase
{
    public void testCreateAndDestroy() throws Exception {
        SauceTunnelFactory tunnelFactory = new SauceTunnelFactory();
        SauceTunnel t = tunnelFactory.create("www.kohsuke.org");
        t.waitUntilRunning(90000);
        assertTrue("tunnel id="+t.getId()+" isn't coming online", t.isRunning());
        t.destroy();
    }

    public void testList() throws Exception {
        SauceTunnelFactory tunnelFactory = new SauceTunnelFactory();
        for (SauceTunnel t : tunnelFactory.list()) {
            System.out.println("id="+t.getId());
            System.out.println("create="+t.getCreationTime());
            System.out.println("domains="+t.getDomainNames());
            System.out.println("shutdown="+t.getShutDownTime());
            System.out.println("running="+t.isRunning());
            System.out.println();
        }
    }

    /**
     * Start a web server locally, set up an SSH tunnel, and have Sauce OnDemand connect to the local server.
     */
    public void testFullRun() throws Exception {
        final int code = new Random().nextInt();

        // start the Jetty locally and have it respond our secret code.
        Server server = new Server();
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(new ServletHolder(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentType("text/html");
                resp.getWriter().println("<html><head><title>test"+code+"</title></head><body>it works</body></html>");
            }
        }),"/");
        server.setHandler(handler);

        SocketConnector connector = new SocketConnector();
        server.addConnector(connector);
        server.start();
        System.out.println("Started Jetty at "+connector.getLocalPort());

        try {
            // start a tunnel
            System.out.println("Starting a tunnel");
            SauceTunnelFactory tunnelFactory = new SauceTunnelFactory();
            SauceTunnel t = tunnelFactory.create("test"+code+".org");
            t.waitUntilRunning(90000);
            assertTrue("tunnel id="+t.getId()+" isn't coming online", t.isRunning());
            t.connect(80,"localhost",connector.getLocalPort());
            System.out.println("tunnel established");


            try {
                Credential c = new Credential();
                DefaultSelenium selenium = new DefaultSelenium(
                            "saucelabs.com",
                            4444,
                            "{\"username\": \""+c.getUsername()+"\"," +
                            "\"access-key\": \""+c.getKey()+"\"," +
                            "\"os\": \"Windows 2003\"," +
                            "\"browser\": \"firefox\"," +
                            "\"browser-version\": \"3.\"," +
                            "\"job-name\": \"This is an example test\"}",
                            "http://test"+code+".org/");
                selenium.start();
                selenium.open("/");
                // if the server really hit our Jetty, we should see the same title that includes the secret code.
                assertEquals("test"+code,selenium.getTitle());
                selenium.stop();
            } finally {
                t.destroy();
            }
        } finally {
            server.stop();
        }
    }
}
