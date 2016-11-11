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

package io.pivotal.cf.service.consumer.app;

import io.pivotal.cf.service.connector.KafkaRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.KafkaDataListener;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;


@Slf4j
@Component
public class KafkaSampleConsumer implements ApplicationListener<ContextRefreshedEvent> {

    private KafkaRepository repo;

    public KafkaSampleConsumer(KafkaRepository repo) {
        this.repo = repo;
    }

    private void consumeMessages() throws ExecutionException, InterruptedException {
        KafkaMessageListenerContainer<Integer, String> container = repo.getConsumer(new ConsumerListener());
        container.start();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        (new Thread() {
            public void run() {
                try {
                    consumeMessages();
                } catch (Exception e) {
                    log.error("Consumer crapped out.", e);
                }
            }
        }).start();

    }
}

@Slf4j
class ConsumerListener implements AcknowledgingMessageListener<Integer, String>, KafkaDataListener<ConsumerRecord<Integer, String>> {

    @Override
    public void onMessage(ConsumerRecord<Integer, String> data, Acknowledgment acknowledgment) {
        log.info("received: " + data);
    }
}