package io.pivotal.cf.service.connector;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaRepositoryFactory {

    public KafkaRepository create(KafkaServiceInfo info) {
        log.info("creating kafkaRepository with info: " + info);

        return new KafkaRepository(info);

    }
}
