package com.manus.kafka.j2ee.config;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration source that loads properties from JNDI.
 */
public class JndiConfigurationSource implements ConfigurationSource {
    
    private final String jndiName;
    private Map<String, Object> properties;
    
    /**
     * Creates a new instance of JndiConfigurationSource.
     *
     * @param jndiName the JNDI name of the properties object
     */
    public JndiConfigurationSource(String jndiName) {
        this.jndiName = jndiName;
        this.properties = new HashMap<>();
        loadProperties();
    }
    
    /**
     * Loads the properties from JNDI.
     */
    private void loadProperties() {
        try {
            Context context = new InitialContext();
            Object jndiObject = context.lookup(jndiName);
            
            if (jndiObject instanceof Properties) {
                Properties props = (Properties) jndiObject;
                for (String key : props.stringPropertyNames()) {
                    properties.put(key, props.getProperty(key));
                }
            } else if (jndiObject instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> props = (Map<String, Object>) jndiObject;
                properties.putAll(props);
            } else {
                throw new RuntimeException("JNDI object is not a Properties or Map: " + jndiObject.getClass().getName());
            }
        } catch (NamingException e) {
            throw new RuntimeException("Failed to load properties from JNDI: " + jndiName, e);
        }
    }
    
    /**
     * Reloads the properties from JNDI.
     */
    public void reload() {
        properties.clear();
        loadProperties();
    }
    
    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    /**
     * Gets the JNDI name.
     *
     * @return the JNDI name
     */
    public String getJndiName() {
        return jndiName;
    }
}