package io.pivotal.cf.servicebroker;

import kafka.admin.TopicCommand;
import kafka.utils.ZkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class KafkaClient {

    private Util util;
    private Environment env;

    public KafkaClient(Util util, Environment env) {
        this.util = util;
        this.env = env;
    }

    void deleteTopic(String topicName) {
        ZkUtils zu = null;
        try {
            zu = util.getUtils();
            TopicCommand.deleteTopic(zu, new TopicCommand.TopicCommandOptions(new String[]{"--topic", topicName}));
        } finally {
            if (zu != null) {
                zu.close();
            }
        }
    }

    void createTopic(String topicName) {
        ZkUtils zu = null;

        try {
            zu = util.getUtils();
            zu.
            TopicCommand.createTopic(zu, new TopicCommand.TopicCommandOptions(new String[]{"--topic", topicName, "--partitions", "1", "--replication-factor", "1", "--zookeeper", env.getProperty("ZOOKEEPER_HOST")}));
        } finally {
            if (zu != null) {
                zu.close();
            }
        }
    }

    List<String> listTopics() {
        ZooKeeper z = null;
        try {
            z = util.getZooKeeper();
            return z.getChildren("/brokers/topics", false);
        } catch (Exception e) {
            throw new KafkaBrokerException(e.getMessage(), e);
        } finally {
            if (z != null) {
                try {
                    z.close();
                } catch (InterruptedException e) {
                    log.warn(e.getMessage());
                }
            }
        }
    }
}