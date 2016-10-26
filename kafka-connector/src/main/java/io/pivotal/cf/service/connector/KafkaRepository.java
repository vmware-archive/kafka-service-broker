package io.pivotal.cf.service.connector;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

public class KafkaRepository {

    private KafkaServiceInfo info;

    public KafkaRepository(KafkaServiceInfo info) {
        this.info = info;
    }

    public KafkaTemplate<Integer, String> template() throws ClassNotFoundException {
        ProducerFactory<Integer, String> pf =
                new DefaultKafkaProducerFactory<>(senderProperties());
        return new KafkaTemplate<>(pf);
    }

    private Map<String, Object> senderProperties() throws ClassNotFoundException {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, info.getHost() + ":" + info.getPort());
        props.put(ProducerConfig.RETRIES_CONFIG, info.getRetriesConfig());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Class.forName(info.getKeySerializerClassConfig()));
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Class.forName(info.getValueSerializerClassConfig()));
        return props;
    }

}