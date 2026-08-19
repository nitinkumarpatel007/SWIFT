package com.swift.framework.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central configuration reader. Loads {@code config.properties} from the classpath and
 * allows every key to be overridden with a matching JVM system property
 * (e.g. {@code -Dbrowser=firefox}), which is what CI pipelines and local
 * ad-hoc runs use to switch behaviour without touching the file.
 */
public final class ConfigManager {

    private static final Logger LOG = LogManager.getLogger(ConfigManager.class);
    private static final Properties PROPERTIES = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Unable to find " + CONFIG_FILE + " on the classpath");
            }
            PROPERTIES.load(input);
            LOG.info("Loaded configuration from {}", CONFIG_FILE);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + CONFIG_FILE, e);
        }
    }

    private ConfigManager() {
    }

    /** Returns a config value, preferring a system property of the same name. */
    public static String get(String key) {
        return System.getProperty(key, PROPERTIES.getProperty(key));
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        return value == null || value.isBlank() ? defaultValue : Integer.parseInt(value.trim());
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return value == null || value.isBlank() ? defaultValue : Boolean.parseBoolean(value.trim());
    }

    public static String browser() {
        return get("browser", "chrome");
    }

    public static boolean headless() {
        return getBoolean("headless", false);
    }

    public static String executionMode() {
        return get("execution.mode", "local");
    }

    public static String remoteProvider() {
        return get("remote.provider", "lambdatest");
    }

    public static String gridUrl() {
        return get("grid.url", "http://localhost:4444/wd/hub");
    }

    public static String lambdaTestUrl() {
        return get("lambdatest.url", "https://hub.lambdatest.com/wd/hub");
    }

    public static String lambdaTestUsername() {
        return firstNonBlank(System.getProperty("LT_USERNAME"), System.getenv("LT_USERNAME"));
    }

    public static String lambdaTestAccessKey() {
        return firstNonBlank(System.getProperty("LT_ACCESS_KEY"), System.getenv("LT_ACCESS_KEY"));
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    public static String baseUrl() {
        return get("base.url", "https://www.swift.com");
    }

    public static String careersUrl() {
        return get("careers.url", "https://www.swift.com/about-us/careers");
    }

    public static int implicitTimeout() {
        return getInt("timeout.implicit", 5);
    }

    public static int explicitTimeout() {
        return getInt("timeout.explicit", 20);
    }

    public static int pageLoadTimeout() {
        return getInt("timeout.pageload", 45);
    }

    public static boolean maximizeWindow() {
        return getBoolean("window.maximize", true);
    }

    public static String screenshotDir() {
        return get("screenshot.dir", "test-output/screenshots");
    }

    public static int linkCheckTimeoutMs() {
        return getInt("link.check.timeout.ms", 8000);
    }

    public static int linkCheckMax() {
        return getInt("link.check.max", 60);
    }
}
