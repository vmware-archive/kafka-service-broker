package io.pivotal.cf.service.consumer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {

        KafkaSampleConsumer kc = new KafkaSampleConsumer();
        kc.consumeMessages();
        SpringApplication.run(Application.class, args);
    }

}