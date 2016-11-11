package io.pivotal.cf.servicebroker;

import kafka.admin.TopicCommand;
import kafka.utils.ZkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import scala.util.parsing.combinator.testing.Str;

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
            TopicCommand.createTopic(zu, new TopicCommand.TopicCommandOptions(new String[]{"--topic", topicName, "--partitions", "1", "--replication-factor", "1", "--zookeeper", env.getProperty("ZOOKEEPER_HOST")}));
        } finally {
            if (zu != null) {
                zu.close();
            }
        }
    }

    List<String> listTopics() throws Exception {
        ZooKeeper z = null;
        try {
            z = util.getZooKeeper();
            return z.getChildren("/brokers/topics", false);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
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

   String getBootstrapServers() throws Exception {

       String bootstrap_servers = util.getBootstrapServers().toString().replaceAll("\\s","");
       bootstrap_servers = bootstrap_servers.substring(1,bootstrap_servers.length()-1);
       return bootstrap_servers;
    }


}