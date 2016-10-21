package io.pivotal.cf.servicebroker;

import io.pivotal.cf.servicebroker.model.ServiceBinding;
import io.pivotal.cf.servicebroker.model.ServiceInstance;
import io.pivotal.cf.servicebroker.service.BrokeredService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaBrokerTest {

    @Autowired
    private BrokeredService kafkaBroker;

    @Autowired
    private ServiceInstance serviceInstance;

    @Autowired
    private ServiceBinding serviceBinding;

    @Test
    public void testCreateInstance() throws ServiceBrokerException {
        kafkaBroker.createInstance(serviceInstance);
        Object o = serviceInstance.getParameters().get(KafkaBroker.TOPIC_NAME_KEY);
        assertNotNull(o);
        assertEquals(TestConfig.SI_ID, o.toString());

        serviceInstance.getParameters().put(KafkaBroker.TOPIC_NAME_KEY, "myTopic");
        o = serviceInstance.getParameters().get(KafkaBroker.TOPIC_NAME_KEY);
        assertNotNull(o);
        assertEquals("myTopic", o.toString());
    }

    @Test
    public void testDeleteInstance() throws ServiceBrokerException {
        kafkaBroker.deleteInstance(serviceInstance);
    }

    @Test
    public void testUpdateInstance() throws ServiceBrokerException {
        kafkaBroker.updateInstance(serviceInstance);
    }

    @Test
    public void testCreateBinding() {
        kafkaBroker.createBinding(serviceInstance, serviceBinding);
    }

    @Test
    public void testDeleteBinding() {
        kafkaBroker.deleteBinding(serviceInstance, serviceBinding);
    }

    @Test
    public void testGetCredentials() {
        Map<String, Object> m = kafkaBroker.getCredentials(serviceInstance, serviceBinding);
        assertNotNull(m);
    }

    @Test
    public void testAsync() {
        assertFalse(kafkaBroker.isAsync());
    }
}