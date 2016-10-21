package io.pivotal.cf.servicebroker;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class KafkaClientTest {

    @Autowired
    private KafkaTemplate<Integer, String> template;

    @Autowired
    private KafkaMessageListenerContainer<Integer, String> container;

    @Autowired
    private KafkaClient client;

    @Autowired
    private CountDownLatch latch;

    @Test
    public void testProducerConsumer() throws ServiceBrokerException, InterruptedException {
        log.info("Start auto");
        container.setBeanName("testAuto");
        container.start();
        Thread.sleep(1000); // wait a bit for the container to start

        client.sendMessage("topic1", "foo");
        client.sendMessage("topic1", "bar");
        client.sendMessage("topic1", "baz");
        client.sendMessage("topic1", "qux");

        assertTrue(latch.await(60, TimeUnit.SECONDS));
        container.stop();
        log.info("Stop auto");
    }

    @Test
    public void testListTopics() throws InterruptedException, IOException, KeeperException {
        List<String> s = client.listTopics();
        assertNotNull(s);
        assertTrue(s.size() > 0);
    }
}