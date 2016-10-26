package io.pivotal.cf.servicebroker;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Private class to support the new consumer API in a simple thread model.
//
// Thread will run forever (or until the "lastExpectedMessage" is seen
//  NOTE: The current implementation always reads "newly arrived data",
//      not the data from the beginning of the topic.   So invoking
//      the test against the same topic will simply keep adding data.
class ConsumerLoop implements Callable {
    private final KafkaConsumer<String, String> consumer;
    private final List<String> lastExpectedMessages;
    private final List<String> topics;
    private final String groupId;
    private final int id;

    public ConsumerLoop(int id,
                        String groupId,
                        List<String> topics,
                        List<String> terminationMessages) {
        this.id = id;
        this.groupId = groupId;
        this.topics = topics;
        this.lastExpectedMessages = terminationMessages;
        Properties props = new Properties();
        props.put("bootstrap.servers", "54.242.77.150:9092");
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public Void call() {
        System.out.println("ConsumerLoop: call()");
        try {
            Boolean keepPolling = true;

            System.out.printf("ConsumerLoop %d subscribing to %s (group id %s)\n",
                    this.id, topics, groupId);

            consumer.subscribe(topics);

            while (keepPolling) {
                ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
                for (ConsumerRecord<String, String> record : records) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("partition", record.partition());
                    data.put("offset", record.offset());
                    data.put("value", record.value());
                    System.out.println(this.id + ": " + data);

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
            System.out.println("Closing consumer");
            consumer.close();
        }

        return null;
    }

    public void shutdown() {
        System.out.println("ConsumerLoop shutdown()");
        consumer.wakeup();
    }
}


/**
 * Hello world!
 */
public class JointProducerConsumer {
    public static void main(String[] args) throws Exception {

        // Check arguments length value
        if (args.length == 0) {
            System.out.println("usage: JointProducerConsumer <topic> [ <numMessages> ]");
            return;
        }

        //Assign topicName to string variable
        String topicName = args[0];
        int numMessages = 10;

        if (args.length == 2) {
            int newMessages = Integer.parseInt(args[1]);
            if (newMessages > 0) numMessages = newMessages;
        }

        // Assemble properties
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", "54.242.77.150:9092");
        producerProps.put("acks", "all");
        producerProps.put("retries", 0);
        producerProps.put("batch.size", 16384);
        producerProps.put("linger.ms", 1);
        producerProps.put("buffer.memory", 33554432);

        producerProps.put("key.serializer", StringSerializer.class.getName());
        producerProps.put("value.serializer", StringSerializer.class.getName());

        Producer<String, String> producer = new KafkaProducer
                <String, String>(producerProps);

        System.out.println("Publishing messages (static key)");
        for (int i = 0; i < numMessages; i++)
            producer.send(new ProducerRecord<String, String>(topicName,
                    "SimpleKey", Integer.toString(i)));

        System.out.println(Integer.toString(numMessages) + " messages sent successfully");
        producer.flush();

        // Create our Consumer sub-tasks.  Use a unique group.id
        // for each topic so that registering with the brokers
        // is a bit cleaner
        final List<Callable<Void>> consumers = new ArrayList<>();
        ConsumerLoop cloop = new ConsumerLoop(0,
                topicName + "Test",
                //topicName,
                Arrays.asList(topicName),
                Arrays.asList(Integer.toString(numMessages - 1)));
        consumers.add(cloop);

        // Create a thread pool to host the consumers
        ExecutorService executor = Executors.newFixedThreadPool(consumers.size());
        executor.invokeAll(consumers);
        System.out.println("All consumers exited");
        executor.shutdown();
        System.exit(0);
    }
}
