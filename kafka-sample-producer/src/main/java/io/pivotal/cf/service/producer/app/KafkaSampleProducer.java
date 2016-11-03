package io.pivotal.cf.service.producer.app;

import io.pivotal.cf.service.connector.KafkaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class KafkaSampleProducer implements ApplicationListener<ContextRefreshedEvent> {

    private KafkaRepository repo;

    public KafkaSampleProducer(KafkaRepository repo) {
        this.repo = repo;
    }


    private void produceMessages() throws ExecutionException, InterruptedException {

        while (true) {
            ListenableFuture<SendResult<Integer, String>> future = repo.sendMessage("hello from the KafkaSampleProducer: " + System.currentTimeMillis());
            try {
                SendResult<Integer, String> result = future.get();
                log.info( result.getProducerRecord().value() + " ....Message .... was sent to Topic " + result.getRecordMetadata().topic());
                TimeUnit.SECONDS.sleep(10);
            } catch (Exception e) {
                log.warn("KafkaSampleProducer interrupted.", e);
                throw e;
            }
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        (new Thread() {
            public void run() {
                try {
                    produceMessages();
                } catch (Exception e) {
                    log.error("Producer crapped out.", e);
                }
            }
        }).start();

    }
}