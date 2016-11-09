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

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

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
            assertEquals("52.207.94.210:2181", s);
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