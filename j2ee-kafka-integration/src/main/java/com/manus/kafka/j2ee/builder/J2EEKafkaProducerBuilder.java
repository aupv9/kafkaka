package com.manus.kafka.j2ee.builder;

import com.manus.kafka.core.config.KafkaProducerProperties;
import com.manus.kafka.core.factory.KafkaProducerFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Builder for creating Kafka Producer instances in a J2EE environment.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class J2EEKafkaProducerBuilder<K, V> {
    
    private final KafkaProducerProperties properties;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;
    
    /**
     * Creates a new instance of J2EEKafkaProducerBuilder with default properties.
     */
    public J2EEKafkaProducerBuilder() {
        this.properties = new KafkaProducerProperties();
    }
    
    /**
     * Creates a new instance of J2EEKafkaProducerBuilder with the specified properties.
     *
     * @param properties the producer properties
     */
    public J2EEKafkaProducerBuilder(KafkaProducerProperties properties) {
        this.properties = properties;
    }
    
    /**
     * Sets the bootstrap servers.
     *
     * @param bootstrapServers comma-separated list of host:port pairs
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> bootstrapServers(String bootstrapServers) {
        properties.bootstrapServers(bootstrapServers);
        return this;
    }
    
    /**
     * Sets the client ID.
     *
     * @param clientId the client ID
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> clientId(String clientId) {
        properties.clientId(clientId);
        return this;
    }
    
    /**
     * Sets the acknowledgment mode.
     *
     * @param acks the acknowledgment mode (0, 1, all)
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> acks(String acks) {
        properties.acks(acks);
        return this;
    }
    
    /**
     * Sets the number of retries.
     *
     * @param retries the number of retries
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> retries(int retries) {
        properties.retries(retries);
        return this;
    }
    
    /**
     * Sets the batch size in bytes.
     *
     * @param batchSize the batch size in bytes
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> batchSize(int batchSize) {
        properties.batchSize(batchSize);
        return this;
    }
    
    /**
     * Sets the linger time in milliseconds.
     *
     * @param lingerMs the linger time in milliseconds
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> lingerMs(long lingerMs) {
        properties.lingerMs(lingerMs);
        return this;
    }
    
    /**
     * Sets the buffer memory in bytes.
     *
     * @param bufferMemory the buffer memory in bytes
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> bufferMemory(long bufferMemory) {
        properties.bufferMemory(bufferMemory);
        return this;
    }
    
    /**
     * Sets the key serializer class.
     *
     * @param keySerializerClass the fully qualified class name of the key serializer
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> keySerializer(String keySerializerClass) {
        properties.keySerializer(keySerializerClass);
        return this;
    }
    
    /**
     * Sets the value serializer class.
     *
     * @param valueSerializerClass the fully qualified class name of the value serializer
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> valueSerializer(String valueSerializerClass) {
        properties.valueSerializer(valueSerializerClass);
        return this;
    }
    
    /**
     * Sets the key serializer.
     *
     * @param keySerializer the key serializer
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> keySerializer(Serializer<K> keySerializer) {
        this.keySerializer = keySerializer;
        return this;
    }
    
    /**
     * Sets the value serializer.
     *
     * @param valueSerializer the value serializer
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> valueSerializer(Serializer<V> valueSerializer) {
        this.valueSerializer = valueSerializer;
        return this;
    }
    
    /**
     * Sets the compression type.
     *
     * @param compressionType the compression type (none, gzip, snappy, lz4, zstd)
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> compressionType(String compressionType) {
        properties.compressionType(compressionType);
        return this;
    }
    
    /**
     * Sets the security protocol.
     *
     * @param securityProtocol the security protocol (PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL)
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> securityProtocol(String securityProtocol) {
        properties.securityProtocol(securityProtocol);
        return this;
    }
    
    /**
     * Sets the SASL mechanism.
     *
     * @param saslMechanism the SASL mechanism (GSSAPI, PLAIN, SCRAM-SHA-256, SCRAM-SHA-512, OAUTHBEARER)
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> saslMechanism(String saslMechanism) {
        properties.saslMechanism(saslMechanism);
        return this;
    }
    
    /**
     * Sets the SASL JAAS config.
     *
     * @param saslJaasConfig the SASL JAAS config
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> saslJaasConfig(String saslJaasConfig) {
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
    public J2EEKafkaProducerBuilder<K, V> property(String key, Object value) {
        properties.property(key, value);
        return this;
    }
    
    /**
     * Sets multiple custom properties.
     *
     * @param props the properties to set
     * @return this instance for method chaining
     */
    public J2EEKafkaProducerBuilder<K, V> properties(Map<String, Object> props) {
        properties.properties(props);
        return this;
    }
    
    /**
     * Builds a Kafka Producer with the configured properties.
     *
     * @return a new Kafka Producer
     */
    public Producer<K, V> build() {
        KafkaProducerFactory<K, V> factory = new KafkaProducerFactory<>(properties);
        
        if (keySerializer != null && valueSerializer != null) {
            return factory.createProducer(keySerializer, valueSerializer);
        } else {
            return factory.createProducer();
        }
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