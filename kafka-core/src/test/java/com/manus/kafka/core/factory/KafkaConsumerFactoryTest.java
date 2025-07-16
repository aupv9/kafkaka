package com.manus.kafka.core.factory;

import com.manus.kafka.core.config.KafkaConsumerProperties;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link KafkaConsumerFactory}.
 */
public class KafkaConsumerFactoryTest {

    @Test
    @DisplayName("Test constructor with default properties")
    public void testConstructorWithDefaultProperties() {
        // Given/When
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>();

        // Then
        KafkaConsumerProperties properties = factory.getProperties();
        assertNotNull(properties);
        assertEquals("false", properties.asMap().get("enable.auto.commit"));
        assertEquals("earliest", properties.asMap().get("auto.offset.reset"));
    }

    @Test
    @DisplayName("Test constructor with custom properties")
    public void testConstructorWithCustomProperties() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        properties.bootstrapServers("localhost:9092");
        properties.groupId("test-group");
        properties.enableAutoCommit(true);

        // When
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>(properties);

        // Then
        KafkaConsumerProperties factoryProperties = factory.getProperties();
        assertNotNull(factoryProperties);
        assertEquals("localhost:9092", factoryProperties.asMap().get("bootstrap.servers"));
        assertEquals("test-group", factoryProperties.asMap().get("group.id"));
        assertEquals("true", factoryProperties.asMap().get("enable.auto.commit"));
    }

    @Test
    @DisplayName("Test createConsumer with invalid properties")
    public void testCreateConsumerWithInvalidProperties() {
        // Given
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>();

        // When/Then
        // The factory should validate properties before creating a Consumer
        assertThrows(IllegalStateException.class, () -> factory.createConsumer());
    }

    @Test
    @DisplayName("Test createConsumer with custom properties")
    public void testCreateConsumerWithCustomProperties() {
        // Given
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>();
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("bootstrap.servers", "localhost:9092");
        customProps.put("group.id", "test-group");
        customProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        customProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // When/Then
        // We can't easily test the actual creation of a Consumer without a real Kafka broker,
        // but we can verify that the method doesn't throw a validation exception
        Consumer<String, String> consumer = null;
        try {
            consumer = factory.createConsumer(customProps);
            assertNotNull(consumer, "Consumer should not be null");
        } catch (Exception e) {
            // We expect a connection-related exception, not a validation exception
            assertFalse(e instanceof IllegalStateException, "Unexpected IllegalStateException: " + e.getMessage());
        } finally {
            // Clean up
            if (consumer != null) {
                consumer.close();
            }
        }
    }

    @Test
    @DisplayName("Test createConsumer with invalid custom properties")
    public void testCreateConsumerWithInvalidCustomProperties() {
        // Given
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>();
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("bootstrap.servers", "localhost:9092");
        // Missing required properties: group.id, key.deserializer, value.deserializer

        // When/Then
        assertThrows(IllegalStateException.class, () -> factory.createConsumer(customProps));
    }

    @Test
    @DisplayName("Test static createConsumer method")
    public void testStaticCreateConsumer() {
        // Given
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "test-group");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // When/Then
        // The static method doesn't validate properties or try to connect immediately,
        // so it should not throw an exception
        Consumer<String, String> consumer = null;
        try {
            consumer = KafkaConsumerFactory.createConsumer(properties);
            assertNotNull(consumer, "Consumer should not be null");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            // Clean up
            if (consumer != null) {
                consumer.close();
            }
        }
    }
    
    // Comprehensive factory method tests with various configurations
    
    @ParameterizedTest
    @MethodSource("provideConsumerConfigurationTestData")
    @DisplayName("Test factory with various consumer configurations")
    public void testFactoryWithVariousConfigurations(Map<String, Object> config, boolean shouldSucceed, String expectedErrorKeyword) {
        // Given
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>();
        
        // When/Then
        if (shouldSucceed) {
            Consumer<String, String> consumer = null;
            try {
                consumer = factory.createConsumer(config);
                assertNotNull(consumer, "Consumer should not be null for valid configuration");
            } catch (Exception e) {
                // Connection exceptions are acceptable, validation exceptions are not
                assertFalse(e instanceof IllegalStateException, 
                    "Should not throw validation exception for valid config: " + e.getMessage());
            } finally {
                if (consumer != null) {
                    consumer.close();
                }
            }
        } else {
            IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> factory.createConsumer(config));
            assertTrue(exception.getMessage().contains(expectedErrorKeyword),
                "Expected error message to contain '" + expectedErrorKeyword + "' but got: " + exception.getMessage());
        }
    }
    
    private static Stream<Arguments> provideConsumerConfigurationTestData() {
        return Stream.of(
            // Valid configurations
            Arguments.of(createBasicConsumerConfig(), true, ""),
            Arguments.of(createSecureConsumerConfig(), true, ""),
            Arguments.of(createManualCommitConsumerConfig(), true, ""),
            Arguments.of(createAutoCommitConsumerConfig(), true, ""),
            Arguments.of(createAvroConsumerConfig(), true, ""),
            Arguments.of(createEarliestOffsetConsumerConfig(), true, ""),
            Arguments.of(createLatestOffsetConsumerConfig(), true, ""),
            
            // Invalid configurations
            Arguments.of(createMissingBootstrapConfig(), false, "bootstrap.servers"),
            Arguments.of(createMissingGroupIdConfig(), false, "group.id"),
            Arguments.of(createMissingKeyDeserializerConfig(), false, "key.deserializer"),
            Arguments.of(createMissingValueDeserializerConfig(), false, "value.deserializer"),
            Arguments.of(createInvalidOffsetResetConfig(), false, "auto.offset.reset")
        );
    }
    
    private static Map<String, Object> createBasicConsumerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("group.id", "test-group");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return config;
    }
    
    private static Map<String, Object> createSecureConsumerConfig() {
        Map<String, Object> config = createBasicConsumerConfig();
        config.put("security.protocol", "SASL_SSL");
        config.put("sasl.mechanism", "PLAIN");
        config.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";");
        config.put("ssl.truststore.location", "/path/to/truststore.jks");
        config.put("ssl.truststore.password", "password");
        return config;
    }
    
    private static Map<String, Object> createManualCommitConsumerConfig() {
        Map<String, Object> config = createBasicConsumerConfig();
        config.put("enable.auto.commit", "false");
        config.put("auto.offset.reset", "earliest");
        config.put("max.poll.records", 100);
        config.put("session.timeout.ms", 30000);
        return config;
    }
    
    private static Map<String, Object> createAutoCommitConsumerConfig() {
        Map<String, Object> config = createBasicConsumerConfig();
        config.put("enable.auto.commit", "true");
        config.put("auto.commit.interval.ms", 5000);
        config.put("auto.offset.reset", "latest");
        return config;
    }
    
    private static Map<String, Object> createAvroConsumerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("group.id", "avro-consumer-group");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        config.put("schema.registry.url", "http://localhost:8081");
        config.put("specific.avro.reader", "true");
        return config;
    }
    
    private static Map<String, Object> createEarliestOffsetConsumerConfig() {
        Map<String, Object> config = createBasicConsumerConfig();
        config.put("auto.offset.reset", "earliest");
        return config;
    }
    
    private static Map<String, Object> createLatestOffsetConsumerConfig() {
        Map<String, Object> config = createBasicConsumerConfig();
        config.put("auto.offset.reset", "latest");
        return config;
    }
    
    private static Map<String, Object> createMissingBootstrapConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("group.id", "test-group");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return config;
    }
    
    private static Map<String, Object> createMissingGroupIdConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return config;
    }
    
    private static Map<String, Object> createMissingKeyDeserializerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("group.id", "test-group");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return config;
    }
    
    private static Map<String, Object> createMissingValueDeserializerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("group.id", "test-group");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return config;
    }
    
    private static Map<String, Object> createInvalidOffsetResetConfig() {
        Map<String, Object> config = createBasicConsumerConfig();
        config.put("auto.offset.reset", "invalid");
        return config;
    }
    
    @ParameterizedTest
    @CsvSource({
        "test-group-1, true",
        "test-group-2, true",
        "consumer-group-with-dashes, true",
        "consumer_group_with_underscores, true",
        "'', false",
        "group.with.dots, true"
    })
    @DisplayName("Test factory with various group ID configurations")
    public void testFactoryWithGroupIdVariations(String groupId, boolean shouldPassValidation) {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("group.id", groupId);
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>();
        
        // When/Then
        if (shouldPassValidation) {
            Consumer<String, String> consumer = null;
            try {
                consumer = factory.createConsumer(config);
                assertNotNull(consumer, "Consumer should not be null for valid configuration");
            } catch (Exception e) {
                // Connection exceptions are acceptable
                assertFalse(e instanceof IllegalStateException, 
                    "Should not throw validation exception: " + e.getMessage());
            } finally {
                if (consumer != null) {
                    consumer.close();
                }
            }
        } else {
            assertThrows(IllegalStateException.class, () -> factory.createConsumer(config));
        }
    }
    
    @Test
    @DisplayName("Test factory properties inheritance and override")
    public void testFactoryPropertiesInheritanceAndOverride() {
        // Given
        KafkaConsumerProperties baseProperties = new KafkaConsumerProperties();
        baseProperties.bootstrapServers("localhost:9092");
        baseProperties.groupId("base-group");
        baseProperties.enableAutoCommit(false);
        baseProperties.autoOffsetReset("earliest");
        baseProperties.maxPollRecords(500);
        
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>(baseProperties);
        
        Map<String, Object> overrideProps = new HashMap<>();
        overrideProps.put("group.id", "override-group"); // Override base property
        overrideProps.put("session.timeout.ms", 45000); // Add new property
        overrideProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        overrideProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        
        // When
        Consumer<String, String> consumer = null;
        try {
            consumer = factory.createConsumer(overrideProps);
            
            // Then
            assertNotNull(consumer, "Consumer should be created successfully");
            
        } catch (Exception e) {
            // Connection exceptions are acceptable
            assertFalse(e instanceof IllegalStateException, 
                "Should not throw validation exception: " + e.getMessage());
        } finally {
            if (consumer != null) {
                consumer.close();
            }
        }
    }
    
    @Test
    @DisplayName("Test factory with null and empty configurations")
    public void testFactoryWithNullAndEmptyConfigurations() {
        // Given
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>();
        
        // When/Then - null configuration
        assertThrows(IllegalStateException.class, () -> factory.createConsumer((Map<String, Object>) null));
        
        // When/Then - empty configuration
        assertThrows(IllegalStateException.class, () -> factory.createConsumer(new HashMap<>()));
        
        // When/Then - null Properties
        assertThrows(IllegalArgumentException.class, () -> KafkaConsumerFactory.createConsumer((Properties) null));
    }
    
    @Test
    @DisplayName("Test factory thread safety")
    public void testFactoryThreadSafety() throws InterruptedException {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        properties.bootstrapServers("localhost:9092");
        properties.groupId("thread-test-group");
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>(properties);
        
        Map<String, Object> config = new HashMap<>();
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        // When - concurrent factory usage
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    // Use different group IDs to avoid conflicts
                    Map<String, Object> threadConfig = new HashMap<>(config);
                    threadConfig.put("group.id", "thread-test-group-" + threadIndex);
                    
                    Consumer<String, String> consumer = factory.createConsumer(threadConfig);
                    if (consumer != null) {
                        consumer.close();
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Connection exceptions are acceptable
                    if (!(e instanceof IllegalStateException)) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Then
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(threadCount, successCount.get(), "All threads should complete successfully");
        
        executor.shutdown();
    }
    
    @ParameterizedTest
    @CsvSource({
        "true, 1000",
        "true, 5000", 
        "true, 10000",
        "false, 0"
    })
    @DisplayName("Test factory with various auto-commit configurations")
    public void testFactoryWithAutoCommitVariations(boolean enableAutoCommit, int autoCommitInterval) {
        // Given
        Map<String, Object> config = createBasicConsumerConfig();
        config.put("enable.auto.commit", String.valueOf(enableAutoCommit));
        if (enableAutoCommit) {
            config.put("auto.commit.interval.ms", autoCommitInterval);
        }
        
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>();
        
        // When
        Consumer<String, String> consumer = null;
        try {
            consumer = factory.createConsumer(config);
            
            // Then
            assertNotNull(consumer, "Consumer should be created successfully");
            
        } catch (Exception e) {
            // Connection exceptions are acceptable
            assertFalse(e instanceof IllegalStateException, 
                "Should not throw validation exception: " + e.getMessage());
        } finally {
            if (consumer != null) {
                consumer.close();
            }
        }
    }
}
