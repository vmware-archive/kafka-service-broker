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


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.tomcat.jni.Time;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static junit.framework.TestCase.fail;

// Private class to support the new consumer API in a simple thread model.
//
// Thread will run forever (or until the "lastExpectedMessage" is seen
//  NOTE: The current implementation always reads "newly arrived data",
//      not the data from the beginning of the topic.   So invoking
//      the test against the same topic will simply keep adding data.

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class JointProducerConsumer {

    @Autowired
    private Environment env;

    @Autowired
    private KafkaClient client;

    private static final int NUM_MESSAGES = 3;
    private static final String TOPIC_NAME = "topic" + System.currentTimeMillis();

    @Test
    public void runIt() throws Exception {

        // Assemble properties
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers",client.getBootstrapServers());
        producerProps.put("acks", "0");
        producerProps.put("linger.ms", 0);

        producerProps.put("key.serializer", StringSerializer.class.getName());
        producerProps.put("value.serializer", StringSerializer.class.getName());

        Producer<String, String> producer = new KafkaProducer<>(producerProps);

        System.out.println("creating topic");
        client.createTopic(TOPIC_NAME);

        boolean topicExists = false;
        for (int i = 0; i < 5; i++) {
            if (client.listTopics().contains(TOPIC_NAME)) {
                topicExists = true;
                break;
            } else {
                Time.sleep(5000);
            }
        }

        if (!topicExists) {
            fail("topic: " + TOPIC_NAME + " was not created in a timely fashion");
        }

        System.out.println("Publishing messages (static key)");
        for (int i = 0; i < NUM_MESSAGES; i++) {
            producer.send(new ProducerRecord<>(TOPIC_NAME, "SimpleKey", Integer.toString(i)));
        }

        producer.flush();

        System.out.println(Integer.toString(NUM_MESSAGES) + " messages sent successfully");

        // Create our Consumer sub-tasks.  Use a unique group.id
        // for each topic so that registering with the brokers
        // is a bit cleaner
        final List<Callable<Void>> consumers = new ArrayList<>();
        ConsumerLoop cloop = new ConsumerLoop(0, Arrays.asList(TOPIC_NAME), Arrays.asList(Integer.toString(NUM_MESSAGES - 1)));
        consumers.add(cloop);

        // Create a thread pool to host the consumers
        ExecutorService executor = Executors.newFixedThreadPool(consumers.size());
        executor.invokeAll(consumers);
        System.out.println("All consumers exited");
        executor.shutdown();
        System.exit(0);
    }
}

@Slf4j
class ConsumerLoop implements Callable {

    private final KafkaConsumer<String, String> consumer;
    private final List<String> lastExpectedMessages;
    private final List<String> topics;
    private final int id;

    ConsumerLoop(int id, List<String> topics, List<String> terminationMessages) {
        this.id = id;
        this.topics = topics;
        this.lastExpectedMessages = terminationMessages;
        Properties props = new Properties();
        props.put("bootstrap.servers", "52.207.94.210:9092");
        props.put("group.id", "none");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");
        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public Void call() {
        log.info("ConsumerLoop: call()");
        try {
            Boolean keepPolling = true;

            log.info("ConsumerLoop subscribing to: " + this.topics);

            consumer.subscribe(topics);

            while (keepPolling) {
                log.info("looping......");
                ConsumerRecords<String, String> records = consumer.poll(10000);
                for (ConsumerRecord<String, String> record : records) {
                    log.info("found a record!");
                    Map<String, Object> data = new HashMap<>();
                    data.put("partition", record.partition());
                    data.put("offset", record.offset());
                    data.put("value", record.value());
                    log.info(this.id + ": " + data);

                    for (String topic : topics) {
                        if (record.topic().equals(topic)) {
                            if (lastExpectedMessages.get(topics.indexOf(topic)).equals(record.value())) {
                                keepPolling = false;
                            }
                        }
                    }
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            log.info("Closing consumer");
            consumer.close();
        }

        return null;
    }

    public void shutdown() {
        log.info("ConsumerLoop shutdown()");
        consumer.wakeup();
    }
}