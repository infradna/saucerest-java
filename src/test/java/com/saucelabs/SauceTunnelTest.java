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

import com.saucelabs.rest.SauceTunnel;
import com.saucelabs.rest.SauceTunnelFactory;
import junit.framework.TestCase;

/**
 * Unit test for {@link SauceTunnel}.
 */
public class SauceTunnelTest extends TestCase
{
    public void testCreateAndDestroy() throws Exception {
        SauceTunnelFactory tunnelFactory = new SauceTunnelFactory();
        SauceTunnel t = tunnelFactory.create("www.kohsuke.org");
        t.waitUntilRunning(30000);
        assertTrue(t.isRunning());
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
}
