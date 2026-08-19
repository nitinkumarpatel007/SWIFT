package com.swift.framework.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * Thread-safe wrapper around a single {@link ExtentReports} instance shared by
 * every (possibly parallel) test thread. Each thread gets its own
 * {@link ExtentTest} node via a {@link ThreadLocal}.
 */
public final class ExtentManager {

    private static final String REPORT_DIR = "test-output/extent-report";
    private static volatile ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    private ExtentManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_DIR + "/SwiftAutomationReport-" + timestamp + ".html");
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("Swift Selenium Automation Report");
            sparkReporter.config().setReportName("Swift.com Regression Suite");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Framework", "Selenium + TestNG");
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        }
        return extentReports;
    }

    public static ExtentTest startTest(String name, String description) {
        ExtentTest test = getInstance().createTest(name, description);
        CURRENT_TEST.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return CURRENT_TEST.get();
    }

    public static void removeTest() {
        CURRENT_TEST.remove();
    }

    public static synchronized void flush() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
