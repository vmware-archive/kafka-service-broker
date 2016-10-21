package io.pivotal.cf.servicebroker;

import io.pivotal.cf.servicebroker.model.ServiceBinding;
import io.pivotal.cf.servicebroker.model.ServiceInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Configuration
@PropertySource("classpath:application.properties")
@Slf4j
class TestConfig {

    static final String SI_ID = "siId";
    private static final String SB_ID = "sbId";

    private static final String SD_ID = "aUniqueId";
    private static final String PLAN_ID = "anotherUniqueId";
    private static final String APP_GUID = "anAppGuid";
    private static final String ORG_GUID = "anOrgGuid";
    private static final String SPACE_GUID = "aSpaceGuid";

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

    @Bean
    public ServiceInstance serviceInstance(CreateServiceInstanceRequest req) {
        return new ServiceInstance(req);
    }

    private Map<String, Object> getBindResources() {
        Map<String, Object> m = new HashMap<>();
        m.put("app_guid", APP_GUID);
        return m;
    }

    @Bean
    public CreateServiceInstanceRequest createServiceInstanceRequest() {
        CreateServiceInstanceRequest req = new CreateServiceInstanceRequest(SD_ID, PLAN_ID, ORG_GUID, SPACE_GUID, getParameters());
        req.withServiceInstanceId(SI_ID);
        return req;
    }

    private Map<String, Object> getParameters() {
        Map<String, Object> m = new HashMap<>();
        m.put("foo", "bar");
        m.put("bizz", "bazz");
        return m;
    }

    @Bean
    public ServiceBinding serviceBinding(CreateServiceInstanceBindingRequest req) {
        return new ServiceBinding(req);
    }

    @Bean
    public CreateServiceInstanceBindingRequest createBindingRequest() {
        CreateServiceInstanceBindingRequest req = new CreateServiceInstanceBindingRequest(SD_ID, PLAN_ID, APP_GUID,
                getBindResources(), getParameters());
        req.withBindingId(SB_ID);
        req.withServiceInstanceId(SI_ID);
        return req;
    }

    @Bean
    public ZooKeeper zooKeeper() throws IOException {
        return new ZooKeeper(env.getProperty("ZOOKEEPER_HOST"), Integer.parseInt(env.getProperty("ZOOKEEPER_TIMEOUT")), new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                log.info("watching: " + event.toString());
            }
        });
    }

//    @Bean
//    public ZkUtils zkUtils() {
//        ZkClient zkClient = new ZkClient(
//                env.getProperty("ZOOKEEPER_HOST"),
//                10000,
//                8000,
//                ZKStringSerializer$.MODULE$);
//
//        return new ZkUtils(zkClient, new ZkConnection(env.getProperty("ZOOKEEPER_HOST")), false);
//    }
}