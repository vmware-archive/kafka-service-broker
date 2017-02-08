/**
 Copyright (C) 2016-Present Pivotal Software, Inc. All rights reserved.

 This program and the accompanying materials are made available under
 the terms of the under the Apache License, Version 2.0 (the "License”);
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package io.pivotal.cf.servicebroker;

import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static junit.framework.TestCase.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UtilTest {

    @Autowired
    private Util util;

    @Test
    public void testGetConnection() throws InterruptedException {
        ZkConnection c = null;
        try {
            c = util.getConnection();
            assertNotNull(c);
            c.connect(null);
            String s = c.getServers();
            assertNotNull(s);
            assertEquals("104.154.136.114:2181", s);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    @Test
    public void testGetClient() throws InterruptedException {
        ZkClient c = null;
        try {
            c = util.getClient();
            assertNotNull(c);
            assertEquals(0, c.numberOfListeners());
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    @Test
    public void testGetUtil() throws InterruptedException {
        ZkUtils u = null;
        try {
            u = util.getUtils();
            assertNotNull(u);
            assertNotNull(u.ClusterId());
        } finally {
            if (u != null) {
                u.close();
            }
        }
    }

    @Test
    public void testGetServers() throws Exception {
        List<String> s = util.getBootstrapServers();
        assertNotNull(s);
        assertTrue(s.size() > 0);
    }

    @Test
    public void testZoo() throws Exception {
        ZooKeeper z = null;
        try {
            z = util.getZooKeeper();
            assertNotNull(z);
            assertNotNull(z.getSessionId());
        } finally {
            if (z != null) {
                z.close();
            }
        }
    }
}