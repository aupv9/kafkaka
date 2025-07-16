package com.manus.kafka.j2ee.lifecycle;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import jakarta.servlet.ServletContextEvent;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;

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
    
    // Concurrent testing scenarios
    
    @RepeatedTest(10)
    @DisplayName("Test concurrent registration and unregistration")
    @Timeout(5)
    public void testConcurrentRegistrationAndUnregistration() throws InterruptedException {
        // Given
        int threadCount = 10;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        List<Producer<String, String>> producers = new ArrayList<>();
        List<Consumer<String, String>> consumers = new ArrayList<>();
        List<AdminClient> adminClients = new ArrayList<>();
        
        // Create mock clients
        for (int i = 0; i < operationsPerThread; i++) {
            producers.add(mock(Producer.class));
            consumers.add(mock(Consumer.class));
            adminClients.add(mock(AdminClient.class));
        }
        
        // When - concurrent registration and unregistration
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        int index = threadIndex * operationsPerThread + j;
                        if (index < producers.size()) {
                            // Register clients
                            lifecycleManager.registerProducer(producers.get(index));
                            lifecycleManager.registerConsumer(consumers.get(index));
                            lifecycleManager.registerAdminClient(adminClients.get(index));
                            
                            // Randomly unregister some clients
                            if (j % 3 == 0) {
                                lifecycleManager.unregisterProducer(producers.get(index));
                            }
                            if (j % 5 == 0) {
                                lifecycleManager.unregisterConsumer(consumers.get(index));
                            }
                            if (j % 7 == 0) {
                                lifecycleManager.unregisterAdminClient(adminClients.get(index));
                            }
                        }
                    }
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Then
        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertEquals(threadCount, successCount.get());
        
        // Cleanup
        lifecycleManager.closeAll();
        executor.shutdown();
    }
    
    @Test
    @DisplayName("Test concurrent closeAll operations")
    @Timeout(5)
    public void testConcurrentCloseAll() throws InterruptedException {
        // Given
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);
        
        // Register some clients
        for (int i = 0; i < 10; i++) {
            lifecycleManager.registerProducer(mock(Producer.class));
            lifecycleManager.registerConsumer(mock(Consumer.class));
            lifecycleManager.registerAdminClient(mock(AdminClient.class));
        }
        
        // When - concurrent closeAll operations
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    lifecycleManager.closeAll();
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Then
        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertTrue(successCount.get() + exceptionCount.get() == threadCount);
        
        executor.shutdown();
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 50, 100})
    @DisplayName("Test lifecycle manager with varying numbers of clients")
    public void testWithVaryingClientCounts(int clientCount) {
        // Given
        List<Producer<String, String>> producers = new ArrayList<>();
        List<Consumer<String, String>> consumers = new ArrayList<>();
        List<AdminClient> adminClients = new ArrayList<>();
        
        for (int i = 0; i < clientCount; i++) {
            producers.add(mock(Producer.class));
            consumers.add(mock(Consumer.class));
            adminClients.add(mock(AdminClient.class));
        }
        
        // When
        for (int i = 0; i < clientCount; i++) {
            lifecycleManager.registerProducer(producers.get(i));
            lifecycleManager.registerConsumer(consumers.get(i));
            lifecycleManager.registerAdminClient(adminClients.get(i));
        }
        
        lifecycleManager.closeAll();
        
        // Then
        for (int i = 0; i < clientCount; i++) {
            verify(producers.get(i)).close();
            verify(consumers.get(i)).close();
            verify(adminClients.get(i)).close();
        }
    }
    
    @Test
    @DisplayName("Test concurrent registration with mixed client types")
    @Timeout(5)
    public void testConcurrentMixedRegistration() throws InterruptedException {
        // Given
        int threadCount = 6;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger totalOperations = new AtomicInteger(0);
        
        // When - each thread registers different types of clients
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 50; j++) {
                        switch (threadIndex % 3) {
                            case 0:
                                lifecycleManager.registerProducer(mock(Producer.class));
                                break;
                            case 1:
                                lifecycleManager.registerConsumer(mock(Consumer.class));
                                break;
                            case 2:
                                lifecycleManager.registerAdminClient(mock(AdminClient.class));
                                break;
                        }
                        totalOperations.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Then
        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertEquals(threadCount * 50, totalOperations.get());
        
        // Cleanup
        lifecycleManager.closeAll();
        executor.shutdown();
    }
    
    @Test
    @DisplayName("Test exception handling during concurrent close operations")
    @Timeout(5)
    public void testExceptionHandlingDuringConcurrentClose() throws InterruptedException {
        // Given
        Producer<String, String> faultyProducer = mock(Producer.class);
        Consumer<String, String> faultyConsumer = mock(Consumer.class);
        AdminClient faultyAdminClient = mock(AdminClient.class);
        
        doThrow(new RuntimeException("Producer close failed")).when(faultyProducer).close();
        doThrow(new RuntimeException("Consumer close failed")).when(faultyConsumer).close();
        doThrow(new RuntimeException("AdminClient close failed")).when(faultyAdminClient).close();
        
        lifecycleManager.registerProducer(faultyProducer);
        lifecycleManager.registerConsumer(faultyConsumer);
        lifecycleManager.registerAdminClient(faultyAdminClient);
        
        // Add some normal clients too
        for (int i = 0; i < 5; i++) {
            lifecycleManager.registerProducer(mock(Producer.class));
            lifecycleManager.registerConsumer(mock(Consumer.class));
            lifecycleManager.registerAdminClient(mock(AdminClient.class));
        }
        
        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger exceptionCount = new AtomicInteger(0);
        
        // When - concurrent close operations with exceptions
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    lifecycleManager.closeAll();
                } catch (RuntimeException e) {
                    exceptionCount.incrementAndGet();
                    assertEquals("Failed to close one or more Kafka clients", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Then
        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertTrue(exceptionCount.get() > 0, "Expected at least one exception to be thrown");
        
        executor.shutdown();
    }
    
    @Test
    @DisplayName("Test thread safety of singleton instance")
    @Timeout(5)
    public void testSingletonThreadSafety() throws InterruptedException {
        // Given
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        ConcurrentHashMap<KafkaLifecycleManager, Integer> instances = new ConcurrentHashMap<>();
        
        // When - multiple threads get the singleton instance
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    KafkaLifecycleManager instance = KafkaLifecycleManager.getInstance();
                    instances.merge(instance, 1, Integer::sum);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Then
        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertEquals(1, instances.size(), "Should have only one singleton instance");
        assertEquals(threadCount, instances.values().iterator().next().intValue(), 
            "All threads should get the same instance");
        
        executor.shutdown();
    }
}