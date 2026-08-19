package com.swift.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Page object for the Swift.com public home page (https://www.swift.com/). */
public class HomePage extends BasePage {

    private static final By LOGO = By.cssSelector("header a[href='/'], a.header__logo, a[href='https://www.swift.com/']");
    private static final By MAIN_HEADING = By.cssSelector("h1");
    private static final By LOGIN_LINK = By.xpath("//a[contains(@href,'/login')]");
    private static final By FOOTER = By.tagName("footer");
    private static final By FOOTER_CAREERS_LINK = By.xpath("//a[@data-tracking-interaction='menu_link'][normalize-space()='Careers']");
    private static final By FOOTER_SOCIAL_LINKS = By.cssSelector( "footer a[href*='x.com'], " +
    "footer a[href*='twitter.com'], " +
    "footer a[href*='linkedin.com'], " +
    "footer a[href*='facebook.com'], " +
    "footer a[href*='youtube.com']");
    private static final By COPYRIGHT = By.xpath("//footer//*[contains(text(),'Swift')]");
    private static final By NAV_MENU_ITEMS = By.cssSelector("nav a, header a");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        driver.get(com.swift.framework.config.ConfigManager.baseUrl());
        acceptCookiesIfPresent();
        return this;
    }

    public boolean isLogoDisplayed() {
        return !findAll(LOGO).isEmpty();
    }

    public String getMainHeadingText() {
        List<WebElement> headings = findAll(MAIN_HEADING);
        return headings.isEmpty() ? "" : headings.get(0).getText().trim();
    }

    public boolean isLoginLinkPresent() {
        return !findAll(LOGIN_LINK).isEmpty();
    }

    public boolean isFooterDisplayed() {
        List<WebElement> footers = findAll(FOOTER);
        return !footers.isEmpty() && footers.get(0).isDisplayed();
    }

    public boolean isSocialLinksSectionPresent() {
        return !findAll(FOOTER_SOCIAL_LINKS).isEmpty();
    }

    public int getVisibleImageCount() {
        return findVisibleImages().size();
    }

    public List<WebElement> getNavMenuItems() {
        return findAll(NAV_MENU_ITEMS);
    }

    public String getCopyrightText() {
        List<WebElement> matches = findAll(COPYRIGHT);
        return matches.isEmpty() ? "" : matches.get(0).getText().trim();
    }

    public CareersPage goToCareersPage() {
        List<WebElement> links = findAll(FOOTER_CAREERS_LINK);
        if (links.isEmpty()) {
            driver.get(com.swift.framework.config.ConfigManager.careersUrl());
        } else {
            scrollIntoView(links.get(0));
            links.get(0).click();
        }
        return new CareersPage(driver);
    }
}
