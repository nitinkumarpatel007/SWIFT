package com.swift.framework.pages;

import com.swift.framework.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Page object for the Swift.com careers page (https://www.swift.com/about-us/careers). */
public class CareersPage extends BasePage {

    private static final By MAIN_HEADING = By.cssSelector("h1");
    private static final By OUR_POSITIONS_SECTION = By.xpath("//*[contains(text(),'Our positions')]");
    private static final By OPEN_POSITIONS_LINK = By.xpath("//a[contains(@href,'myworkdayjobs.com')]");
    private static final By FOOTER = By.tagName("footer");

    public CareersPage(WebDriver driver) {
        super(driver);
    }

    public CareersPage open() {
        driver.get(ConfigManager.careersUrl());
        acceptCookiesIfPresent();
        return this;
    }

    public String getMainHeadingText() {
        List<WebElement> headings = findAll(MAIN_HEADING);
        return headings.isEmpty() ? "" : headings.get(0).getText().trim();
    }

    public boolean isOurPositionsSectionDisplayed() {
        return !findAll(OUR_POSITIONS_SECTION).isEmpty();
    }

    public boolean isOpenPositionsLinkPresent() {
        return !findAll(OPEN_POSITIONS_LINK).isEmpty();
    }

    public String getOpenPositionsLinkHref() {
        List<WebElement> links = findAll(OPEN_POSITIONS_LINK);
        return links.isEmpty() ? "" : links.get(0).getDomAttribute("href");
    }

    public boolean isFooterDisplayed() {
        List<WebElement> footers = findAll(FOOTER);
        return !footers.isEmpty() && footers.get(0).isDisplayed();
    }

    public int getVisibleImageCount() {
        return findVisibleImages().size();
    }
}
