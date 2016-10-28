package io.pivotal.cf.service.producer.app;

import io.pivotal.cf.servicebroker.KafkaSampleProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        KafkaSampleProducer kp = new KafkaSampleProducer();
        kp.produceMessages();
        SpringApplication.run(Application.class, args);
    }

}