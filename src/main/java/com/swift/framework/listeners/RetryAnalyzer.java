package com.swift.framework.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/** Retries a failed test up to {@link #MAX_RETRY_COUNT} times before it is reported as failed. */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRY_COUNT = 1;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            return true;
        }
        return false;
    }
}
