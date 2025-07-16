package com.manus.kafka.j2ee.builder;

import com.manus.kafka.core.config.KafkaAdminClientProperties;
import com.manus.kafka.core.factory.KafkaAdminClientFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedConstruction;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link J2EEKafkaAdminClientBuilder}.
 */
public class J2EEKafkaAdminClientBuilderTest {

    private AdminClient mockAdminClient;
    
    @BeforeEach
    public void setUp() {
        mockAdminClient = mock(AdminClient.class);
    }

    @Test
    @DisplayName("Test default constructor creates default properties")
    public void testDefaultConstructor() {
        // When
        J2EEKafkaAdminClientBuilder builder = new J2EEKafkaAdminClientBuilder();
        
        // Then
        KafkaAdminClientProperties properties = builder.getProperties();
        assertNotNull(properties);
        assertEquals(30000, properties.asMap().get("request.timeout.ms"));
        assertEquals(5, properties.asMap().get("retries"));
    }

    @Test
    @DisplayName("Test constructor with properties")
    public void testConstructorWithProperties() {
        // Given
        KafkaAdminClientProperties properties = new KafkaAdminClientProperties();
        properties.bootstrapServers("localhost:9092");
        
        // When
        J2EEKafkaAdminClientBuilder builder = new J2EEKafkaAdminClientBuilder(properties);
        
        // Then
        assertSame(properties, builder.getProperties());
        assertEquals("localhost:9092", properties.asMap().get("bootstrap.servers"));
    }

    @Test
    @DisplayName("Test builder methods set properties correctly")
    public void testBuilderMethods() {
        // When
        J2EEKafkaAdminClientBuilder builder = new J2EEKafkaAdminClientBuilder()
            .bootstrapServers("localhost:9092")
            .clientId("test-client")
            .requestTimeoutMs(60000)
            .retries(10)
            .retryBackoffMs(1000)
            .connectionsMaxIdleMs(600000)
            .securityProtocol("SASL_SSL")
            .saslMechanism("PLAIN")
            .saslJaasConfig("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";");
        
        // Then
        KafkaAdminClientProperties properties = builder.getProperties();
        Map<String, Object> map = properties.asMap();
        assertEquals("localhost:9092", map.get("bootstrap.servers"));
        assertEquals("test-client", map.get("client.id"));
        assertEquals(60000, map.get("request.timeout.ms"));
        assertEquals(10, map.get("retries"));
        assertEquals(1000L, map.get("retry.backoff.ms"));
        assertEquals(600000L, map.get("connections.max.idle.ms"));
        assertEquals("SASL_SSL", map.get("security.protocol"));
        assertEquals("PLAIN", map.get("sasl.mechanism"));
        assertEquals("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";", 
                     map.get("sasl.jaas.config"));
    }

    @Test
    @DisplayName("Test property method sets custom property")
    public void testPropertyMethod() {
        // When
        J2EEKafkaAdminClientBuilder builder = new J2EEKafkaAdminClientBuilder()
            .property("custom.property", "custom-value");
        
        // Then
        assertEquals("custom-value", builder.getProperties().asMap().get("custom.property"));
    }

    @Test
    @DisplayName("Test properties method sets multiple custom properties")
    public void testPropertiesMethod() {
        // Given
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("prop1", "value1");
        customProps.put("prop2", "value2");
        
        // When
        J2EEKafkaAdminClientBuilder builder = new J2EEKafkaAdminClientBuilder()
            .properties(customProps);
        
        // Then
        assertEquals("value1", builder.getProperties().asMap().get("prop1"));
        assertEquals("value2", builder.getProperties().asMap().get("prop2"));
    }

    @Test
    @DisplayName("Test build method creates admin client")
    public void testBuild() {
        try (MockedConstruction<KafkaAdminClientFactory> mocked = mockConstruction(
                KafkaAdminClientFactory.class,
                (mock, context) -> {
                    when(mock.createAdminClient()).thenReturn(mockAdminClient);
                })) {
            
            // Given
            J2EEKafkaAdminClientBuilder builder = new J2EEKafkaAdminClientBuilder()
                .bootstrapServers("localhost:9092")
                .clientId("test-client");
            
            // When
            AdminClient adminClient = builder.build();
            
            // Then
            assertSame(mockAdminClient, adminClient);
            assertEquals(1, mocked.constructed().size());
            KafkaAdminClientFactory factory = mocked.constructed().get(0);
            verify(factory).createAdminClient();
            verifyNoMoreInteractions(factory);
        }
    }
}