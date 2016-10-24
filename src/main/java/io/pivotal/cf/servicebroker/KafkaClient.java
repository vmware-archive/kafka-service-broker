package io.pivotal.cf.servicebroker;

import kafka.admin.TopicCommand;
import kafka.utils.ZkUtils;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
class KafkaClient {

    private KafkaTemplate<Integer, String> template;
    private Environment env;

    public KafkaClient(KafkaTemplate<Integer, String> template, Environment env) {
        this.template = template;
        this.env = env;
    }

    void sendMessage(String topicName, String message) {
        template.setDefaultTopic(topicName);
        template.sendDefault(message);
        template.flush();
    }

    void createTopic(String topicName) {
        sendMessage(topicName, "creating topic");
    }

    void deleteTopic(String topicName) {
        ZkUtils zu = null;

        try {
            ZkConnection con = new ZkConnection(env.getProperty("ZOOKEEPER_HOST"), Integer.parseInt(env.getProperty("ZOOKEEPER_TIMEOUT")));
            ZkClient zc = new ZkClient(con);
            zu = new ZkUtils(zc, con, false);
            TopicCommand.deleteTopic(zu, new TopicCommand.TopicCommandOptions(new String[]{"--topic", topicName}));
        } finally {
            if (zu != null) {
                zu.close();
            }
        }
    }

    List<String> listTopics() {
        ZooKeeper z = null;
        try {
            z = zooKeeper();
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

    private ZooKeeper zooKeeper() throws IOException {
        return new ZooKeeper(env.getProperty("ZOOKEEPER_HOST"), Integer.parseInt(env.getProperty("ZOOKEEPER_TIMEOUT")), new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                log.info("watching: " + event.toString());
            }
        });
    }
}