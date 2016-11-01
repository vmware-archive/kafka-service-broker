package io.pivotal.cf.servicebroker;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaSampleProducer {

    static String KAFKA_NODE = "54.175.241.13:9092";

    public void produceMessages() throws Exception {

        //Assign topicName to string variable
        String topicName = "test_topic";
        int numMessages = 3;

        // Assemble properties
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", KAFKA_NODE);
        producerProps.put("acks", "all");
        producerProps.put("retries", 0);
        producerProps.put("batch.size", 16384);
        producerProps.put("linger.ms", 0);
        producerProps.put("buffer.memory", 33554432);

        producerProps.put("key.serializer", StringSerializer.class.getName());
        producerProps.put("value.serializer", StringSerializer.class.getName());

        Producer<String, String> producer = new KafkaProducer<>(producerProps);

        System.out.println("Publishing messages (static key)");
        for (int i = 0; i < numMessages; i++)
            producer.send(new ProducerRecord<>(topicName,
                    "SimpleKey", Integer.toString(i)));

        log.info(Integer.toString(numMessages) + " messages MAY HAVE BEEN successfully....LOOK AT THE CONSUMER");
        producer.flush();
        producer.close();


    }
}