package com.manus.kafka.j2ee.lifecycle;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Lifecycle manager for Kafka clients in a J2EE environment.
 * This class can be used as a ServletContextListener to automatically close Kafka clients when the application is shutting down.
 */
public class KafkaLifecycleManager implements ServletContextListener {

    private static final KafkaLifecycleManager INSTANCE = new KafkaLifecycleManager();

    private final List<Producer<?, ?>> producers = new CopyOnWriteArrayList<>();
    private final List<Consumer<?, ?>> consumers = new CopyOnWriteArrayList<>();
    private final List<AdminClient> adminClients = new CopyOnWriteArrayList<>();

    /**
     * Gets the singleton instance of KafkaLifecycleManager.
     *
     * @return the singleton instance
     */
    public static KafkaLifecycleManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a producer to be managed by this lifecycle manager.
     *
     * @param producer the producer to register
     */
    public void registerProducer(Producer<?, ?> producer) {
        if (producer != null) {
            producers.add(producer);
        }
    }

    /**
     * Registers a consumer to be managed by this lifecycle manager.
     *
     * @param consumer the consumer to register
     */
    public void registerConsumer(Consumer<?, ?> consumer) {
        if (consumer != null) {
            consumers.add(consumer);
        }
    }

    /**
     * Registers an admin client to be managed by this lifecycle manager.
     *
     * @param adminClient the admin client to register
     */
    public void registerAdminClient(AdminClient adminClient) {
        if (adminClient != null) {
            adminClients.add(adminClient);
        }
    }

    /**
     * Unregisters a producer from this lifecycle manager.
     *
     * @param producer the producer to unregister
     */
    public void unregisterProducer(Producer<?, ?> producer) {
        if (producer != null) {
            producers.remove(producer);
        }
    }

    /**
     * Unregisters a consumer from this lifecycle manager.
     *
     * @param consumer the consumer to unregister
     */
    public void unregisterConsumer(Consumer<?, ?> consumer) {
        if (consumer != null) {
            consumers.remove(consumer);
        }
    }

    /**
     * Unregisters an admin client from this lifecycle manager.
     *
     * @param adminClient the admin client to unregister
     */
    public void unregisterAdminClient(AdminClient adminClient) {
        if (adminClient != null) {
            adminClients.remove(adminClient);
        }
    }

    /**
     * Closes all registered Kafka clients.
     */
    public void closeAll() {
        List<Exception> exceptions = new ArrayList<>();

        // Close producers
        for (Producer<?, ?> producer : producers) {
            try {
                producer.close();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        producers.clear();

        // Close consumers
        for (Consumer<?, ?> consumer : consumers) {
            try {
                consumer.close();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        consumers.clear();

        // Close admin clients
        for (AdminClient adminClient : adminClients) {
            try {
                adminClient.close();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        adminClients.clear();

        // If there were exceptions, throw a runtime exception with the first one as the cause
        if (!exceptions.isEmpty()) {
            RuntimeException ex = new RuntimeException("Failed to close one or more Kafka clients");
            ex.initCause(exceptions.get(0));
            throw ex;
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Nothing to do here
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        closeAll();
    }
}
