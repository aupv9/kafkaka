package com.manus.kafka.core.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ValidationResult}.
 */
public class ValidationResultTest {

    @Test
    @DisplayName("Test default constructor creates empty result")
    public void testDefaultConstructor() {
        // When
        ValidationResult result = new ValidationResult();
        
        // Then
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertEquals(0, result.getTotalIssues());
        assertTrue(result.getErrors().isEmpty());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    @DisplayName("Test adding errors and warnings")
    public void testAddingErrorsAndWarnings() {
        // Given
        ValidationResult result = new ValidationResult();
        ValidationError error = ValidationError.missingRequired("test.property");
        ValidationWarning warning = ValidationWarning.performance("test.property", "Test warning", "Test suggestion");
        
        // When
        result.addError(error);
        result.addWarning(warning);
        
        // Then
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertTrue(result.hasWarnings());
        assertEquals(2, result.getTotalIssues());
        assertEquals(1, result.getErrors().size());
        assertEquals(1, result.getWarnings().size());
        assertEquals(error, result.getErrors().get(0));
        assertEquals(warning, result.getWarnings().get(0));
    }

    @Test
    @DisplayName("Test merge functionality")
    public void testMerge() {
        // Given
        ValidationResult result1 = new ValidationResult();
        ValidationResult result2 = new ValidationResult();
        
        ValidationError error1 = ValidationError.missingRequired("property1");
        ValidationError error2 = ValidationError.missingRequired("property2");
        ValidationWarning warning1 = ValidationWarning.performance("property1", "Warning 1", null);
        ValidationWarning warning2 = ValidationWarning.performance("property2", "Warning 2", null);
        
        result1.addError(error1);
        result1.addWarning(warning1);
        result2.addError(error2);
        result2.addWarning(warning2);
        
        // When
        result1.merge(result2);
        
        // Then
        assertEquals(2, result1.getErrors().size());
        assertEquals(2, result1.getWarnings().size());
        assertEquals(4, result1.getTotalIssues());
        assertTrue(result1.getErrors().contains(error1));
        assertTrue(result1.getErrors().contains(error2));
        assertTrue(result1.getWarnings().contains(warning1));
        assertTrue(result1.getWarnings().contains(warning2));
    }

    @Test
    @DisplayName("Test static factory methods")
    public void testStaticFactoryMethods() {
        // Test success
        ValidationResult success = ValidationResult.success();
        assertTrue(success.isValid());
        assertFalse(success.hasErrors());
        assertFalse(success.hasWarnings());
        
        // Test error
        ValidationError error = ValidationError.missingRequired("test.property");
        ValidationResult errorResult = ValidationResult.error(error);
        assertFalse(errorResult.isValid());
        assertTrue(errorResult.hasErrors());
        assertFalse(errorResult.hasWarnings());
        assertEquals(error, errorResult.getErrors().get(0));
        
        // Test warning
        ValidationWarning warning = ValidationWarning.performance("test.property", "Test warning", null);
        ValidationResult warningResult = ValidationResult.warning(warning);
        assertTrue(warningResult.isValid()); // Warnings don't make result invalid
        assertFalse(warningResult.hasErrors());
        assertTrue(warningResult.hasWarnings());
        assertEquals(warning, warningResult.getWarnings().get(0));
    }

    @Test
    @DisplayName("Test null handling")
    public void testNullHandling() {
        // Given
        ValidationResult result = new ValidationResult();
        
        // When
        result.addError(null);
        result.addWarning(null);
        result.merge(null);
        
        // Then
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertEquals(0, result.getTotalIssues());
    }

    @Test
    @DisplayName("Test constructor with lists")
    public void testConstructorWithLists() {
        // Given
        ValidationError error = ValidationError.missingRequired("test.property");
        ValidationWarning warning = ValidationWarning.performance("test.property", "Test warning", null);
        List<ValidationError> errors = List.of(error);
        List<ValidationWarning> warnings = List.of(warning);
        
        // When
        ValidationResult result = new ValidationResult(errors, warnings);
        
        // Then
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertTrue(result.hasWarnings());
        assertEquals(1, result.getErrors().size());
        assertEquals(1, result.getWarnings().size());
        assertEquals(error, result.getErrors().get(0));
        assertEquals(warning, result.getWarnings().get(0));
    }

    @Test
    @DisplayName("Test immutable lists")
    public void testImmutableLists() {
        // Given
        ValidationResult result = new ValidationResult();
        ValidationError error = ValidationError.missingRequired("test.property");
        result.addError(error);
        
        // When/Then
        List<ValidationError> errors = result.getErrors();
        assertThrows(UnsupportedOperationException.class, () -> errors.add(error));
        
        List<ValidationWarning> warnings = result.getWarnings();
        ValidationWarning warning = ValidationWarning.performance("test.property", "Test warning", null);
        assertThrows(UnsupportedOperationException.class, () -> warnings.add(warning));
    }
}