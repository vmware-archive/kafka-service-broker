#sample-connector
This is a sample spring cloud spring service connector that can be used to simplify service access for consumers of the sample-service, as brokered by the sample-broker. For general information about service connectors, please refer to [this documentation.](http://cloud.spring.io/spring-cloud-connectors/spring-cloud-spring-service-connector.html)

##Adapting the connector
To adapt the connector for use with your service, rename and modify the following classes as appropriate, and then include the connector project as a dependency to the client project.

###[HelloConnectionCreator](https://github.com/cf-platform-eng/simple-service-broker/blob/master/sample-connector/src/main/java/io/pivotal/cf/service/connector/HelloConnectionCreator.java)
This class is responsible for calling the HelloRepositoryFactory class, and returning a repository object. Clients will use this repository to communicate with your backend service.

###[HelloRepository](https://github.com/cf-platform-eng/simple-service-broker/blob/master/sample-connector/src/main/java/io/pivotal/cf/service/connector/HelloRepository.java)
The go-to for interacting with the back-end service. In our case this is a [feign](https://github.com/OpenFeign/feign) repository that speaks to rest API endpoints exposed by the HelloService.

###[HelloRepositoryFactory](https://github.com/cf-platform-eng/simple-service-broker/blob/master/sample-connector/src/main/java/io/pivotal/cf/service/connector/HelloRepositoryFactory.java)
Creates a repository using the data pulled out of HelloServiceInfo.
 
###[HelloServiceInfo](https://github.com/cf-platform-eng/simple-service-broker/blob/master/sample-connector/src/main/java/io/pivotal/cf/service/connector/HelloServiceInfo.java)
Value-holder that contains whatever is needed to connect to the backend service. On Cloud Foundry this data comes from VCAP_SERVICES environment variables, which are loaded from data provided in the broker's [getCredentials()](https://github.com/cf-platform-eng/simple-service-broker/blob/master/sample-broker/src/main/java/io/pivotal/cf/servicebroker/HelloBroker.java#L179-L200) method. The spring connector framework does the work of parsing these out for you and creating a populated instance of this class during your client's startup.

###[HelloServiceInfoCreator](https://github.com/cf-platform-eng/simple-service-broker/blob/master/sample-connector/src/main/java/io/pivotal/cf/service/connector/HelloServiceInfoCreator.java)
Translates the raw data from VCAP_SERVICES into a HelloServiceInfo instance.

###[resources/META-INF/services](https://github.com/cf-platform-eng/simple-service-broker/tree/master/sample-connector/src/main/resources/META-INF/services)
You will need to edit these files to add your specific class names (they are needed by the connector framework).