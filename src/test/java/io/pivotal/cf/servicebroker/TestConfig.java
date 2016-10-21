package io.pivotal.cf.servicebroker;

import io.pivotal.cf.servicebroker.model.ServiceBinding;
import io.pivotal.cf.servicebroker.model.ServiceInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Configuration
@PropertySource("classpath:application.properties")
@Slf4j
class TestConfig {

    @Autowired
    private Environment env;

    @Bean
    public KafkaTemplate<Integer, String> template() throws ClassNotFoundException {
        ProducerFactory<Integer, String> pf =
                new DefaultKafkaProducerFactory<>(senderProperties());
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    private Map<String, Object> senderProperties() throws ClassNotFoundException {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
        props.put(ProducerConfig.RETRIES_CONFIG, env.getProperty("RETRIES_CONFIG"));
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, env.getProperty("BATCH_SIZE_CONFIG"));
        props.put(ProducerConfig.LINGER_MS_CONFIG, env.getProperty("LINGER_MS_CONFIG"));
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, env.getProperty("BUFFER_MEMORY_CONFIG"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Class.forName(env.getProperty("KEY_SERIALIZER_CLASS_CONFIG")));
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Class.forName(env.getProperty("VALUE_SERIALIZER_CLASS_CONFIG")));
        return props;
    }

    @Bean
    public CountDownLatch latch() {
        return new CountDownLatch(4);
    }

    @Bean
    public KafkaMessageListenerContainer<Integer, String> container() throws ClassNotFoundException {
        ContainerProperties containerProperties = new ContainerProperties("topic1", "topic2");

        Map<String, Object> props = consumerProps();
        DefaultKafkaConsumerFactory<Integer, String> cf =
                new DefaultKafkaConsumerFactory<>(props);
        KafkaMessageListenerContainer<Integer, String> container =
                new KafkaMessageListenerContainer<>(cf, containerProperties);

        containerProperties.setMessageListener(new MessageListener<Integer, String>() {

            @Override
            public void onMessage(ConsumerRecord<Integer, String> message) {
                log.info("received: " + message);
                latch().countDown();
            }

        });

        return container;
    }

    private Map<String, Object> consumerProps() throws ClassNotFoundException {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("GROUP_ID_CONFIG"));
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, env.getProperty("ENABLE_AUTO_COMMIT_CONFIG"));
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, env.getProperty("AUTO_COMMIT_INTERVAL_MS_CONFIG"));
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, env.getProperty("SESSION_TIMEOUT_MS_CONFIG"));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Class.forName(env.getProperty("KEY_DESERIALIZER_CLASS_CONFIG")));
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Class.forName(env.getProperty("VALUE_DESERIALIZER_CLASS_CONFIG")));
        return props;
    }

    @MockBean
    private RedisTemplate<String, ServiceInstance> instanceTemplate;

    @MockBean
    private RedisTemplate<String, ServiceBinding> bindingTemplate;
}