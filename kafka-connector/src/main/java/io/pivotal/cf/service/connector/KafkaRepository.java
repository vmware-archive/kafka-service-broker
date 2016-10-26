package io.pivotal.cf.service.connector;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class KafkaRepository {

    private KafkaServiceInfo info;
    private KafkaConsumer<String, String> consumer;


    public KafkaRepository(KafkaServiceInfo info) throws InterruptedException {
        this.info = info;
        hostConsumers();
    }

    public KafkaTemplate<Integer, String> template() throws ClassNotFoundException {
        ProducerFactory<Integer, String> pf =
                new DefaultKafkaProducerFactory<>(senderProperties());
        return new KafkaTemplate<>(pf);
    }

    private Map<String, Object> senderProperties() throws ClassNotFoundException {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, info.getHost() + ":" + info.getPort());
        props.put(ProducerConfig.RETRIES_CONFIG, info.getRetriesConfig());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Class.forName(info.getKeySerializerClassConfig()));
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Class.forName(info.getValueSerializerClassConfig()));
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        return props;
    }

    public void sendMessage(String message) throws ClassNotFoundException {

        Producer<String, String> producer = new KafkaProducer<>(senderProperties());
        System.out.println("Publishing messages (static key)");
        producer.send(new ProducerRecord<String, String>(info.getTopicName(), "SimpleKey", message));
        System.out.println(message + " .....messages sent successfully");
        producer.flush();
    }

    public void hostConsumers() throws InterruptedException {
        (new Thread(new KafkaMessageConsumer(this.info))).start();
        System.out.println("All consumers exited");
    }
}