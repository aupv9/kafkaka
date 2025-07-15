package com.manus.kafka.j2ee.config;

import com.manus.kafka.core.util.PropertyLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration source that loads properties from a file.
 */
public class FileConfigurationSource implements ConfigurationSource {
    
    private final String filePath;
    private Map<String, Object> properties;
    
    /**
     * Creates a new instance of FileConfigurationSource.
     *
     * @param filePath the path to the properties file
     */
    public FileConfigurationSource(String filePath) {
        this.filePath = filePath;
        this.properties = new HashMap<>();
        loadProperties();
    }
    
    /**
     * Loads the properties from the file.
     */
    private void loadProperties() {
        try {
            properties = PropertyLoader.loadFromFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties from file: " + filePath, e);
        }
    }
    
    /**
     * Reloads the properties from the file.
     */
    public void reload() {
        loadProperties();
    }
    
    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    /**
     * Gets the file path.
     *
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }
}