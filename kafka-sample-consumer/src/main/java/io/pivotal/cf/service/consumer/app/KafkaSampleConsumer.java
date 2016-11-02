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

    @Autowired
    private KafkaRepository repo;

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
        //acknowledgment.acknowledge();
    }
}