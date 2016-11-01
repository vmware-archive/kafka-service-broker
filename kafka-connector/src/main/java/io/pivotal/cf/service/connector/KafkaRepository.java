package io.pivotal.cf.service.connector;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;

@Slf4j
public class KafkaRepository {

    private KafkaServiceInfo info;


    public KafkaRepository(KafkaServiceInfo info) throws InterruptedException {
        this.info = info;
    }

    private Properties senderProperties() throws ClassNotFoundException {
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", info.getHost() + ":" + info.getPort());

        producerProps.put("acks", "all");
        producerProps.put("linger.ms", 0);

        producerProps.put("key.serializer", StringSerializer.class.getName());
        producerProps.put("value.serializer", StringSerializer.class.getName());

        return producerProps;

    }

    public void sendMessage(String message) throws Exception {

        Producer<String, String> producer = new KafkaProducer<>(senderProperties());
        log.info("Publishing messages to topic:  " + info.getTopicName());
        Future<RecordMetadata> f = producer.send(new ProducerRecord<>(info.getTopicName(), message));

        producer.flush();
        RecordMetadata m = f.get();
        log.info("message record: " + m.toString());
        producer.close();

        log.info(message + " .....message may have been sent successfully");
    }
}