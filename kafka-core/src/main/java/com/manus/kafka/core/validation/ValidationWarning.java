package com.manus.kafka.core.validation;

import java.util.Objects;

/**
 * Represents a validation warning that occurred during configuration validation.
 * Warnings indicate potential issues that don't prevent the configuration from
 * working but may lead to suboptimal behavior or future problems.
 */
public class ValidationWarning {
    
    private final String propertyName;
    private final String message;
    private final String suggestion;
    private final ValidationWarningType type;
    private final Object actualValue;
    
    /**
     * Creates a new ValidationWarning with the specified details.
     *
     * @param propertyName the name of the property that generated the warning
     * @param message the warning message
     * @param type the type of validation warning
     */
    public ValidationWarning(String propertyName, String message, ValidationWarningType type) {
        this(propertyName, message, type, null, null);
    }
    
    /**
     * Creates a new ValidationWarning with the specified details and suggestion.
     *
     * @param propertyName the name of the property that generated the warning
     * @param message the warning message
     * @param type the type of validation warning
     * @param suggestion optional suggestion for addressing the warning
     */
    public ValidationWarning(String propertyName, String message, ValidationWarningType type, String suggestion) {
        this(propertyName, message, type, suggestion, null);
    }
    
    /**
     * Creates a new ValidationWarning with complete details.
     *
     * @param propertyName the name of the property that generated the warning
     * @param message the warning message
     * @param type the type of validation warning
     * @param suggestion optional suggestion for addressing the warning
     * @param actualValue the actual value that generated the warning
     */
    public ValidationWarning(String propertyName, String message, ValidationWarningType type, 
                           String suggestion, Object actualValue) {
        this.propertyName = propertyName;
        this.message = message;
        this.type = type != null ? type : ValidationWarningType.GENERAL;
        this.suggestion = suggestion;
        this.actualValue = actualValue;
    }
    
    /**
     * Gets the name of the property that generated the warning.
     *
     * @return the property name
     */
    public String getPropertyName() {
        return propertyName;
    }
    
    /**
     * Gets the warning message.
     *
     * @return the warning message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the suggestion for addressing the warning.
     *
     * @return the suggestion, or null if none provided
     */
    public String getSuggestion() {
        return suggestion;
    }
    
    /**
     * Gets the type of validation warning.
     *
     * @return the validation warning type
     */
    public ValidationWarningType getType() {
        return type;
    }
    
    /**
     * Gets the actual value that generated the warning.
     *
     * @return the actual value, or null if not provided
     */
    public Object getActualValue() {
        return actualValue;
    }
    
    /**
     * Returns true if this warning has a suggestion for addressing it.
     *
     * @return true if suggestion is available, false otherwise
     */
    public boolean hasSuggestion() {
        return suggestion != null && !suggestion.trim().isEmpty();
    }
    
    /**
     * Gets a formatted warning message including suggestion if available.
     *
     * @return the formatted warning message
     */
    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();
        if (propertyName != null) {
            sb.append("[").append(propertyName).append("] ");
        }
        sb.append(message);
        if (hasSuggestion()) {
            sb.append(" Suggestion: ").append(suggestion);
        }
        return sb.toString();
    }
    
    /**
     * Creates a ValidationWarning for a potentially problematic configuration.
     *
     * @param propertyName the name of the property
     * @param message the warning message
     * @param suggestion optional suggestion for improvement
     * @return a ValidationWarning for the configuration issue
     */
    public static ValidationWarning performance(String propertyName, String message, String suggestion) {
        return new ValidationWarning(propertyName, message, ValidationWarningType.PERFORMANCE, suggestion);
    }
    
    /**
     * Creates a ValidationWarning for a security-related concern.
     *
     * @param propertyName the name of the property
     * @param message the warning message
     * @param suggestion optional suggestion for improvement
     * @return a ValidationWarning for the security concern
     */
    public static ValidationWarning security(String propertyName, String message, String suggestion) {
        return new ValidationWarning(propertyName, message, ValidationWarningType.SECURITY, suggestion);
    }
    
    /**
     * Creates a ValidationWarning for a deprecated configuration.
     *
     * @param propertyName the name of the deprecated property
     * @param replacement the recommended replacement
     * @return a ValidationWarning for the deprecated property
     */
    public static ValidationWarning deprecated(String propertyName, String replacement) {
        return new ValidationWarning(
            propertyName,
            "Property is deprecated",
            ValidationWarningType.DEPRECATED,
            "Use " + replacement + " instead"
        );
    }
    
    /**
     * Creates a ValidationWarning for a reliability-related concern.
     *
     * @param propertyName the name of the property
     * @param message the warning message
     * @param suggestion optional suggestion for improvement
     * @return a ValidationWarning for the reliability concern
     */
    public static ValidationWarning reliability(String propertyName, String message, String suggestion) {
        return new ValidationWarning(propertyName, message, ValidationWarningType.RELIABILITY, suggestion);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationWarning that = (ValidationWarning) o;
        return Objects.equals(propertyName, that.propertyName) &&
               Objects.equals(message, that.message) &&
               type == that.type;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(propertyName, message, type);
    }
    
    @Override
    public String toString() {
        return getFormattedMessage();
    }
}