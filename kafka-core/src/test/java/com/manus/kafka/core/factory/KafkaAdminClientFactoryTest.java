package com.manus.kafka.core.factory;

import com.manus.kafka.core.config.KafkaAdminClientProperties;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link KafkaAdminClientFactory}.
 */
public class KafkaAdminClientFactoryTest {

    @Test
    @DisplayName("Test constructor with default properties")
    public void testConstructorWithDefaultProperties() {
        // Given/When
        KafkaAdminClientFactory factory = new KafkaAdminClientFactory();

        // Then
        KafkaAdminClientProperties properties = factory.getProperties();
        assertNotNull(properties);
        assertEquals(30000, properties.asMap().get("request.timeout.ms"));
        assertEquals(5, properties.asMap().get("retries"));
    }

    @Test
    @DisplayName("Test constructor with custom properties")
    public void testConstructorWithCustomProperties() {
        // Given
        KafkaAdminClientProperties properties = new KafkaAdminClientProperties();
        properties.bootstrapServers("localhost:9092");
        properties.requestTimeoutMs(60000);

        // When
        KafkaAdminClientFactory factory = new KafkaAdminClientFactory(properties);

        // Then
        KafkaAdminClientProperties factoryProperties = factory.getProperties();
        assertNotNull(factoryProperties);
        assertEquals("localhost:9092", factoryProperties.asMap().get("bootstrap.servers"));
        assertEquals(60000, factoryProperties.asMap().get("request.timeout.ms"));
    }

    @Test
    @DisplayName("Test createAdminClient with custom properties")
    public void testCreateAdminClientWithCustomProperties() {
        // Given
        KafkaAdminClientFactory factory = new KafkaAdminClientFactory();
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("bootstrap.servers", "localhost:9092");
        customProps.put("request.timeout.ms", 60000);

        // When/Then
        // We can't easily test the actual creation of an AdminClient without a real Kafka broker,
        // but we can verify that the method doesn't throw an exception when the properties are valid
        try {
            factory.createAdminClient(customProps);
            // If we get here, the test passes - no exception was thrown during validation
        } catch (Exception e) {
            // We expect a connection-related exception, not a validation exception
            assertFalse(e instanceof IllegalStateException, "Unexpected IllegalStateException: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test createAdminClient with invalid properties")
    public void testCreateAdminClientWithInvalidProperties() {
        // Given
        KafkaAdminClientFactory factory = new KafkaAdminClientFactory();

        // When/Then
        // The factory should validate properties before creating an AdminClient
        assertThrows(IllegalStateException.class, () -> factory.createAdminClient());
    }

    @Test
    @DisplayName("Test static createAdminClient method")
    public void testStaticCreateAdminClient() {
        // Given
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");

        // When/Then
        // The static method doesn't validate properties or try to connect immediately,
        // so it should not throw an exception
        AdminClient adminClient = null;
        try {
            adminClient = KafkaAdminClientFactory.createAdminClient(properties);
            assertNotNull(adminClient, "AdminClient should not be null");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            // Clean up
            if (adminClient != null) {
                adminClient.close();
            }
        }
    }
}
