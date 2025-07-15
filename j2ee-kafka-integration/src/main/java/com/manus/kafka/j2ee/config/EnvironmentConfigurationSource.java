package com.manus.kafka.j2ee.config;

import com.manus.kafka.core.util.PropertyLoader;

import java.util.Map;

/**
 * Configuration source that loads properties from environment variables.
 */
public class EnvironmentConfigurationSource implements ConfigurationSource {
    
    private final String prefix;
    private Map<String, Object> properties;
    
    /**
     * Creates a new instance of EnvironmentConfigurationSource with the default prefix "KAFKA_".
     */
    public EnvironmentConfigurationSource() {
        this("KAFKA_");
    }
    
    /**
     * Creates a new instance of EnvironmentConfigurationSource with the specified prefix.
     *
     * @param prefix the prefix of environment variables to load
     */
    public EnvironmentConfigurationSource(String prefix) {
        this.prefix = prefix;
        loadProperties();
    }
    
    /**
     * Loads the properties from environment variables.
     */
    private void loadProperties() {
        properties = PropertyLoader.loadFromEnvironment(prefix);
    }
    
    /**
     * Reloads the properties from environment variables.
     */
    public void reload() {
        loadProperties();
    }
    
    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    /**
     * Gets the prefix.
     *
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }
}