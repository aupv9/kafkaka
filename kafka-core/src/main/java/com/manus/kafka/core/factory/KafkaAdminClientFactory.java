package com.manus.kafka.core.factory;

import com.manus.kafka.core.config.KafkaAdminClientProperties;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;

import java.util.Map;
import java.util.Properties;

/**
 * Factory for creating Kafka AdminClient instances.
 * This factory provides methods to create admin clients with different configurations.
 */
public class KafkaAdminClientFactory {
    
    private final KafkaAdminClientProperties properties;
    
    /**
     * Creates a new instance of KafkaAdminClientFactory with default properties.
     */
    public KafkaAdminClientFactory() {
        this.properties = new KafkaAdminClientProperties();
    }
    
    /**
     * Creates a new instance of KafkaAdminClientFactory with the specified properties.
     *
     * @param properties the admin client properties
     */
    public KafkaAdminClientFactory(KafkaAdminClientProperties properties) {
        this.properties = properties;
    }
    
    /**
     * Creates a new Kafka AdminClient with the current properties.
     *
     * @return a new Kafka AdminClient
     */
    public AdminClient createAdminClient() {
        properties.validate();
        return KafkaAdminClient.create(properties.asProperties());
    }
    
    /**
     * Creates a new Kafka AdminClient with the current properties and additional custom properties.
     *
     * @param customProperties additional properties to override the current ones
     * @return a new Kafka AdminClient
     */
    public AdminClient createAdminClient(Map<String, Object> customProperties) {
        KafkaAdminClientProperties mergedProperties = new KafkaAdminClientProperties();
        mergedProperties.properties(properties.asMap());
        mergedProperties.properties(customProperties);
        mergedProperties.validate();
        return KafkaAdminClient.create(mergedProperties.asProperties());
    }
    
    /**
     * Creates a new Kafka AdminClient with the specified properties.
     *
     * @param properties the admin client properties
     * @return a new Kafka AdminClient
     */
    public static AdminClient createAdminClient(Properties properties) {
        return KafkaAdminClient.create(properties);
    }
    
    /**
     * Gets the current admin client properties.
     *
     * @return the current admin client properties
     */
    public KafkaAdminClientProperties getProperties() {
        return properties;
    }
}