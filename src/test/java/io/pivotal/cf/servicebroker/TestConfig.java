package io.pivotal.cf.servicebroker;

import io.pivotal.cf.servicebroker.model.ServiceBinding;
import io.pivotal.cf.servicebroker.model.ServiceInstance;
import org.apache.kafka.connect.data.Date;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
class TestConfig {

 @Bean
    public Date foo() {
     return new Date();
 }


//
//    static final String SI_ID = "siId";
//    static final String SB_ID = "sbId";
//
//    private static final String SD_ID = "aUniqueId";
//    private static final String PLAN_ID = "anotherUniqueId";
//    private static final String APP_GUID = "anAppGuid";
//    private static final String ORG_GUID = "anOrgGuid";
//    private static final String SPACE_GUID = "aSpaceGuid";
//
//    static final String PASSWORD = "password";
//
    @MockBean
    private RedisTemplate<String, ServiceInstance> instanceTemplate;

    @MockBean
    private RedisTemplate<String, ServiceBinding> bindingTemplate;
//
//    @Bean
//    public CreateServiceInstanceRequest createServiceInstanceRequest() {
//        CreateServiceInstanceRequest req = new CreateServiceInstanceRequest(SD_ID, PLAN_ID, ORG_GUID, SPACE_GUID, getParameters());
//        req.withServiceInstanceId(SI_ID);
//        return req;
//    }
//
//    @Bean
//    public ServiceInstance serviceInstance(CreateServiceInstanceRequest req) {
//        return new ServiceInstance(req);
//    }
//
//    private Map<String, Object> getBindResources() {
//        Map<String, Object> m = new HashMap<>();
//        m.put("app_guid", APP_GUID);
//        return m;
//    }
//
//    private Map<String, Object> getParameters() {
//        Map<String, Object> m = new HashMap<>();
//        m.put("foo", "bar");
//        m.put("bizz", "bazz");
//        return m;
//    }
//
//    @Bean
//    public CreateServiceInstanceBindingRequest createBindingRequest() {
//        CreateServiceInstanceBindingRequest req = new CreateServiceInstanceBindingRequest(SD_ID, PLAN_ID, APP_GUID,
//                getBindResources(), getParameters());
//        req.withBindingId(SB_ID);
//        req.withServiceInstanceId(SI_ID);
//        return req;
//    }
//
//    @Bean
//    public ServiceBinding serviceBinding(CreateServiceInstanceBindingRequest req) {
//        return new ServiceBinding(req);
//    }
}