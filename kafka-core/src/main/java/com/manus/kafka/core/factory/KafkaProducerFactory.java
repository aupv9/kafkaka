package com.manus.kafka.core.factory;

import com.manus.kafka.core.config.KafkaProducerProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;
import java.util.Properties;

/**
 * Factory for creating Kafka Producer instances.
 * This factory provides methods to create producers with different configurations.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class KafkaProducerFactory<K, V> {
    
    private final KafkaProducerProperties properties;
    
    /**
     * Creates a new instance of KafkaProducerFactory with default properties.
     */
    public KafkaProducerFactory() {
        this.properties = new KafkaProducerProperties();
    }
    
    /**
     * Creates a new instance of KafkaProducerFactory with the specified properties.
     *
     * @param properties the producer properties
     */
    public KafkaProducerFactory(KafkaProducerProperties properties) {
        this.properties = properties;
    }
    
    /**
     * Creates a new Kafka Producer with the current properties.
     *
     * @return a new Kafka Producer
     */
    public Producer<K, V> createProducer() {
        properties.validate();
        return new KafkaProducer<>(properties.asProperties());
    }
    
    /**
     * Creates a new Kafka Producer with the current properties and the specified key and value serializers.
     *
     * @param keySerializer the key serializer
     * @param valueSerializer the value serializer
     * @return a new Kafka Producer
     */
    public Producer<K, V> createProducer(Serializer<K> keySerializer, Serializer<V> valueSerializer) {
        properties.validate();
        return new KafkaProducer<>(properties.asProperties(), keySerializer, valueSerializer);
    }
    
    /**
     * Creates a new Kafka Producer with the current properties and additional custom properties.
     *
     * @param customProperties additional properties to override the current ones
     * @return a new Kafka Producer
     */
    public Producer<K, V> createProducer(Map<String, Object> customProperties) {
        KafkaProducerProperties mergedProperties = new KafkaProducerProperties();
        mergedProperties.properties(properties.asMap());
        mergedProperties.properties(customProperties);
        mergedProperties.validate();
        return new KafkaProducer<>(mergedProperties.asProperties());
    }
    
    /**
     * Creates a new Kafka Producer with the current properties, additional custom properties,
     * and the specified key and value serializers.
     *
     * @param customProperties additional properties to override the current ones
     * @param keySerializer the key serializer
     * @param valueSerializer the value serializer
     * @return a new Kafka Producer
     */
    public Producer<K, V> createProducer(Map<String, Object> customProperties, 
                                         Serializer<K> keySerializer, 
                                         Serializer<V> valueSerializer) {
        KafkaProducerProperties mergedProperties = new KafkaProducerProperties();
        mergedProperties.properties(properties.asMap());
        mergedProperties.properties(customProperties);
        mergedProperties.validate();
        return new KafkaProducer<>(mergedProperties.asProperties(), keySerializer, valueSerializer);
    }
    
    /**
     * Creates a new Kafka Producer with the specified properties.
     *
     * @param properties the producer properties
     * @return a new Kafka Producer
     */
    public static <K, V> Producer<K, V> createProducer(Properties properties) {
        return new KafkaProducer<>(properties);
    }
    
    /**
     * Gets the current producer properties.
     *
     * @return the current producer properties
     */
    public KafkaProducerProperties getProperties() {
        return properties;
    }
}