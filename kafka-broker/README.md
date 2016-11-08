#kafka-broker
A cloud foundry service broker for kafka.

##Prerequisites
The [simple-service-broker](https://github.com/cf-platform-eng/simple-service-broker) project is a dependency for the kafka broker. Please see the [README](https://github.com/cf-platform-eng/simple-service-broker/blob/master/simple-broker/README.md) for instructions on building this dependency.
  
##Using kafka-broker
1. kafka-broker requires a redis datastore. To set this up:
  
  ```bash
  cf create-service p-redis shared-vm redis-for-kafka
  ```
2. The broker makes use of spring-security to protect itself against unauthorized meddling. To set its password edit the [application.properties file](https://github.com/cf-platform-eng/confluent-kafka-broker/blob/master/kafka-broker/src/main/resources/application.properties) (you probably don't want to check this in!)
1. Edit the [manifest.yml](https://github.com/cf-platform-eng/confluent-kafka-broker/blob/master/kafka-broker/manifest.yml) file as needed for your CF install.
1. Build the broker:
  
  ```bash
  cd kafka-broker
  mvn clean install
  ```
5. Push the broker to cf:
  
  ```bash
  cf push
  ```
6. Register the broker:
  
  ```bash
  cf create-service-broker your_broker_name user the_password_from_application_properties https://uri.of.your.broker.app
  ```
7. See the broker:
  
  ```bash
  cf service-brokers
  Getting service brokers as admin...
  
  name                          url
  ...
  kafka-broker                  https://your-broker-url
  ...
  
  cf service-access
  Getting service access as admin...
  ...
  broker: kafka-broker
     service          plan      access   orgs
     KafkaService     PubSub    none
  ...
  
  cf enable-service-access KafkaService
  Enabling access to all plans of service KafkaService for all orgs as admin...

  cf marketplace
  Getting services from marketplace in org your-org / space your-space as you...
  OK
  
  service          plans           description
  KafkaService     PubSub          Kafka Service Broker for Pivotal Cloud Foundry
  ...
  ```