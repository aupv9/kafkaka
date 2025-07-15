package com.manus.kafka.j2ee.builder;

import com.manus.kafka.core.config.KafkaConsumerProperties;
import com.manus.kafka.core.factory.KafkaConsumerFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Builder for creating Kafka Consumer instances in a J2EE environment.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class J2EEKafkaConsumerBuilder<K, V> {
    
    private final KafkaConsumerProperties properties;
    private Deserializer<K> keyDeserializer;
    private Deserializer<V> valueDeserializer;
    
    /**
     * Creates a new instance of J2EEKafkaConsumerBuilder with default properties.
     */
    public J2EEKafkaConsumerBuilder() {
        this.properties = new KafkaConsumerProperties();
    }
    
    /**
     * Creates a new instance of J2EEKafkaConsumerBuilder with the specified properties.
     *
     * @param properties the consumer properties
     */
    public J2EEKafkaConsumerBuilder(KafkaConsumerProperties properties) {
        this.properties = properties;
    }
    
    /**
     * Sets the bootstrap servers.
     *
     * @param bootstrapServers comma-separated list of host:port pairs
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> bootstrapServers(String bootstrapServers) {
        properties.bootstrapServers(bootstrapServers);
        return this;
    }
    
    /**
     * Sets the client ID.
     *
     * @param clientId the client ID
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> clientId(String clientId) {
        properties.clientId(clientId);
        return this;
    }
    
    /**
     * Sets the group ID.
     *
     * @param groupId the consumer group ID
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> groupId(String groupId) {
        properties.groupId(groupId);
        return this;
    }
    
    /**
     * Sets whether to enable auto commit.
     *
     * @param enableAutoCommit whether to enable auto commit
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> enableAutoCommit(boolean enableAutoCommit) {
        properties.enableAutoCommit(enableAutoCommit);
        return this;
    }
    
    /**
     * Sets the auto commit interval in milliseconds.
     *
     * @param autoCommitIntervalMs the auto commit interval in milliseconds
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> autoCommitIntervalMs(int autoCommitIntervalMs) {
        properties.autoCommitIntervalMs(autoCommitIntervalMs);
        return this;
    }
    
    /**
     * Sets the auto offset reset policy.
     *
     * @param autoOffsetReset the auto offset reset policy (earliest, latest, none)
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> autoOffsetReset(String autoOffsetReset) {
        properties.autoOffsetReset(autoOffsetReset);
        return this;
    }
    
    /**
     * Sets the fetch minimum bytes.
     *
     * @param fetchMinBytes the fetch minimum bytes
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> fetchMinBytes(int fetchMinBytes) {
        properties.fetchMinBytes(fetchMinBytes);
        return this;
    }
    
    /**
     * Sets the fetch maximum wait time in milliseconds.
     *
     * @param fetchMaxWaitMs the fetch maximum wait time in milliseconds
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> fetchMaxWaitMs(int fetchMaxWaitMs) {
        properties.fetchMaxWaitMs(fetchMaxWaitMs);
        return this;
    }
    
    /**
     * Sets the maximum number of records returned in a single call to poll().
     *
     * @param maxPollRecords the maximum number of records
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> maxPollRecords(int maxPollRecords) {
        properties.maxPollRecords(maxPollRecords);
        return this;
    }
    
    /**
     * Sets the key deserializer class.
     *
     * @param keyDeserializerClass the fully qualified class name of the key deserializer
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> keyDeserializer(String keyDeserializerClass) {
        properties.keyDeserializer(keyDeserializerClass);
        return this;
    }
    
    /**
     * Sets the value deserializer class.
     *
     * @param valueDeserializerClass the fully qualified class name of the value deserializer
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> valueDeserializer(String valueDeserializerClass) {
        properties.valueDeserializer(valueDeserializerClass);
        return this;
    }
    
    /**
     * Sets the key deserializer.
     *
     * @param keyDeserializer the key deserializer
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> keyDeserializer(Deserializer<K> keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
        return this;
    }
    
    /**
     * Sets the value deserializer.
     *
     * @param valueDeserializer the value deserializer
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> valueDeserializer(Deserializer<V> valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
        return this;
    }
    
    /**
     * Sets the security protocol.
     *
     * @param securityProtocol the security protocol (PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL)
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> securityProtocol(String securityProtocol) {
        properties.securityProtocol(securityProtocol);
        return this;
    }
    
    /**
     * Sets the SASL mechanism.
     *
     * @param saslMechanism the SASL mechanism (GSSAPI, PLAIN, SCRAM-SHA-256, SCRAM-SHA-512, OAUTHBEARER)
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> saslMechanism(String saslMechanism) {
        properties.saslMechanism(saslMechanism);
        return this;
    }
    
    /**
     * Sets the SASL JAAS config.
     *
     * @param saslJaasConfig the SASL JAAS config
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> saslJaasConfig(String saslJaasConfig) {
        properties.saslJaasConfig(saslJaasConfig);
        return this;
    }
    
    /**
     * Sets a custom property.
     *
     * @param key the property key
     * @param value the property value
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> property(String key, Object value) {
        properties.property(key, value);
        return this;
    }
    
    /**
     * Sets multiple custom properties.
     *
     * @param props the properties to set
     * @return this instance for method chaining
     */
    public J2EEKafkaConsumerBuilder<K, V> properties(Map<String, Object> props) {
        properties.properties(props);
        return this;
    }
    
    /**
     * Builds a Kafka Consumer with the configured properties.
     *
     * @return a new Kafka Consumer
     */
    public Consumer<K, V> build() {
        KafkaConsumerFactory<K, V> factory = new KafkaConsumerFactory<>(properties);
        
        if (keyDeserializer != null && valueDeserializer != null) {
            return factory.createConsumer(keyDeserializer, valueDeserializer);
        } else {
            return factory.createConsumer();
        }
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