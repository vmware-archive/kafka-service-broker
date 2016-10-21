package io.pivotal.cf.servicebroker;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Slf4j
public class KafkaSpringTest {

    @Autowired
    private KafkaTemplate<Integer, String> template;

    @Autowired
    private KafkaMessageListenerContainer<Integer, String> container;

    @Autowired
    private CountDownLatch latch;

    @Test
    public void testProducerConsumer() throws ServiceBrokerException, InterruptedException {
        log.info("Start auto");
        container.setBeanName("testAuto");
        container.start();
        Thread.sleep(1000); // wait a bit for the container to start
        template.setDefaultTopic("topic1");
        template.sendDefault("foo");
        template.sendDefault("bar");
        template.sendDefault("baz");
        template.sendDefault(2, "qux");
        template.flush();
        assertTrue(latch.await(60, TimeUnit.SECONDS));
        container.stop();
        log.info("Stop auto");
    }
}