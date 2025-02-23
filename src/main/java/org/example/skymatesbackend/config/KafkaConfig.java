package org.example.skymatesbackend.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        // 配置生产者, Kafka 几个常见概念: Producer, Consumer, Topic, Broker
        // Broker 负责接收生产者发送的消息、存储这些消息, 消费者主动从 Broker 拉取消息
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        // KafkaTemplate 是 Spring Kafka 提供的工具
        // 简单来说，KafkaTemplate 是一个高级抽象，用于方便地向 Kafka 发送消息，
        // 而无需手动管理底层的 Kafka 生产者
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        // 配置 Kafka 消费者
        // 客户端启动时，会用 `bootstrap.servers` 提供的地址列表来初次连接 Kafka 集群
        // 这个配置仅用于**入口**，帮助客户端找到集群中任意一个 broker，然后获取整个集群的元数据信息
        // 在生产环境中，为了容错，通常会配置多个 broker 的地址
        // configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9092,broker3:9092");
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "like-group");
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        // ConcurrentKafkaListenerContainerFactory 是 Spring Kafka 提供的工具
        // 简单来说，它是一个工厂类，用于创建和管理支持并发消费的 Kafka 监听器容器，
        // 让你方便地从 Kafka Topic 消费消息，而无需手动管理底层的 Kafka 消费者
        // “Kafka 监听器容器”是 Spring Kafka 提供的一个抽象, 并非 Kafka 本身的概念
        // Kafka 监听器容器 是 Spring Kafka 提供的管理和封装 Kafka 消费者实例的工具, 负责创建和管理 消费者实例,
        // 使用 Kafka 的消费者 API 时，需要手动管理消费者的创建、轮询、提交偏移量、错误处理等, 监听器容器把这些复杂性封装起来，开发者只需要关注业务逻辑
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
