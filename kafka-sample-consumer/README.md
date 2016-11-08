#kafka-sample-consumer
This is an example spring boot sample application that makes use of the kafka-connector and kafka-broker to bind to a kafka service and consume messages.

##Using kafka-sample-consumer
1. Git checkout and build the modules (if you have not already done so):

  ```bash
  git clone git@github.com:cf-platform-eng/kafka-service-broker.git
  cd kafka-service-broker
  mvn clean install
  ```
1. Deploy kafka-sample-consumer. There is nothing to configure, other than maybe changing the name of the services in the manifest file, because the name should match the name you give to the kafka-service created.
  ```bash
  cd kafka-sample-consumer
  cf push
  ```
2. Once the app has been pushed successfully and is running, tail the app logs using the following command:
  ```bash
  cf logs kafka-sample-consumer
  ```  
3. The consumer app will display any messages sent from the kafka-sample-producer (or any other app publishing to the kafka-service-instance) as long as they are both bound to the _same_ kafka service instance.  
