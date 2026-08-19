package com.swift.framework.pages;

import com.swift.framework.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/** Common behaviour shared by every page object. */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitUtils waitUtils;
    protected final Logger log;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
        this.log = LogManager.getLogger(getClass());
        PageFactory.initElements(driver, this);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    public List<WebElement> findVisibleImages() {
        return driver.findElements(By.tagName("img")).stream()
                .filter(WebElement::isDisplayed)
                .toList();
    }

    /** Best-effort dismissal of the cookie-consent banner so it doesn't block clicks. */
    public void acceptCookiesIfPresent() {
        List<By> candidates = List.of(
                By.xpath("//button[contains(translate(., 'ACEPT', 'acept'), 'accept all')]"),
                By.id("onetrust-accept-btn-handler"),
                By.cssSelector("button#onetrust-accept-btn-handler"));

        for (By locator : candidates) {
            List<WebElement> matches = driver.findElements(locator);
            if (!matches.isEmpty() && matches.get(0).isDisplayed()) {
                try {
                    matches.get(0).click();
                    log.debug("Dismissed cookie banner using locator {}", locator);
                    return;
                } catch (Exception e) {
                    log.debug("Cookie banner click failed for {}: {}", locator, e.getMessage());
                }
            }
        }
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }
}
