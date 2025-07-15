package com.manus.kafka.core.factory;

import com.manus.kafka.core.config.KafkaConsumerProperties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;
import java.util.Properties;

/**
 * Factory for creating Kafka Consumer instances.
 * This factory provides methods to create consumers with different configurations.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class KafkaConsumerFactory<K, V> {
    
    private final KafkaConsumerProperties properties;
    
    /**
     * Creates a new instance of KafkaConsumerFactory with default properties.
     */
    public KafkaConsumerFactory() {
        this.properties = new KafkaConsumerProperties();
    }
    
    /**
     * Creates a new instance of KafkaConsumerFactory with the specified properties.
     *
     * @param properties the consumer properties
     */
    public KafkaConsumerFactory(KafkaConsumerProperties properties) {
        this.properties = properties;
    }
    
    /**
     * Creates a new Kafka Consumer with the current properties.
     *
     * @return a new Kafka Consumer
     */
    public Consumer<K, V> createConsumer() {
        properties.validate();
        return new KafkaConsumer<>(properties.asProperties());
    }
    
    /**
     * Creates a new Kafka Consumer with the current properties and the specified key and value deserializers.
     *
     * @param keyDeserializer the key deserializer
     * @param valueDeserializer the value deserializer
     * @return a new Kafka Consumer
     */
    public Consumer<K, V> createConsumer(Deserializer<K> keyDeserializer, Deserializer<V> valueDeserializer) {
        properties.validate();
        return new KafkaConsumer<>(properties.asProperties(), keyDeserializer, valueDeserializer);
    }
    
    /**
     * Creates a new Kafka Consumer with the current properties and additional custom properties.
     *
     * @param customProperties additional properties to override the current ones
     * @return a new Kafka Consumer
     */
    public Consumer<K, V> createConsumer(Map<String, Object> customProperties) {
        KafkaConsumerProperties mergedProperties = new KafkaConsumerProperties();
        mergedProperties.properties(properties.asMap());
        mergedProperties.properties(customProperties);
        mergedProperties.validate();
        return new KafkaConsumer<>(mergedProperties.asProperties());
    }
    
    /**
     * Creates a new Kafka Consumer with the current properties, additional custom properties,
     * and the specified key and value deserializers.
     *
     * @param customProperties additional properties to override the current ones
     * @param keyDeserializer the key deserializer
     * @param valueDeserializer the value deserializer
     * @return a new Kafka Consumer
     */
    public Consumer<K, V> createConsumer(Map<String, Object> customProperties, 
                                         Deserializer<K> keyDeserializer, 
                                         Deserializer<V> valueDeserializer) {
        KafkaConsumerProperties mergedProperties = new KafkaConsumerProperties();
        mergedProperties.properties(properties.asMap());
        mergedProperties.properties(customProperties);
        mergedProperties.validate();
        return new KafkaConsumer<>(mergedProperties.asProperties(), keyDeserializer, valueDeserializer);
    }
    
    /**
     * Creates a new Kafka Consumer with the specified properties.
     *
     * @param properties the consumer properties
     * @return a new Kafka Consumer
     */
    public static <K, V> Consumer<K, V> createConsumer(Properties properties) {
        return new KafkaConsumer<>(properties);
    }
    
    /**
     * Gets the current consumer properties.
     *
     * @return the current consumer properties
     */
    public KafkaConsumerProperties getProperties() {
        return properties;
    }
}