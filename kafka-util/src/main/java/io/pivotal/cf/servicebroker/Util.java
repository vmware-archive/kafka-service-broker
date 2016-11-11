/**
 Copyright (C) 2016-Present Pivotal Software, Inc. All rights reserved.

 This program and the accompanying materials are made available under
 the terms of the under the Apache License, Version 2.0 (the "License”);
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

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