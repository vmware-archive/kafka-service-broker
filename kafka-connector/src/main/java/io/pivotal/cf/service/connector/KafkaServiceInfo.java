package io.pivotal.cf.service.connector;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.cloud.service.ServiceInfo;

@Data
@AllArgsConstructor
public class KafkaServiceInfo implements ServiceInfo {

    static final String URI_SCHEME = "kafka";

    private String id;
    private String hosts;
    private String uri;
    private String retriesConfig;
    private String keySerializerClassConfig;
    private String valueSerializerClassConfig;
    private String topicName;

}


