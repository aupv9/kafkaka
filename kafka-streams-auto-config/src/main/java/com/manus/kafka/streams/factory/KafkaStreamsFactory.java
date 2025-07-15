package com.manus.kafka.streams.factory;

import com.manus.kafka.streams.config.KafkaStreamsProperties;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import java.util.Map;
import java.util.Properties;

/**
 * Factory for creating Kafka Streams instances.
 * This factory provides methods to create Kafka Streams with different configurations.
 */
public class KafkaStreamsFactory {
    
    private final KafkaStreamsProperties properties;
    
    /**
     * Creates a new instance of KafkaStreamsFactory with default properties.
     */
    public KafkaStreamsFactory() {
        this.properties = new KafkaStreamsProperties();
    }
    
    /**
     * Creates a new instance of KafkaStreamsFactory with the specified properties.
     *
     * @param properties the Kafka Streams properties
     */
    public KafkaStreamsFactory(KafkaStreamsProperties properties) {
        this.properties = properties;
    }
    
    /**
     * Creates a new Kafka Streams instance with the current properties and the specified topology.
     *
     * @param topology the Kafka Streams topology
     * @return a new Kafka Streams instance
     */
    public KafkaStreams createKafkaStreams(Topology topology) {
        properties.validate();
        return new KafkaStreams(topology, properties.asProperties());
    }
    
    /**
     * Creates a new Kafka Streams instance with the current properties and the specified StreamsBuilder.
     *
     * @param streamsBuilder the Kafka Streams builder
     * @return a new Kafka Streams instance
     */
    public KafkaStreams createKafkaStreams(StreamsBuilder streamsBuilder) {
        return createKafkaStreams(streamsBuilder.build());
    }
    
    /**
     * Creates a new Kafka Streams instance with the current properties, additional custom properties,
     * and the specified topology.
     *
     * @param customProperties additional properties to override the current ones
     * @param topology the Kafka Streams topology
     * @return a new Kafka Streams instance
     */
    public KafkaStreams createKafkaStreams(Map<String, Object> customProperties, Topology topology) {
        KafkaStreamsProperties mergedProperties = new KafkaStreamsProperties();
        mergedProperties.properties(properties.asMap());
        mergedProperties.properties(customProperties);
        mergedProperties.validate();
        return new KafkaStreams(topology, mergedProperties.asProperties());
    }
    
    /**
     * Creates a new Kafka Streams instance with the current properties, additional custom properties,
     * and the specified StreamsBuilder.
     *
     * @param customProperties additional properties to override the current ones
     * @param streamsBuilder the Kafka Streams builder
     * @return a new Kafka Streams instance
     */
    public KafkaStreams createKafkaStreams(Map<String, Object> customProperties, StreamsBuilder streamsBuilder) {
        return createKafkaStreams(customProperties, streamsBuilder.build());
    }
    
    /**
     * Creates a new Kafka Streams instance with the specified properties and topology.
     *
     * @param properties the Kafka Streams properties
     * @param topology the Kafka Streams topology
     * @return a new Kafka Streams instance
     */
    public static KafkaStreams createKafkaStreams(Properties properties, Topology topology) {
        return new KafkaStreams(topology, properties);
    }
    
    /**
     * Creates a new Kafka Streams instance with the specified properties and StreamsBuilder.
     *
     * @param properties the Kafka Streams properties
     * @param streamsBuilder the Kafka Streams builder
     * @return a new Kafka Streams instance
     */
    public static KafkaStreams createKafkaStreams(Properties properties, StreamsBuilder streamsBuilder) {
        return createKafkaStreams(properties, streamsBuilder.build());
    }
    
    /**
     * Gets the current Kafka Streams properties.
     *
     * @return the current Kafka Streams properties
     */
    public KafkaStreamsProperties getProperties() {
        return properties;
    }
}