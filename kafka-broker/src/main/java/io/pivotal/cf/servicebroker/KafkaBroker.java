package io.pivotal.cf.servicebroker;

import io.pivotal.cf.servicebroker.model.ServiceBinding;
import io.pivotal.cf.servicebroker.model.ServiceInstance;
import io.pivotal.cf.servicebroker.service.DefaultServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
class KafkaBroker extends DefaultServiceImpl {

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
     */
    @Override
    public void createInstance(ServiceInstance instance) {

        try {
            Object name = instance.getParameters().get(TOPIC_NAME_KEY);
            if (name == null) {
                name = instance.getId();
                instance.getParameters().put(TOPIC_NAME_KEY, name);
            }

            log.info("creating topic: " + name.toString());
            client.createTopic(name.toString());
        } catch (Throwable throwable) {
            throw new KafkaBrokerException(throwable);
        }
    }

    /**
     * Code here will be called during the delete-service instance process. You can use this to de-allocate resources
     * on your underlying service, delete user accounts, destroy environments, etc.
     *
     * @param instance service instance data passed in by the cloud connector.
     */
    @Override
    public void deleteInstance(ServiceInstance instance) {
        try {
            log.info("de-provisioning service instance which is a kafka topic: " + instance.getId());

            //call out to kafka to delete the topic
            client.deleteTopic(instance.getParameters().get(TOPIC_NAME_KEY).toString());
        } catch (Throwable throwable) {
            throw new KafkaBrokerException(throwable);
        }
    }

    /**
     * Code here will be called during the update-service process. You can use this to modify
     * your service instance.
     *
     * @param instance service instance data passed in by the cloud connector.
     */
    @Override
    public void updateInstance(ServiceInstance instance) {
        log.info("updating broker user: " + instance.getId());
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
     */
    @Override
    public void createBinding(ServiceInstance instance, ServiceBinding binding) {
        // use app guid to send bind request
        //don't need to talk to kafka, just return credentials.
        log.info("binding app: " + binding.getAppGuid() + " to topic: " + instance.getParameters().get(TOPIC_NAME_KEY));
    }

    /**
     * Called during the unbind-service process. This is a good time to destroy any resources, users, connections set up during the bind process.
     *
     * @param instance service instance data passed in by the cloud connector.
     * @param binding  binding data passed in by the cloud connector.
     */
    @Override
    public void deleteBinding(ServiceInstance instance, ServiceBinding binding) {
        log.info("unbinding app: " + binding.getAppGuid() + " from topic: " + instance.getParameters().get(TOPIC_NAME_KEY));
    }

    /**
     * Bind credentials that will be returned as the result of a create-binding process. The format and values of these credentials will
     * depend on the nature of the underlying service. For more information and some examples, see
     * <a href=https://docs.cloudfoundry.org/services/binding-credentials.html>here.</a>
     * <p>
     * This method is called after the create-binding method: any information stored in binding.properties in the createBinding call
     * will be available here, along with any custom data passed in as json parameters as part of the create-binding process by the client.
     *
     * @param instance service instance data passed in by the cloud connector.
     * @param binding  binding data passed in by the cloud connector.
     * @return credentials, as a series of key/value pairs
     */
    @Override
    public Map<String, Object> getCredentials(ServiceInstance instance, ServiceBinding binding) {
        log.info("returning credentials.");

        try {
            Map<String, Object> m = new HashMap<>();
            //m.put("hostname", env.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
            m.put("hostname", client.getBootstrapServers());
            m.put(TOPIC_NAME_KEY, instance.getParameters().get(TOPIC_NAME_KEY));

            String uri = "kafka://" + m.get("hostname") + "/" + m.get(TOPIC_NAME_KEY);
            m.put("uri", uri);
            return m;
        } catch (Throwable t) {
            throw new KafkaBrokerException(t);
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}