package io.pivotal.cf.service.connector;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public class KafkaRepository {

    private KafkaServiceInfo info;


    public KafkaRepository(KafkaServiceInfo info) {
        this.info = info;
    }

    private Map<String, Object> senderProperties() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, info.getHost() + ":" + info.getPort());
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return props;
    }

    private KafkaTemplate<Integer, String> getTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(senderProperties()));
    }

    public  ListenableFuture<SendResult<Integer, String>> sendMessage(String message) throws ExecutionException, InterruptedException {
        KafkaTemplate<Integer, String> template = getTemplate();
        ListenableFuture<SendResult<Integer, String>> future = template.send(info.getTopicName(), message);
        template.flush();
        return future;
    }
}