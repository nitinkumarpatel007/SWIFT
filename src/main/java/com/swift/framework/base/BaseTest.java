package com.swift.framework.base;

import com.swift.framework.driver.DriverFactory;
import com.swift.framework.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Parent class for every test class. Initializes a fresh browser session
 * before each test method (thread-safe, so it supports TestNG parallel
 * execution) and tears it down afterwards. Browser/platform/execution mode
 * can be supplied via testng.xml &lt;parameter&gt; tags, which fall back to
 * {@code config.properties} / system properties when not provided.
 */
public abstract class BaseTest {

    protected final Logger log = LogManager.getLogger(getClass());

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "executionMode"})
    public void setUp(@Optional("") String browser, @Optional("") String executionMode) {
        String resolvedBrowser = browser.isBlank() ? com.swift.framework.config.ConfigManager.browser() : browser;
        String resolvedMode = executionMode.isBlank() ? com.swift.framework.config.ConfigManager.executionMode() : executionMode;

        log.info("Initializing WebDriver -> browser={}, mode={}", resolvedBrowser, resolvedMode);
        WebDriver driver = DriverFactory.createDriver(resolvedBrowser, resolvedMode);
        DriverManager.setDriver(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("Quitting WebDriver session");
        DriverManager.quitDriver();
    }

    protected WebDriver driver() {
        return DriverManager.getDriver();
    }
}
