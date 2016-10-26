package io.pivotal.cf.service.connector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.service.AbstractServiceConnectorCreator;
import org.springframework.cloud.service.ServiceConnectorConfig;

@Slf4j
public class KafkaConnectionCreator extends AbstractServiceConnectorCreator<KafkaRepository, KafkaServiceInfo> {

    @Override
    public KafkaRepository create(KafkaServiceInfo serviceInfo, ServiceConnectorConfig serviceConnectorConfig) {
        log.debug("creating hello repo wth service info: " + serviceInfo);
        return new KafkaRepositoryFactory().create(serviceInfo);
    }
}