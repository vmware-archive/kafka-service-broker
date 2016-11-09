#kafka-service-broker
This is a Cloud Foundry service broker for [apache kafka](https://kafka.apache.org/documentation). It supports the dynamic creation and deletion of topics, tied to the creation and deletion of Cloud Foundry service instances. Applications can then bind to these service instances to take part in Pub/Sub message exchanges.

This version should be considered a beta product, has been tested against a single zookeeper and a single kafka node. Further testing is currently a work-in-progress.

##Where to get the tile
The tile will be available on [pivnet](https://network.pivotal.io/) in the near future (Fall, 2016). In the meanwhile, if you are interested in using the broker you can build and deploy it manually following the directions below.

##Prerequisites

This is a service broker implementation and requires a zookeeper and a kafka instance. This is a pre-cursor to a bosh-release and currently has been tested with a single kafka node and a single zookeeper node.
The broker is based on the [simple-service-broker](https://github.com/cf-platform-eng/simple-service-broker). Follow the instruction in the [README](https://github.com/cf-platform-eng/simple-service-broker/blob/master/simple-broker/README.md) of that project to check-out, mvn build and install the library.

##The Modules
The kafka broker project includes the folowing modules. See their respective READMEs for more information.

###[kafka-broker](https://github.com/cf-platform-eng/kafka-service-broker/tree/master/kafka-broker)
* This module contains the broker code.

###[kafka-connector](https://github.com/cf-platform-eng/kafka-service-broker/tree/master/kafka-connector)
* This module contains spring-cloud-connector code that can optionally be used by consumers of the brokered service, to make it easier to connect to the kafka back-end services.

###[kafka-sample-consumer](https://github.com/cf-platform-eng/kafka-service-broker/tree/master/kafka-sample-consumer)
* A sample project that can be used to demo message consumption, and illustrates the use of the broker-connector.
 
###[kafka-sample-producer](https://github.com/cf-platform-eng/kafka-service-broker/tree/master/kafka-sample-producer)
* A sample project that can be used to demo message production, and illustrates the use of the broker-connector.

##Instructions to run the demo
1. check out and build the project

  ```bash
  git clone git@github.com:cf-platform-eng/kafka-service-broker.git
  cd kafka-service-broker
  mvn clean install  
  ```
2. Follow the instructions in the [kafka-broker](https://github.com/cf-platform-eng/kafka-service-broker/tree/master/kafka-broker) to push and register the broker.
3. Create the kafka service and make sure the _name of the service_ matches the names in the manifest.yml files _exactly_ in the producer and consumer apps. If it does not, make sure to change the name in the manifest.yml. 

  ```bash  
  cf create-service KafkaService PubSub kafka-service   
  ```  
4. Follow the instructions in the [kafka-sample-producer](https://github.com/cf-platform-eng/kafka-service-broker/tree/master/kafka-sample-producer) to push the sample producer app.
5. Follow the instructions in the [kafka-sample-consumer](https://github.com/cf-platform-eng/kafka-service-broker/tree/master/kafka-sample-consumer) to push the sample consumer app.


