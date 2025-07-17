package com.manus.kafka.core.validation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception thrown when configuration validation fails.
 * Contains detailed information about validation errors and provides
 * context and recovery suggestions.
 */
public class ConfigurationException extends RuntimeException {
    
    private final ValidationResult validationResult;
    
    /**
     * Creates a new ConfigurationException with a validation result.
     *
     * @param validationResult the validation result containing errors
     */
    public ConfigurationException(ValidationResult validationResult) {
        super(buildMessage(validationResult));
        this.validationResult = validationResult;
    }
    
    /**
     * Creates a new ConfigurationException with a validation result and cause.
     *
     * @param validationResult the validation result containing errors
     * @param cause the underlying cause
     */
    public ConfigurationException(ValidationResult validationResult, Throwable cause) {
        super(buildMessage(validationResult), cause);
        this.validationResult = validationResult;
    }
    
    /**
     * Creates a new ConfigurationException with a single error.
     *
     * @param error the validation error
     */
    public ConfigurationException(ValidationError error) {
        this(ValidationResult.error(error));
    }
    
    /**
     * Creates a new ConfigurationException with a message.
     *
     * @param message the error message
     */
    public ConfigurationException(String message) {
        super(message);
        this.validationResult = ValidationResult.error(
            new ValidationError(null, message, ValidationErrorType.GENERAL)
        );
    }
    
    /**
     * Gets the validation result that caused this exception.
     *
     * @return the validation result
     */
    public ValidationResult getValidationResult() {
        return validationResult;
    }
    
    /**
     * Gets the list of validation errors.
     *
     * @return the validation errors
     */
    public List<ValidationError> getErrors() {
        return validationResult != null ? validationResult.getErrors() : List.of();
    }
    
    /**
     * Gets the list of validation warnings.
     *
     * @return the validation warnings
     */
    public List<ValidationWarning> getWarnings() {
        return validationResult != null ? validationResult.getWarnings() : List.of();
    }
    
    /**
     * Returns true if this exception has recovery suggestions.
     *
     * @return true if recovery suggestions are available
     */
    public boolean hasRecoverySuggestions() {
        return getErrors().stream().anyMatch(ValidationError::hasSuggestion) ||
               getWarnings().stream().anyMatch(ValidationWarning::hasSuggestion);
    }
    
    /**
     * Gets all recovery suggestions from errors and warnings.
     *
     * @return a list of recovery suggestions
     */
    public List<String> getRecoverySuggestions() {
        List<String> suggestions = getErrors().stream()
            .filter(ValidationError::hasSuggestion)
            .map(ValidationError::getSuggestion)
            .collect(Collectors.toList());
        
        suggestions.addAll(getWarnings().stream()
            .filter(ValidationWarning::hasSuggestion)
            .map(ValidationWarning::getSuggestion)
            .collect(Collectors.toList()));
        
        return suggestions;
    }
    
    /**
     * Gets a formatted error message with all errors and suggestions.
     *
     * @return the formatted error message
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Configuration validation failed");
        
        if (validationResult != null) {
            List<ValidationError> errors = validationResult.getErrors();
            if (!errors.isEmpty()) {
                sb.append("\n\nErrors:");
                for (int i = 0; i < errors.size(); i++) {
                    sb.append("\n  ").append(i + 1).append(". ").append(errors.get(i).getFormattedMessage());
                }
            }
            
            List<ValidationWarning> warnings = validationResult.getWarnings();
            if (!warnings.isEmpty()) {
                sb.append("\n\nWarnings:");
                for (int i = 0; i < warnings.size(); i++) {
                    sb.append("\n  ").append(i + 1).append(". ").append(warnings.get(i).getFormattedMessage());
                }
            }
            
            List<String> suggestions = getRecoverySuggestions();
            if (!suggestions.isEmpty()) {
                sb.append("\n\nSuggestions:");
                for (int i = 0; i < suggestions.size(); i++) {
                    sb.append("\n  ").append(i + 1).append(". ").append(suggestions.get(i));
                }
            }
        }
        
        return sb.toString();
    }
    
    private static String buildMessage(ValidationResult validationResult) {
        if (validationResult == null || !validationResult.hasErrors()) {
            return "Configuration validation failed";
        }
        
        List<ValidationError> errors = validationResult.getErrors();
        if (errors.size() == 1) {
            return "Configuration validation failed: " + errors.get(0).getMessage();
        } else {
            return "Configuration validation failed with " + errors.size() + " errors";
        }
    }
}