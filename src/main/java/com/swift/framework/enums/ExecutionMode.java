package com.swift.framework.enums;

/** Where the WebDriver session actually runs. */
public enum ExecutionMode {
    LOCAL,
    REMOTE;

    public static ExecutionMode fromString(String value) {
        if (value == null || value.isBlank()) {
            return LOCAL;
        }
        return ExecutionMode.valueOf(value.trim().toUpperCase());
    }
}
