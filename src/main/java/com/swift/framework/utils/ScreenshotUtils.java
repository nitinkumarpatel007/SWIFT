package com.swift.framework.utils;

import com.swift.framework.config.ConfigManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/** Captures PNG screenshots on demand (mainly used for failed-test evidence). */
public final class ScreenshotUtils {

    private static final Logger LOG = LogManager.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private ScreenshotUtils() {
    }

    /** Takes a screenshot and returns the absolute path of the saved file, or null on failure. */
    public static String capture(WebDriver driver, String testName) {
        if (!(driver instanceof TakesScreenshot)) {
            return null;
        }
        try {
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String fileName = testName + "_" + LocalDateTime.now().format(TIMESTAMP) + ".png";
            File destination = Paths.get(ConfigManager.screenshotDir(), fileName).toFile();
            FileUtils.copyFile(source, destination);
            return destination.getAbsolutePath();
        } catch (IOException | RuntimeException e) {
            LOG.warn("Unable to capture screenshot for {}: {}", testName, e.getMessage());
            return null;
        }
    }
}
