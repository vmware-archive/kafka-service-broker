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

import io.pivotal.cf.servicebroker.model.ServiceBinding;
import io.pivotal.cf.servicebroker.model.ServiceInstance;
import io.pivotal.cf.servicebroker.service.BrokeredService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest

public class KafkaBrokerTest {

    @Autowired
    private BrokeredService kafkaBroker;

    @Autowired
    private ServiceInstance serviceInstance;

    @Autowired
    private ServiceBinding serviceBinding;

    @Test
    public void testCreateAndDeleteInstance() throws ServiceBrokerException {
        serviceInstance.getParameters().put(KafkaBroker.TOPIC_NAME_KEY, "myTopic");
        Object o = serviceInstance.getParameters().get(KafkaBroker.TOPIC_NAME_KEY);
        assertNotNull(o);
        assertEquals("myTopic", o.toString());

        String topicName = "topic" + System.currentTimeMillis();
        serviceInstance.getParameters().put(KafkaBroker.TOPIC_NAME_KEY, topicName);

        kafkaBroker.createInstance(serviceInstance);
        o = serviceInstance.getParameters().get(KafkaBroker.TOPIC_NAME_KEY);
        assertNotNull(o);
        assertEquals(topicName, o.toString());

        kafkaBroker.deleteInstance(serviceInstance);
    }

    @Test
    public void testUpdateInstance() throws ServiceBrokerException {
        kafkaBroker.updateInstance(serviceInstance);
    }

    @Test
    public void testCreateBinding() {
        kafkaBroker.createBinding(serviceInstance, serviceBinding);
    }

    @Test
    public void testDeleteBinding() {
        kafkaBroker.deleteBinding(serviceInstance, serviceBinding);
    }

    @Test
    public void testGetCredentials() {
        serviceInstance.getParameters().put(KafkaBroker.TOPIC_NAME_KEY, "foo");
        Map<String, Object> m = kafkaBroker.getCredentials(serviceInstance, serviceBinding);
        assertNotNull(m);
        assertEquals("54.86.225.103:9092,54.87.26.5:9092", m.get("hostname")); //change this to your actual hostname
        assertEquals("kafka://54.86.225.103:9092,54.87.26.5:9092/foo", m.get("uri"));   //change to kafka's hostname and kafka's port, default is 9092
        assertEquals("foo", m.get(KafkaBroker.TOPIC_NAME_KEY));  //this assumes you have a topic with the name foo
    }

    @Test
    public void testAsync() {
        assertFalse(kafkaBroker.isAsync());
    }
}