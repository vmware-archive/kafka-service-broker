package io.pivotal.cf.service.connector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;

import java.util.Map;

@Slf4j
public class KafkaServiceInfoCreator extends CloudFoundryServiceInfoCreator<KafkaServiceInfo> {

    public KafkaServiceInfoCreator() {
        super(new Tags(KafkaServiceInfo.URI_SCHEME), KafkaServiceInfo.URI_SCHEME);
    }

    @Override
    public KafkaServiceInfo createServiceInfo(Map<String, Object> serviceData) {
        log.info("Returning kafka service info: " + serviceData.toString());

        Map<String, Object> credentials = getCredentials(serviceData);
        String id = getId(serviceData);
        String host = credentials.get("hostname").toString();
        String port = credentials.get("port").toString();
        String uri = credentials.get("uri").toString();
        String topicName = credentials.get("topicName").toString();

        return new KafkaServiceInfo(id, host, port, uri, "0", "org.apache.kafka.common.serialization.IntegerSerializer",
                "org.apache.kafka.common.serialization.StringSerializer", topicName);
    }
}