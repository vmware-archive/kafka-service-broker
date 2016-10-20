package io.pivotal.cf.servicebroker;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Slf4j
public class KafkaClient {

    private Environment env;
    private KafkaTemplate<Integer, String> template;

    public KafkaClient(Environment env, KafkaTemplate<Integer, String> template) {
        this.env = env;
        this.template = template;
    }

    public void sendMessage(String topicName, String message) {
        template.setDefaultTopic(topicName);
        template.sendDefault(message);
    }

    public void deleteTopic(String topicName) {

//        ZkUtils zkUtils = new ZkUtils(getZookeeperClient(), new ZkConnection(env.getProperty("ZOOKEEPER_HOST")), false);
//        AdminUtils.deleteTopic(zkUtils, topicName);

    }
}