/**
 * Copyright (C) 2016-Present Pivotal Software, Inc. All rights reserved.
 * <p>
 * This program and the accompanying materials are made available under
 * the terms of the under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.cf.servicebroker;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import scala.collection.JavaConversions;

import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class KafkaClient {

    private Util util;

    public KafkaClient(Util util) {
        this.util = util;
    }

    void deleteTopic(String topicName) {
        ZkUtils zu = null;
        try {
            zu = util.getUtils();
            AdminUtils.deleteTopic(zu, topicName);
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
            AdminUtils.createTopic(zu, topicName, 2, 3, new Properties(), RackAwareMode.Disabled$.MODULE$);
        } finally {
            if (zu != null) {
                zu.close();
            }
        }
    }

    List<String> listTopics() throws Exception {
        ZkUtils zu = null;
        try {
            zu = util.getUtils();
            return JavaConversions.asJavaList(zu.getAllTopics());
        } finally {
            if (zu != null) {
                zu.close();
            }
        }
    }

   String getBootstrapServers() throws Exception {
       String bootstrap_servers = util.getBootstrapServers().toString().replaceAll("\\s","");
       bootstrap_servers = bootstrap_servers.substring(1,bootstrap_servers.length()-1);
       return bootstrap_servers;
    }
}