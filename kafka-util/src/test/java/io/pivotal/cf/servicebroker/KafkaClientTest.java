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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Ignore
public class KafkaClientTest {

    @Autowired
    private KafkaClient client;

    @Autowired
    private Util util;


    @Test
    public void testListTopics() throws Exception {

        List<String> listOfTopics = new ArrayList<>();
        listOfTopics.add("foo");

//        given(this.client.listTopics())
//                .willReturn(listOfTopics);

        List<String> s = client.listTopics();
        assertNotNull(s);
        assertTrue(s.size() > 0);
    }

    @Test
    public void testCreateAndDeleteTopic() throws Exception {


        String topicName = "topic" + System.currentTimeMillis();
        assertFalse(client.listTopics().contains(topicName));

        client.createTopic(topicName);
        TimeUnit.SECONDS.sleep(3);

//        List<String> listOfTopics = new ArrayList<>();
//        listOfTopics.add(topicName);

//        given(this.client.listTopics()).willReturn(listOfTopics);

        assertTrue(client.listTopics().contains(topicName));

        client.deleteTopic(topicName);
        TimeUnit.SECONDS.sleep(3);
//        listOfTopics.remove(topicName);

        //assertFalse(client.listTopics().contains(topicName));
    }

    @Test
    public void testGetBootstraps() throws Exception {
        String s = client.getBootstrapServers();
        assertNotNull(s);
        assertEquals("54.86.225.103:9092,54.87.26.5:9092",s);
    }


}