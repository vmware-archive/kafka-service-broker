package io.pivotal.cf.servicebroker;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class KafkaClientTest {

    @Autowired
    private KafkaClient client;

    @Test
    public void testListTopics() {
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

        assertTrue(client.listTopics().contains(topicName));

        client.deleteTopic(topicName);
        TimeUnit.SECONDS.sleep(3);

        assertFalse(client.listTopics().contains(topicName));
    }
}