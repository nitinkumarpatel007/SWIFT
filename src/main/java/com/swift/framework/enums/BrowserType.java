package com.swift.framework.enums;

/**
 * Supported browsers for local and remote execution.
 */
public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType fromString(String value) {
        if (value == null || value.isBlank()) {
            return CHROME;
        }
        return BrowserType.valueOf(value.trim().toUpperCase());
    }
}
