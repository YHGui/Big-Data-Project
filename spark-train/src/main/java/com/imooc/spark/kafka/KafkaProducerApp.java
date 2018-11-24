package com.imooc.spark.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;
import java.util.UUID;

public class KafkaProducerApp {
    public static void main(String[] args) {
        String topic = "imooc_pk_offset";

        Properties properties = new Properties();

        properties.put("metadata.broker.list", KafkaProperties.BROKER_LIST);
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("request.required.acks", "1");
        properties.put("partitioner.class", "kafka.producer.DefaultPartitioner");

        Producer<String, String> producer = new Producer<String, String>(new ProducerConfig(properties));

        for (int index = 0; index < 100; index++) {
            producer.send(new KeyedMessage<String, String>(topic, index + "", "慕课PK哥: " + UUID.randomUUID()));
        }

        System.out.println("慕课网PK哥Kafka生产者生产数据完毕...");
    }
}
