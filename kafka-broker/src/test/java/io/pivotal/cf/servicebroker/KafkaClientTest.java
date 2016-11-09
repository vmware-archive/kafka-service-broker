package io.pivotal.cf.servicebroker;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class KafkaClientTest {

    @Autowired
    private KafkaClient client;


    @Test
    public void testListTopics() {

        List<String> listOfTopics = new ArrayList<>();
        listOfTopics.add("foo");

//        given(this.client.listTopics())
//                .willReturn(listOfTopics);

        List<String> s = client.listTopics();
        assertNotNull(s);
        assertTrue(s.size() > 0);
    }

    @Test
    public void testCreateAndDeleteTopic() throws InterruptedException {


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

        assertFalse(client.listTopics().contains(topicName));
    }

    @Test
    public void testGetBootstraps() throws Exception {
        List<String> s = client.getBootstrapServers();
        assertNotNull(s);
        assertTrue(s.size() > 0);
    }
}