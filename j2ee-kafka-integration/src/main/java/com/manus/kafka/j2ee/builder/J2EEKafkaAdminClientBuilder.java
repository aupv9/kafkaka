package com.manus.kafka.j2ee.builder;

import com.manus.kafka.core.config.KafkaAdminClientProperties;
import com.manus.kafka.core.factory.KafkaAdminClientFactory;
import org.apache.kafka.clients.admin.AdminClient;

import java.util.Map;

/**
 * Builder for creating Kafka AdminClient instances in a J2EE environment.
 */
public class J2EEKafkaAdminClientBuilder {
    
    private final KafkaAdminClientProperties properties;
    
    /**
     * Creates a new instance of J2EEKafkaAdminClientBuilder with default properties.
     */
    public J2EEKafkaAdminClientBuilder() {
        this.properties = new KafkaAdminClientProperties();
    }
    
    /**
     * Creates a new instance of J2EEKafkaAdminClientBuilder with the specified properties.
     *
     * @param properties the admin client properties
     */
    public J2EEKafkaAdminClientBuilder(KafkaAdminClientProperties properties) {
        this.properties = properties;
    }
    
    /**
     * Sets the bootstrap servers.
     *
     * @param bootstrapServers comma-separated list of host:port pairs
     * @return this instance for method chaining
     */
    public J2EEKafkaAdminClientBuilder bootstrapServers(String bootstrapServers) {
        properties.bootstrapServers(bootstrapServers);
        return this;
    }
    
    /**
     * Sets the client ID.
     *
     * @param clientId the client ID
     * @return this instance for method chaining
     */
    public J2EEKafkaAdminClientBuilder clientId(String clientId) {
        properties.clientId(clientId);
        return this;
    }
    
    /**
     * Sets the request timeout in milliseconds.
     *
     * @param requestTimeoutMs the request timeout in milliseconds
     * @return this instance for method chaining
     */
    public J2EEKafkaAdminClientBuilder requestTimeoutMs(int requestTimeoutMs) {
        properties.requestTimeoutMs(requestTimeoutMs);
        return this;
    }
    
    /**
     * Sets the number of retries.
     *
     * @param retries the number of retries
     * @return this instance for method chaining
     */
    public J2EEKafkaAdminClientBuilder retries(int retries) {
        properties.retries(retries);
        return this;
    }
    
    /**
     * Sets the retry backoff in milliseconds.
     *
     * @param retryBackoffMs the retry backoff in milliseconds
     * @return this instance for method chaining
     */
    public J2EEKafkaAdminClientBuilder retryBackoffMs(long retryBackoffMs) {
        properties.retryBackoffMs(retryBackoffMs);
        return this;
    }
    
    /**
     * Sets the connections max idle time in milliseconds.
     *
     * @param connectionsMaxIdleMs the connections max idle time in milliseconds
     * @return this instance for method chaining
     */
    public J2EEKafkaAdminClientBuilder connectionsMaxIdleMs(long connectionsMaxIdleMs) {
        properties.connectionsMaxIdleMs(connectionsMaxIdleMs);
        return this;
    }
    
    /**
     * Sets the security protocol.
     *
     * @param securityProtocol the security protocol (PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL)
     * @return this instance for method chaining
     */
    public J2EEKafkaAdminClientBuilder securityProtocol(String securityProtocol) {
        properties.securityProtocol(securityProtocol);
        return this;
    }
    
    /**
     * Sets the SASL mechanism.
     *
     * @param saslMechanism the SASL mechanism (GSSAPI, PLAIN, SCRAM-SHA-256, SCRAM-SHA-512, OAUTHBEARER)
     * @return this instance for method chaining
     */
    public J2EEKafkaAdminClientBuilder saslMechanism(String saslMechanism) {
        properties.saslMechanism(saslMechanism);
        return this;
    }
    
    /**
     * Sets the SASL JAAS config.
     *
     * @param saslJaasConfig the SASL JAAS config
     * @return this instance for method chaining
     */
    public J2EEKafkaAdminClientBuilder saslJaasConfig(String saslJaasConfig) {
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
    public J2EEKafkaAdminClientBuilder property(String key, Object value) {
        properties.property(key, value);
        return this;
    }
    
    /**
     * Sets multiple custom properties.
     *
     * @param props the properties to set
     * @return this instance for method chaining
     */
    public J2EEKafkaAdminClientBuilder properties(Map<String, Object> props) {
        properties.properties(props);
        return this;
    }
    
    /**
     * Builds a Kafka AdminClient with the configured properties.
     *
     * @return a new Kafka AdminClient
     */
    public AdminClient build() {
        KafkaAdminClientFactory factory = new KafkaAdminClientFactory(properties);
        return factory.createAdminClient();
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