package com.swift.framework.utils;

import com.swift.framework.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/** Small wrapper around Selenium's explicit waits used by page objects. */
public final class WaitUtils {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.explicitTimeout()));
    }

    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public boolean waitForUrlContains(String fragment) {
        return wait.until(ExpectedConditions.urlContains(fragment));
    }

    public boolean waitForTitleContains(String fragment) {
        return wait.until(ExpectedConditions.titleContains(fragment));
    }
}
