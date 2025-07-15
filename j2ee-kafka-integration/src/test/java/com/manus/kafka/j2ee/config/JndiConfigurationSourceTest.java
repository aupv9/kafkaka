package com.manus.kafka.j2ee.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link JndiConfigurationSource}.
 */
public class JndiConfigurationSourceTest {

    private Context mockContext;
    private final String jndiName = "java:comp/env/kafka/properties";

    @BeforeEach
    public void setUp() throws NamingException {
        mockContext = mock(Context.class);
    }

    @Test
    @DisplayName("Test constructor loads properties from JNDI Properties object")
    public void testConstructorLoadsPropertiesFromJndiPropertiesObject() throws NamingException {
        try (MockedStatic<InitialContext> mockedInitialContext = Mockito.mockStatic(InitialContext.class)) {
            // Given
            Properties jndiProperties = new Properties();
            jndiProperties.setProperty("bootstrap.servers", "localhost:9092");
            jndiProperties.setProperty("group.id", "test-group");
            
            InitialContext initialContext = mock(InitialContext.class);
            mockedInitialContext.when(InitialContext::new).thenReturn(initialContext);
            when(initialContext.lookup(jndiName)).thenReturn(jndiProperties);
            
            // When
            JndiConfigurationSource source = new JndiConfigurationSource(jndiName);
            
            // Then
            assertEquals(jndiName, source.getJndiName());
            Map<String, Object> properties = source.getProperties();
            assertEquals(2, properties.size());
            assertEquals("localhost:9092", properties.get("bootstrap.servers"));
            assertEquals("test-group", properties.get("group.id"));
        }
    }

    @Test
    @DisplayName("Test constructor loads properties from JNDI Map object")
    public void testConstructorLoadsPropertiesFromJndiMapObject() throws NamingException {
        try (MockedStatic<InitialContext> mockedInitialContext = Mockito.mockStatic(InitialContext.class)) {
            // Given
            Map<String, Object> jndiMap = new HashMap<>();
            jndiMap.put("bootstrap.servers", "localhost:9092");
            jndiMap.put("group.id", "test-group");
            
            InitialContext initialContext = mock(InitialContext.class);
            mockedInitialContext.when(InitialContext::new).thenReturn(initialContext);
            when(initialContext.lookup(jndiName)).thenReturn(jndiMap);
            
            // When
            JndiConfigurationSource source = new JndiConfigurationSource(jndiName);
            
            // Then
            assertEquals(jndiName, source.getJndiName());
            Map<String, Object> properties = source.getProperties();
            assertEquals(2, properties.size());
            assertEquals("localhost:9092", properties.get("bootstrap.servers"));
            assertEquals("test-group", properties.get("group.id"));
        }
    }

    @Test
    @DisplayName("Test constructor throws exception for unsupported JNDI object type")
    public void testConstructorThrowsExceptionForUnsupportedJndiObjectType() throws NamingException {
        try (MockedStatic<InitialContext> mockedInitialContext = Mockito.mockStatic(InitialContext.class)) {
            // Given
            String unsupportedObject = "This is not a Properties or Map";
            
            InitialContext initialContext = mock(InitialContext.class);
            mockedInitialContext.when(InitialContext::new).thenReturn(initialContext);
            when(initialContext.lookup(jndiName)).thenReturn(unsupportedObject);
            
            // When/Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> new JndiConfigurationSource(jndiName));
            assertTrue(exception.getMessage().contains("JNDI object is not a Properties or Map"));
        }
    }

    @Test
    @DisplayName("Test constructor throws exception when JNDI lookup fails")
    public void testConstructorThrowsExceptionWhenJndiLookupFails() throws NamingException {
        try (MockedStatic<InitialContext> mockedInitialContext = Mockito.mockStatic(InitialContext.class)) {
            // Given
            NamingException namingException = new NamingException("JNDI lookup failed");
            
            InitialContext initialContext = mock(InitialContext.class);
            mockedInitialContext.when(InitialContext::new).thenReturn(initialContext);
            when(initialContext.lookup(jndiName)).thenThrow(namingException);
            
            // When/Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> new JndiConfigurationSource(jndiName));
            assertTrue(exception.getMessage().contains("Failed to load properties from JNDI"));
            assertEquals(namingException, exception.getCause());
        }
    }

    @Test
    @DisplayName("Test reload method refreshes properties")
    public void testReload() throws NamingException {
        try (MockedStatic<InitialContext> mockedInitialContext = Mockito.mockStatic(InitialContext.class)) {
            // Given
            Properties initialProperties = new Properties();
            initialProperties.setProperty("bootstrap.servers", "localhost:9092");
            initialProperties.setProperty("group.id", "test-group");
            
            Properties updatedProperties = new Properties();
            updatedProperties.setProperty("bootstrap.servers", "localhost:9093");
            updatedProperties.setProperty("group.id", "new-group");
            updatedProperties.setProperty("new.property", "new-value");
            
            InitialContext initialContext = mock(InitialContext.class);
            mockedInitialContext.when(InitialContext::new).thenReturn(initialContext);
            when(initialContext.lookup(jndiName))
                .thenReturn(initialProperties)
                .thenReturn(updatedProperties);
            
            JndiConfigurationSource source = new JndiConfigurationSource(jndiName);
            assertEquals(2, source.getProperties().size());
            
            // When
            source.reload();
            
            // Then
            Map<String, Object> properties = source.getProperties();
            assertEquals(3, properties.size());
            assertEquals("localhost:9093", properties.get("bootstrap.servers"));
            assertEquals("new-group", properties.get("group.id"));
            assertEquals("new-value", properties.get("new.property"));
        }
    }

    @Test
    @DisplayName("Test getProperty method")
    public void testGetProperty() throws NamingException {
        try (MockedStatic<InitialContext> mockedInitialContext = Mockito.mockStatic(InitialContext.class)) {
            // Given
            Properties jndiProperties = new Properties();
            jndiProperties.setProperty("bootstrap.servers", "localhost:9092");
            jndiProperties.setProperty("group.id", "test-group");
            
            InitialContext initialContext = mock(InitialContext.class);
            mockedInitialContext.when(InitialContext::new).thenReturn(initialContext);
            when(initialContext.lookup(jndiName)).thenReturn(jndiProperties);
            
            JndiConfigurationSource source = new JndiConfigurationSource(jndiName);
            
            // When/Then
            assertEquals("localhost:9092", source.getProperty("bootstrap.servers"));
            assertEquals("test-group", source.getProperty("group.id"));
            assertNull(source.getProperty("non.existent.property"));
        }
    }
}