package io.pivotal.cf.servicebroker;

import io.pivotal.cf.servicebroker.model.ServiceBinding;
import io.pivotal.cf.servicebroker.model.ServiceInstance;
import io.pivotal.cf.servicebroker.service.DefaultServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Example service broker. Can be used as a template for creating custom service brokers
 * by adding your code in the appropriate methods. For more information on the CF service broker
 * lifecycle and API, please see See <a href="https://docs.cloudfoundry.org/services/api.html">here.</a>
 * <p>
 * This class extends DefaultServiceImpl, which has no-op implementations of the methods. This means
 * that if, for instance, your broker does not support binding you can just delete the binding methods below
 * (in other words, you do not need to implement your own no-op implementations).
 */
@Service
@Slf4j
public class KafkaBroker extends DefaultServiceImpl {

    public static final String TOPIC_NAME_KEY = "topicName";

    public KafkaBroker(Environment env, KafkaClient client) {
        super();
        this.env = env;
        this.client = client;
    }

    private Environment env;

    private KafkaClient client;

    /**
     * Add code here and it will be run during the create-service process. This might include
     * calling back to your underlying service to create users, schemas, fire up environments, etc.
     *
     * @param instance service instance data passed in by the cloud connector. Clients can pass additional json
     *                 as part of the create-service request, which will show up as key value pairs in instance.parameters.
     * @throws ServiceBrokerException thrown this for any errors during instance creation.
     */
    @Override
    public void createInstance(ServiceInstance instance) throws ServiceBrokerException {

        //TODO use  creds to talk to service?
        //TODO security

        Object name = instance.getParameters().get(TOPIC_NAME_KEY);
        if(name == null) {
            name = instance.getId();
            instance.getParameters().put(TOPIC_NAME_KEY, name);
        }

        log.info("creating topic: " + name.toString());
        client.sendMessage(name.toString(), "creating topic: " + name);
    }

    /**
     * Code here will be called during the delete-service instance process. You can use this to de-allocate resources
     * on your underlying service, delete user accounts, destroy environments, etc.
     *
     * @param instance service instance data passed in by the cloud connector.
     * @throws ServiceBrokerException thrown this for any errors during instance deletion.
     */
    @Override
    public void deleteInstance(ServiceInstance instance) throws ServiceBrokerException {
        //TODO use admin creds to talk to service

        log.info("deprovisioning broker user: " + instance.getId());

        //call out to kafka to delete the topic
        client.deleteTopic(instance.getParameters().get(TOPIC_NAME_KEY).toString());
    }

    /**
     * Code here will be called during the update-service process. You can use this to modify
     * your service instance.
     *
     * @param instance service instance data passed in by the cloud connector.
     * @throws ServiceBrokerException thrown this for any errors during instance deletion. Services that do not support
     *                                updating can through ServiceInstanceUpdateNotSupportedException here.
     */
    @Override
    public void updateInstance(ServiceInstance instance) throws ServiceBrokerException {
        //TODO change user/pw for this instance, use admin creds to talk to service
        log.info("updating broker user: " + instance.getId());

        //future: re-config topics, security settings?

    }

    /**
     * Called during the bind-service process. This is a good time to set up anything on your underlying service specifically
     * needed by an application, such as user accounts, rights and permissions, application-specific environments and connections, etc.
     * <p>
     * Services that do not support binding should set '"bindable": false,' within their catalog.json file. In this case this method
     * can be safely deleted in your implementation.
     *
     * @param instance service instance data passed in by the cloud connector.
     * @param binding  binding data passed in by the cloud connector. Clients can pass additional json
     *                 as part of the bind-service request, which will show up as key value pairs in binding.parameters. Brokers
     *                 can, as part of this method, store any information needed for credentials and unbinding operations as key/value
     *                 pairs in binding.properties
     * @throws ServiceBrokerException thrown this for any errors during binding creation.
     */
    @Override
    public void createBinding(ServiceInstance instance, ServiceBinding binding) throws ServiceBrokerException {
        //TODO use admin creds to talk to service
        // use app guid to send bind request
        //don't need to talk to kafka, just return credentials.
        log.info("binding app: " + binding.getAppGuid() + " to topic: " + instance.getParameters().get(TOPIC_NAME_KEY));
    }

    /**
     * Called during the unbind-service process. This is a good time to destroy any resources, users, connections set up during the bind process.
     *
     * @param instance service instance data passed in by the cloud connector.
     * @param binding  binding data passed in by the cloud connector.
     * @throws ServiceBrokerException thrown this for any errors during the unbinding creation.
     */
    @Override
    public void deleteBinding(ServiceInstance instance, ServiceBinding binding) throws ServiceBrokerException {
        //TODO use admin creds to talk to service
        log.info("unbinding app: " + binding.getAppGuid() + " from topic: " + instance.getParameters().get(TOPIC_NAME_KEY));
    }

    /**
     * Bind credentials that will be returned as the result of a create-binding process. The format and values of these credentials will
     * depend on the nature of the underlying service. For more information and some examples, see
     * <a href=https://docs.cloudfoundry.org/services/binding-credentials.html>here.</a>
     * <p>
     * This method is called after the create-binding method: any information stored in binding.properties in the createBinding call
     * will be availble here, along with any custom data passed in as json parameters as part of the create-binding process by the client.
     *
     * @param instance service instance data passed in by the cloud connector.
     * @param binding  binding data passed in by the cloud connector.
     * @return credentials, as a series of key/value pairs
     * @throws ServiceBrokerException thrown this for any errors during credential creation.
     */
    @Override
    public Map<String, Object> getCredentials(ServiceInstance instance, ServiceBinding binding) throws
            ServiceBrokerException {
        log.info("returning creds.");

//        //producer stuff: more?
//        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
//        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
//        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
//
//        //consumer stuff: more?
//        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
//        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

//        try {
//            User user = (User) binding.getParameters().get("user");
//
            Map<String, Object> m = new HashMap<>();
//            m.put("hostname", env.getProperty("HELLO_HOST"));
//            m.put("port", env.getProperty("HELLO_PORT"));
//            m.put("username", user.getName());
//            m.put("password", user.getPassword());
//
//            String uri = "hello://" + m.get("username") + ":" + m.get("password") + "@" + m.get("hostname") + ":" + m.get("port");
//            m.put("uri", uri);
//
            return m;
//        } catch (Throwable t) {
//            log.error(t.getMessage(), t);
//            throw new ServiceBrokerException(t);
//        }
    }

    @Override
    public boolean isAsync() {
        //TODO deal with async
        return false;
    }
}