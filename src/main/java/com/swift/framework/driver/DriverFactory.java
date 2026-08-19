package com.swift.framework.driver;

import com.swift.framework.config.ConfigManager;
import com.swift.framework.enums.BrowserType;
import com.swift.framework.enums.ExecutionMode;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds a ready-to-use {@link WebDriver} instance for the browser/execution
 * mode requested in configuration. Supports local runs (with driver binaries
 * managed automatically via WebDriverManager), a plain Selenium Grid, and
 * LambdaTest cloud execution.
 */
public final class DriverFactory {

    private static final Logger LOG = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        return createDriver(ConfigManager.browser(), ConfigManager.executionMode());
    }

    public static WebDriver createDriver(String browserName, String executionMode) {
        BrowserType browser = BrowserType.fromString(browserName);
        ExecutionMode mode = ExecutionMode.fromString(executionMode);

        WebDriver driver = mode == ExecutionMode.REMOTE
                ? createRemoteDriver(browser)
                : createLocalDriver(browser);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigManager.implicitTimeout()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigManager.pageLoadTimeout()));
        if (ConfigManager.maximizeWindow() && mode == ExecutionMode.LOCAL) {
            driver.manage().window().maximize();
        }
        return driver;
    }

    private static WebDriver createLocalDriver(BrowserType browser) {
        LOG.info("Starting LOCAL {} browser", browser);
        boolean headless = ConfigManager.headless();

        return switch (browser) {
            case CHROME -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--remote-allow-origins=*", "--disable-notifications", "--no-sandbox");
                yield new ChromeDriver(options);
            }
            case FIREFOX -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                yield new FirefoxDriver(options);
            }
            case EDGE -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--remote-allow-origins=*");
                yield new EdgeDriver(options);
            }
        };
    }

    private static WebDriver createRemoteDriver(BrowserType browser) {
        String provider = ConfigManager.remoteProvider();
        LOG.info("Starting REMOTE {} browser via {}", browser, provider);

        try {
            URL hubUrl = "lambdatest".equalsIgnoreCase(provider)
                    ? buildLambdaTestUrl()
                    : URI.create(ConfigManager.gridUrl()).toURL();

            MutableCapabilities capabilities = "lambdatest".equalsIgnoreCase(provider)
                    ? buildLambdaTestCapabilities(browser)
                    : buildGridCapabilities(browser);

            return new RemoteWebDriver(hubUrl, capabilities);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid remote WebDriver hub URL", e);
        }
    }

    private static URL buildLambdaTestUrl() throws MalformedURLException {
        String username = ConfigManager.lambdaTestUsername();
        String accessKey = ConfigManager.lambdaTestAccessKey();
        if (username == null || accessKey == null) {
            throw new IllegalStateException(
                    "LT_USERNAME and LT_ACCESS_KEY must be set (env vars or -D system properties) to run on LambdaTest");
        }
        String hub = ConfigManager.lambdaTestUrl().replace("https://", "");
        return URI.create("https://" + username + ":" + accessKey + "@" + hub).toURL();
    }

    private static MutableCapabilities buildLambdaTestCapabilities(BrowserType browser) {
        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("browserName", toLambdaTestBrowserName(browser));
        capabilities.setCapability("browserVersion", ConfigManager.get("lt.browserVersion", "latest"));

        Map<String, Object> ltOptions = new HashMap<>();
        ltOptions.put("platformName", ConfigManager.get("lt.platform", "Windows 11"));
        ltOptions.put("build", ConfigManager.get("lt.build", "Swift Framework Build"));
        ltOptions.put("name", ConfigManager.get("lt.testName", "Swift Automation Test"));
        ltOptions.put("project", ConfigManager.get("lt.project", "Swift Selenium Framework"));
        ltOptions.put("selenium_version", ConfigManager.get("lt.seleniumVersion", "4.24.0"));
        ltOptions.put("w3c", true);
        ltOptions.put("console", true);
        ltOptions.put("network", true);
        ltOptions.put("visual", true);
        ltOptions.put("video", true);
        capabilities.setCapability("LT:Options", ltOptions);
        return capabilities;
    }

    private static String toLambdaTestBrowserName(BrowserType browser) {
        return switch (browser) {
            case CHROME -> "Chrome";
            case FIREFOX -> "MozillaFirefox";
            case EDGE -> "MicrosoftEdge";
        };
    }

    private static MutableCapabilities buildGridCapabilities(BrowserType browser) {
        return switch (browser) {
            case CHROME -> new ChromeOptions();
            case FIREFOX -> new FirefoxOptions();
            case EDGE -> new EdgeOptions();
        };
    }
}
