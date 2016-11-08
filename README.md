#confluent-kafka-broker
This is a Cloud Foundry service broker for [apache kafka](https://kafka.apache.org/documentation). It supports the dynamic creation and deletion of topics, tied to the creation and deletion of Cloud Foundry service instances. Applications can then bind to these service instances to take part in Pub/Sub message exchanges.

##Where to get the tile
The tile will be available on [pivnet](https://network.pivotal.io/) in the near future (Fall, 2016). In the meanwhile, if you are interested in using the broker you can build and deploy it manually following the directions below.

##Building the project
The broker is based on the [simple-service-broker](https://github.com/cf-platform-eng/simple-service-broker). Follow the instruction in the [README](https://github.com/cf-platform-eng/simple-service-broker/blob/master/simple-broker/README.md) of that project to check-out, mvn build and install this library.

##The Modules
The kafka broker project includes the folowing modules. See their respective READMEs for more information.

###[kafka-broker](https://github.com/cf-platform-eng/confluent-kafka-broker/tree/master/kafka-broker)
This module contains the broker code.

###[kafka-connector](https://github.com/cf-platform-eng/confluent-kafka-broker/tree/master/kafka-connector)
This module contains spring-cloud-connector code that can optionally be used by consumers of the brokered service, to make it easier to connect to the kafka back-end services.

###[kafka-sample-consumer](https://github.com/cf-platform-eng/confluent-kafka-broker/tree/master/kafka-sample-consumer)
A sample project that can be used to demo message consumption, and illustrates the use of the broker-connector.
 
###[kafka-sample-producer](https://github.com/cf-platform-eng/confluent-kafka-broker/tree/master/kafka-sample-producer)
A sample project that can be used to demo message production, and illustrates the use of the broker-connector.