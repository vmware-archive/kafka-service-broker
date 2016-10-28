package io.pivotal.cf.service.consumer.app;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

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

    //static String KAFKA_NODE = "107.23.42.75:9092";
    static String KAFKA_NODE = "54.175.241.13:9092";

    public ConsumerLoop(int id,
                        String groupId,
                        List<String> topics,
                        List<String> terminationMessages) {
        this.id = id;
        this.groupId = groupId;
        this.topics = topics;
        this.lastExpectedMessages = terminationMessages;
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_NODE);
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

public class KafkaSampleConsumer {


    public void consumeMessages() throws Exception {

        //Assign topicName to string variable
        String topicName = "test_topic";

        int numMessages = 3;


        // Create our Consumer sub-tasks.  Use a unique group.id
        // for each topic so that registering with the brokers
        // is a bit cleaner
        final List<Callable<Void>> consumers = new ArrayList<>();
        ConsumerLoop cloop = new ConsumerLoop(0, topicName, Arrays.asList(topicName), Arrays.asList(Integer.toString(numMessages - 1)));
        consumers.add(cloop);

        // Create a thread pool to host the consumers
        ExecutorService executor = Executors.newFixedThreadPool(consumers.size());
        executor.invokeAll(consumers);
        System.out.println("All consumers exited");
        executor.shutdown();
        System.exit(0);
    }
}
