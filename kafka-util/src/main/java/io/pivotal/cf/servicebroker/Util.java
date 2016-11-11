package io.pivotal.cf.servicebroker;

import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
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
class Util {

    private Environment env;

    Util(Environment env) {
        this.env = env;
    }

    ZkConnection getConnection() {
        return new ZkConnection(env.getProperty("ZOOKEEPER_HOST"), Integer.parseInt(env.getProperty("ZOOKEEPER_TIMEOUT")));
    }

    ZkClient getClient() {
        ZkClient zc = new ZkClient(getConnection());
        zc.setZkSerializer(ZKStringSerializer$.MODULE$);
        return zc;
    }

    ZkUtils getUtils() {
        return new ZkUtils(getClient(), getConnection(), false);
    }

    ZooKeeper getZooKeeper() throws IOException {
        return new ZooKeeper(env.getProperty("ZOOKEEPER_HOST"), Integer.parseInt(env.getProperty("ZOOKEEPER_TIMEOUT")), new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                log.info("watching: " + event.toString());
            }
        });
    }

    List<String> getBootstrapServers() throws Exception {
        List<String> ret = new ArrayList<>();
        ZooKeeper zk = getZooKeeper();

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