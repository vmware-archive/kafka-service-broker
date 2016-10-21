package io.pivotal.cf.servicebroker;

import io.pivotal.cf.servicebroker.model.ServiceBinding;
import io.pivotal.cf.servicebroker.model.ServiceInstance;
import io.pivotal.cf.servicebroker.service.BrokeredService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Ignore
public class KafkaBrokerTest {

//    @MockBean
//    private HelloBrokerRepository helloBrokerRepository;

    @Autowired
    private BrokeredService helloBroker;

    @Autowired
    private ServiceInstance serviceInstance;

    @Autowired
    private ServiceBinding serviceBinding;

    @Test
    public void testProvision() throws ServiceBrokerException {
//        given(this.helloBrokerRepository.provisionUser(instanceUser))
//                .willReturn(new User(instanceUser.getName(), TestConfig.PASSWORD, instanceUser.getRole()));
//
//        helloBroker.createInstance(serviceInstance);
//
//        User user = (User) serviceInstance.getParameters().get("user");
//        assertNotNull(user);
//        assertEquals(TestConfig.SI_ID, user.getName());
//        assertEquals(User.Role.Broker, user.getRole());
//        assertEquals(TestConfig.PASSWORD, user.getPassword());
    }

    @Test
    public void testDeprovision() throws ServiceBrokerException {
//        serviceInstance.getParameters().put("user", instanceUser);
//
//        helloBroker.deleteInstance(serviceInstance);
//        assertFalse(serviceInstance.getParameters().containsKey("user"));
    }

    @Test
    public void testBinding() {
//        given(this.helloBrokerRepository.provisionUser(bindingUser))
//                .willReturn(new User(bindingUser.getName(), TestConfig.PASSWORD, bindingUser.getRole()));
//
//        helloBroker.createBinding(serviceInstance, serviceBinding);
//
//        User user = (User) serviceBinding.getParameters().get("user");
//        assertNotNull(user);
//        assertEquals(TestConfig.SB_ID, user.getName());
//        assertEquals(User.Role.User, user.getRole());
//        assertEquals(TestConfig.PASSWORD, user.getPassword());
    }

    @Test
    public void testDeleteBinding() {
//        serviceBinding.getParameters().put("user", bindingUser);
//
//        helloBroker.deleteBinding(serviceInstance, serviceBinding);
//        assertFalse(serviceBinding.getParameters().containsKey("user"));
    }

    @Test
    public void testCredentials() {
//        bindingUser.setPassword(TestConfig.PASSWORD);
//        serviceBinding.getParameters().put("user", bindingUser);
//
//        Map<String, Object> m = helloBroker.getCredentials(serviceInstance, serviceBinding);
//        assertNotNull(m);
//        assertEquals("localhost", m.get("hostname"));
//        assertEquals("8080", m.get("port"));
//        assertEquals(TestConfig.SB_ID, m.get("username"));
//        assertEquals(TestConfig.PASSWORD, m.get("password"));
//        assertEquals("hello://sbId:password@localhost:8080", m.get("uri"));
    }

    @Test
    public void testAsync() {
        assertFalse(helloBroker.isAsync());
    }

    @Test
    public void testInstanceUpdate() {
//        given(this.helloBrokerRepository.updateUser(instanceUser.getName(), instanceUser))
//                .willReturn(new User(instanceUser.getName(), "newPassword", instanceUser.getRole()));
//
//        serviceInstance.getParameters().put("user", instanceUser);
//
//        helloBroker.updateInstance(serviceInstance);
//
//        User user = (User) serviceInstance.getParameters().get("user");
//        assertNotNull(user);
//        assertEquals(TestConfig.SI_ID, user.getName());
//        assertEquals(User.Role.Broker, user.getRole());
//        assertEquals("newPassword", user.getPassword());
    }
}