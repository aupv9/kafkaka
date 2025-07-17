package com.manus.kafka.core.validation;

/**
 * Enumeration of different types of validation warnings.
 */
public enum ValidationWarningType {
    
    /**
     * A warning related to performance implications.
     */
    PERFORMANCE,
    
    /**
     * A warning related to security concerns.
     */
    SECURITY,
    
    /**
     * A warning about deprecated configuration.
     */
    DEPRECATED,
    
    /**
     * A warning about best practices.
     */
    BEST_PRACTICE,
    
    /**
     * A warning about potential reliability issues.
     */
    RELIABILITY,
    
    /**
     * A general validation warning.
     */
    GENERAL
}