package io.pivotal.cf.servicebroker;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
class KafkaClient {

    private KafkaTemplate<Integer, String> template;
    private ZooKeeper zooKeeper;

    public KafkaClient(KafkaTemplate<Integer, String> template, ZooKeeper zooKeeper) {
        this.template = template;
        this.zooKeeper = zooKeeper;
    }

    void sendMessage(String topicName, String message) {
        template.setDefaultTopic(topicName);
        template.sendDefault(message);
        template.flush();
    }

    void deleteTopic(String topicName) {
//        AdminUtils.deleteTopic(zkUtils, topicName);
    }


    List<String> listTopics() throws IOException, KeeperException, InterruptedException {
        return zooKeeper.getChildren("/brokers/topics", false);
    }
}