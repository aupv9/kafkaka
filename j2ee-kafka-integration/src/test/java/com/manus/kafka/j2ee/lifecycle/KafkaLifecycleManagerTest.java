package com.manus.kafka.j2ee.lifecycle;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import jakarta.servlet.ServletContextEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link KafkaLifecycleManager}.
 */
public class KafkaLifecycleManagerTest {

    private KafkaLifecycleManager lifecycleManager;
    private Producer<String, String> mockProducer;
    private Consumer<String, String> mockConsumer;
    private AdminClient mockAdminClient;
    private ServletContextEvent mockServletContextEvent;

    @BeforeEach
    public void setUp() {
        lifecycleManager = KafkaLifecycleManager.getInstance();
        mockProducer = mock(Producer.class);
        mockConsumer = mock(Consumer.class);
        mockAdminClient = mock(AdminClient.class);
        mockServletContextEvent = mock(ServletContextEvent.class);
        
        // Clear any previously registered clients
        lifecycleManager.closeAll();
    }

    @Test
    @DisplayName("Test getInstance returns singleton instance")
    public void testGetInstance() {
        // When
        KafkaLifecycleManager instance1 = KafkaLifecycleManager.getInstance();
        KafkaLifecycleManager instance2 = KafkaLifecycleManager.getInstance();
        
        // Then
        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("Test registerProducer adds producer")
    public void testRegisterProducer() {
        // When
        lifecycleManager.registerProducer(mockProducer);
        
        // Then - verify that the producer is closed when closeAll is called
        lifecycleManager.closeAll();
        verify(mockProducer).close();
    }

    @Test
    @DisplayName("Test registerConsumer adds consumer")
    public void testRegisterConsumer() {
        // When
        lifecycleManager.registerConsumer(mockConsumer);
        
        // Then - verify that the consumer is closed when closeAll is called
        lifecycleManager.closeAll();
        verify(mockConsumer).close();
    }

    @Test
    @DisplayName("Test registerAdminClient adds admin client")
    public void testRegisterAdminClient() {
        // When
        lifecycleManager.registerAdminClient(mockAdminClient);
        
        // Then - verify that the admin client is closed when closeAll is called
        lifecycleManager.closeAll();
        verify(mockAdminClient).close();
    }

    @Test
    @DisplayName("Test unregisterProducer removes producer")
    public void testUnregisterProducer() {
        // Given
        lifecycleManager.registerProducer(mockProducer);
        
        // When
        lifecycleManager.unregisterProducer(mockProducer);
        
        // Then - verify that the producer is not closed when closeAll is called
        lifecycleManager.closeAll();
        verify(mockProducer, never()).close();
    }

    @Test
    @DisplayName("Test unregisterConsumer removes consumer")
    public void testUnregisterConsumer() {
        // Given
        lifecycleManager.registerConsumer(mockConsumer);
        
        // When
        lifecycleManager.unregisterConsumer(mockConsumer);
        
        // Then - verify that the consumer is not closed when closeAll is called
        lifecycleManager.closeAll();
        verify(mockConsumer, never()).close();
    }

    @Test
    @DisplayName("Test unregisterAdminClient removes admin client")
    public void testUnregisterAdminClient() {
        // Given
        lifecycleManager.registerAdminClient(mockAdminClient);
        
        // When
        lifecycleManager.unregisterAdminClient(mockAdminClient);
        
        // Then - verify that the admin client is not closed when closeAll is called
        lifecycleManager.closeAll();
        verify(mockAdminClient, never()).close();
    }

    @Test
    @DisplayName("Test closeAll closes all registered clients")
    public void testCloseAll() {
        // Given
        lifecycleManager.registerProducer(mockProducer);
        lifecycleManager.registerConsumer(mockConsumer);
        lifecycleManager.registerAdminClient(mockAdminClient);
        
        // When
        lifecycleManager.closeAll();
        
        // Then
        verify(mockProducer).close();
        verify(mockConsumer).close();
        verify(mockAdminClient).close();
    }

    @Test
    @DisplayName("Test closeAll handles exceptions")
    public void testCloseAllHandlesExceptions() {
        // Given
        Producer<String, String> mockProducerWithException = mock(Producer.class);
        doThrow(new RuntimeException("Producer close failed")).when(mockProducerWithException).close();
        
        lifecycleManager.registerProducer(mockProducerWithException);
        lifecycleManager.registerConsumer(mockConsumer);
        lifecycleManager.registerAdminClient(mockAdminClient);
        
        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> lifecycleManager.closeAll());
        assertEquals("Failed to close one or more Kafka clients", exception.getMessage());
        
        // Verify that all clients were still attempted to be closed
        verify(mockProducerWithException).close();
        verify(mockConsumer).close();
        verify(mockAdminClient).close();
    }

    @Test
    @DisplayName("Test contextDestroyed calls closeAll")
    public void testContextDestroyed() {
        // Given
        lifecycleManager.registerProducer(mockProducer);
        lifecycleManager.registerConsumer(mockConsumer);
        lifecycleManager.registerAdminClient(mockAdminClient);
        
        // When
        lifecycleManager.contextDestroyed(mockServletContextEvent);
        
        // Then
        verify(mockProducer).close();
        verify(mockConsumer).close();
        verify(mockAdminClient).close();
    }

    @Test
    @DisplayName("Test contextInitialized does nothing")
    public void testContextInitialized() {
        // When
        lifecycleManager.contextInitialized(mockServletContextEvent);
        
        // Then - no exceptions should be thrown
    }

    @Test
    @DisplayName("Test register with null client does nothing")
    public void testRegisterWithNullClient() {
        // When
        lifecycleManager.registerProducer(null);
        lifecycleManager.registerConsumer(null);
        lifecycleManager.registerAdminClient(null);
        
        // Then - no exceptions should be thrown
        lifecycleManager.closeAll();
    }

    @Test
    @DisplayName("Test unregister with null client does nothing")
    public void testUnregisterWithNullClient() {
        // When
        lifecycleManager.unregisterProducer(null);
        lifecycleManager.unregisterConsumer(null);
        lifecycleManager.unregisterAdminClient(null);
        
        // Then - no exceptions should be thrown
    }
}