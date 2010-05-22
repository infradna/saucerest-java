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

import java.util.List;

/**
 * JSON object that represents the status of the tunnel.
 *
 * @author Kohsuke Kawaguchi
 */
class StatusResponse {
    /*
        Response looks like this:

        {"Status": "running", "CreationTime": 1274488369, "ModificationTime": 1274488401, "Host": "maki201.miso.saucelabs.com",
            "LaunchTime": 1274488375, "Provider": "sauce", "Owner": "kohsuke", "_id": "233b5b2249a1ec135cd08a0ba33e14ec",
            "Type": "tunnel", "id": "233b5b2249a1ec135cd08a0ba33e14ec", "DomainNames": ["www.kohsuke.org"]},
        {"Status": "halting", "ShutDownTime": 1274487617, "CreationTime": 1274487616, "ModificationTime": 1274488572,
            "Owner": "kohsuke", "_id": "4491c98015e61627f3871999d315ee1a", "Type": "tunnel",
            "id": "4491c98015e61627f3871999d315ee1a", "UserShutDown": true, "DomainNames": ["www.kohsuke.org"]}
     */
    String Status;
    long ShutDownTime;
    long CreationTime;
    long ModificationTime;
    String Owner;
    String id;
    String Type;
    String Host;
    Boolean UserShutDown;
    List<String> DomainNames;
}
