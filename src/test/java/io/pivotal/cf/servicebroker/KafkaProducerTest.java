package io.pivotal.cf.servicebroker;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KafkaProducerTest {

    @Autowired
    private KafkaClient client;

    @Test
    public void testProducerConsumer() throws ServiceBrokerException {

        String message = "hello-" + +System.currentTimeMillis();
        String topicName = "pivotal-" + System.currentTimeMillis();

//        String topicName = "pivotal1234";
        ArrayList h = new ArrayList<>();
        h.add(topicName);

        Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "who");




//        Consumer<String, String> consumer = new KafkaConsumer<>(configProperties);
//
//        LongRunningConsumer lrc = new LongRunningConsumer(1, "test",h);
//
//        new Thread(lrc).run();





//        client.createTopic(topicName);
        //client.sendMessage(topicName, "creating topic");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configProperties);
        consumer.subscribe(h);



        client.sendMessage(topicName, message);

        ConsumerRecords<String, String> records = consumer.poll(1000);

        assertNotNull(records);
        assertTrue(records.count() > 0);
        assertEquals(message, records.iterator().next().value());

        consumer.close();
    }
}