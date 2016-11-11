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

package io.pivotal.cf.service.connector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;

import java.util.Map;

@Slf4j
public class KafkaServiceInfoCreator extends CloudFoundryServiceInfoCreator<KafkaServiceInfo> {

    public KafkaServiceInfoCreator() {
        super(new Tags(KafkaServiceInfo.URI_SCHEME), KafkaServiceInfo.URI_SCHEME);
    }

    @Override
    public KafkaServiceInfo createServiceInfo(Map<String, Object> serviceData) {
        log.info("Returning kafka service info: " + serviceData.toString());

        Map<String, Object> credentials = getCredentials(serviceData);
        String id = getId(serviceData);
        String hosts = credentials.get("hosts").toString();
        String uri = credentials.get("uri").toString();
        String topicName = credentials.get("topicName").toString();

        return new KafkaServiceInfo(id, hosts, uri, "0", "org.apache.kafka.common.serialization.IntegerSerializer",
                "org.apache.kafka.common.serialization.StringSerializer", topicName);
    }
}