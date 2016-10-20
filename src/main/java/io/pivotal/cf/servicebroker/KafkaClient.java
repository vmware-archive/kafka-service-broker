package io.pivotal.cf.servicebroker;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Slf4j
public class KafkaClient {

    private Environment env;

    public KafkaClient(Environment env) {
        this.env = env;
    }

    public void sendMessage(String topicName, String message) {
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, env.getProperty("KEY_SERIALIZER_CLASS_CONFIG"));
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, env.getProperty("VALUE_SERIALIZER_CLASS_CONFIG"));
//        configProperties.put("queue.buffering.max.ms", 10);
//        configProperties.put("send.buffer.bytes", 10);
//        configProperties.put("queue.buffering.max.messages", 1);

        Producer<String, String> producer = new KafkaProducer<>(configProperties);

        ProducerRecord<String, String> rec = new ProducerRecord<>(topicName, message);
        producer.send(rec);

//        producer.flush();
    }

//    public void createTopic(String topicName) {
//        Properties configProperties = new Properties();
////        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
////        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, env.getProperty("KEY_SERIALIZER_CLASS_CONFIG"));
////        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, env.getProperty("VALUE_SERIALIZER_CLASS_CONFIG"));
////
////        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, env.getProperty("VALUE_SERIALIZER_CLASS_CONFIG"));
//
//
//        String zoo = "localhost:2181/kafka";
//  /*      ZkClient zkClient = new ZkClient(
//                zoo,
//                10000,
//                8000,
//                ZKStringSerializer$.MODULE$);
//
//        ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zoo), false);
//        AdminUtils.createTopic(zkUtils, topicName, 1, 1, configProperties, RackAwareMode.Enforced$.MODULE$);
////        AdminUtils.addPartitions(zkUtils, topicName, 3, "", true, RackAwareMode.Enforced$.MODULE$);
//    }

}