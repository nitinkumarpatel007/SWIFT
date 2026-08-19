package com.swift.framework.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.swift.framework.driver.DriverManager;
import com.swift.framework.reports.ExtentManager;
import com.swift.framework.utils.ScreenshotUtils;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/**
 * TestNG listener that bridges test lifecycle events into both ExtentReports
 * and Allure, and captures a screenshot whenever a UI test fails.
 */
public class TestListener implements ITestListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        LOG.info("=== Starting test suite: {} ===", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest test = ExtentManager.startTest(testName, result.getMethod().getDescription());
        test.assignCategory(result.getTestClass().getRealClass().getSimpleName());
        LOG.info("Starting test: {}", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.getTest().log(Status.PASS, "Test passed");
        LOG.info("PASSED: {}", result.getMethod().getMethodName());
        ExtentManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        LOG.error("FAILED: {}", testName, result.getThrowable());

        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            attachScreenshotToExtent(driver, testName);
            attachScreenshotToAllure(driver);
        }
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.FAIL, result.getThrowable());
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.SKIP, "Test skipped: " + result.getThrowable());
        }
        LOG.warn("SKIPPED: {}", result.getMethod().getMethodName());
        ExtentManager.removeTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
        LOG.info("=== Finished test suite: {} ===", context.getName());
    }

    private void attachScreenshotToExtent(WebDriver driver, String testName) {
        String path = ScreenshotUtils.capture(driver, testName);
        if (path == null) {
            return;
        }
        try {
            ExtentTest test = ExtentManager.getTest();
            if (test != null) {
                test.fail("Screenshot on failure", MediaEntityBuilder.createScreenCaptureFromPath(path).build());
            }
        } catch (Exception e) {
            LOG.warn("Could not attach screenshot to Extent report: {}", e.getMessage());
        }
    }

    private void attachScreenshotToAllure(WebDriver driver) {
        try {
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot on failure", new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            LOG.warn("Could not attach screenshot to Allure report: {}", e.getMessage());
        }
    }
}
