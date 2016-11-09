package io.pivotal.cf.servicebroker;

import kafka.admin.TopicCommand;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KafkaClient {


    private Environment env;

    public KafkaClient(Environment env) {
        this.env = env;
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

    void createTopic(String topicName) {
        ZkUtils zu = null;


        try {
            ZkConnection con = new ZkConnection(env.getProperty("ZOOKEEPER_HOST"), Integer.parseInt(env.getProperty("ZOOKEEPER_TIMEOUT")));
            ZkClient zc = new ZkClient(con);
            ZkSerializer zs = ZKStringSerializer$.MODULE$;
            zc.setZkSerializer(zs);
            zu = new ZkUtils(zc, con, false);
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
        return new ZooKeeper(env.getProperty("ZOOKEEPER_HOST"), Integer.parseInt(env.getProperty("ZOOKEEPER_TIMEOUT")), new Watcher(){
            @Override
            public void process(WatchedEvent event) {
                log.info("watching: " + event.toString());
            }
        });
    }

    public List<String> getBootstrapServers() throws Exception {
        List<String> ret = new ArrayList<>();
        ZooKeeper zk = zooKeeper();

        List<String> ids = zk.getChildren("/brokers/ids", false);
        for (String id : ids) {
            String brokerInfo = new String(zk.getData("/brokers/ids/" + id, false, null));
            Map m = toMap(brokerInfo);
            ret.add(m.get("host") + ":" + m.get("port"));
        }

        return ret;
    }

    private Map toMap(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Map.class);
    }
}