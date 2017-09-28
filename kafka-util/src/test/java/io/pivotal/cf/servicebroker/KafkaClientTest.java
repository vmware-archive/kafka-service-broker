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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class KafkaClientTest {

    private KafkaClient client;

    @Autowired
    private Util util;

    @Before
    public void setUp() {
        client = new KafkaClient(util);
    }

    @Test
    public void testGetBoots() throws Exception {
        List<String> l = new ArrayList<>();
        l.add("123.456.789.10:1234 ");
        l.add("234.456.789.10:2345 ");
        l.add("234.456.789.10:2345 ");
        when(util.getBootstrapServers()).thenReturn(l);
        String s = client.getBootstrapServers();
        assertNotNull(s);
        assertEquals("123.456.789.10:1234,234.456.789.10:2345,234.456.789.10:2345", s);
    }
}