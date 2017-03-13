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

package io.pivotal.cf.service.producer.app;

import io.pivotal.cf.service.connector.KafkaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class KafkaSampleProducer {

    private KafkaRepository repo;

    public KafkaSampleProducer(KafkaRepository repo) throws Exception {
        this.repo = repo;
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 10000)
    public void produceMessage() {
        log.info("sending message: the time is now {}", dateFormat.format(new Date()));
        try {
            repo.sendMessage("hello from the KafkaSampleProducer: " + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("KafkaSampleProducer interrupted.", e);
        }
    }
}