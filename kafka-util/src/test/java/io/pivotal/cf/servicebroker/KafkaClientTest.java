package io.pivotal.cf.servicebroker;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class KafkaClientTest {

    @Autowired
    private KafkaClient client;

    @Autowired
    private Util util;


    @Test
    public void testListTopics() throws Exception {

        List<String> listOfTopics = new ArrayList<>();
        listOfTopics.add("foo");

//        given(this.client.listTopics())
//                .willReturn(listOfTopics);

        List<String> s = client.listTopics();
        assertNotNull(s);
        assertTrue(s.size() > 0);
    }

    @Test
    public void testCreateAndDeleteTopic() throws Exception {


        String topicName = "topic" + System.currentTimeMillis();
        assertFalse(client.listTopics().contains(topicName));

        client.createTopic(topicName);
        TimeUnit.SECONDS.sleep(3);

//        List<String> listOfTopics = new ArrayList<>();
//        listOfTopics.add(topicName);

//        given(this.client.listTopics()).willReturn(listOfTopics);

        assertTrue(client.listTopics().contains(topicName));

        client.deleteTopic(topicName);
        TimeUnit.SECONDS.sleep(3);
//        listOfTopics.remove(topicName);

        //assertFalse(client.listTopics().contains(topicName));
    }

    @Test
    public void testGetBootstraps() throws Exception {
        String s = client.getBootstrapServers();
        assertNotNull(s);
        assertEquals("54.86.225.103:9092,54.87.26.5:9092",s);
    }


}