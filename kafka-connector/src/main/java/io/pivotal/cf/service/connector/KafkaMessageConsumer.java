package io.pivotal.cf.service.connector;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.utils.SystemTime;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by miyer on 10/26/16.
 */


@Slf4j
public class KafkaMessageConsumer implements Runnable {


    private KafkaServiceInfo info;
    private KafkaConsumer consumer;

    public KafkaMessageConsumer(KafkaServiceInfo info){
        this.info = info;
        Properties props = new Properties();
        props.put("bootstrap.servers", info.getHost()+":"+info.getPort());
        props.put("group.id", "Foo");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(props);
    }


    @Override
    public void run() {


        System.out.println("ConsumerLoop: call()");
        try {
            Boolean keepPolling = true;

            System.out.printf("ConsumerLoop subscribing to (group id )\n", this.info.getId(), this.info.getTopicName(), "Foo");

            List<String> topics = new ArrayList<>();
            topics.add(this.info.getTopicName());

            consumer.subscribe(topics);
            log.info("..........all the topics subscribed to.........."+topics);

            while (keepPolling) {

                log.info("..........Now polling for EVER..........."+ 10000);

                ConsumerRecords<String, String> records = consumer.poll(10000);
                log.info("..........Number of records..........."+ records.count());

                for (ConsumerRecord<String, String> record : records) {

                    log.info("..........Record is ..........."+ record);

                    Map<String, Object> data = new HashMap<>();
                    data.put("partition", record.partition());
                    data.put("offset", record.offset());
                    data.put("value", record.value());
                    System.out.println(this.info.getId() + ": " + data);
                    log.info("Received message ************" + data.toString());

                }
            }

            log.info(".................... fell through..............");
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            System.out.println("Closing consumer");
            consumer.close();
        }
    }
}
