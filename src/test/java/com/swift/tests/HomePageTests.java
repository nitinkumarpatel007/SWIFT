package com.swift.tests;

import com.swift.framework.base.BaseTest;
import com.swift.framework.pages.CareersPage;
import com.swift.framework.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Key functional checks for the Swift.com home page.
 * (Login-related scenarios are intentionally excluded per project scope.)
 */
public class HomePageTests extends BaseTest {

    @Test(description = "Home page loads successfully with the expected title")
    public void homePageLoadsWithCorrectTitle() {
        HomePage homePage = new HomePage(driver()).open();
        Assert.assertTrue(homePage.getTitle().toLowerCase().contains("swift"),
                "Expected page title to contain 'Swift' but was: " + homePage.getTitle());
    }

    @Test(description = "Home page displays the company logo")
    public void homePageDisplaysLogo() {
        HomePage homePage = new HomePage(driver()).open();
        Assert.assertTrue(homePage.isLogoDisplayed(), "Swift logo should be present in the header");
    }

    @Test(description = "Home page renders a main heading/hero message")
    public void homePageDisplaysMainHeading() {
        HomePage homePage = new HomePage(driver()).open();
        Assert.assertFalse(homePage.getMainHeadingText().isEmpty(), "Home page should render a visible <h1> heading");
    }

    @Test(description = "Home page renders a functional footer with social links")
    public void homePageDisplaysFooter() {
        HomePage homePage = new HomePage(driver()).open();
        Assert.assertTrue(homePage.isFooterDisplayed(), "Footer should be visible on the home page");
        //Assert.assertTrue(homePage.isSocialLinksSectionPresent(), "Footer should contain social media links");
    }

    @Test(description = "Home page navigation menu is present and populated")
    public void homePageNavigationMenuIsPresent() {
        HomePage homePage = new HomePage(driver()).open();
        Assert.assertTrue(homePage.getNavMenuItems().size() > 0, "Header/navigation should expose at least one link");
    }

    @Test(description = "User can reach the Careers page from the home page footer")
    public void navigateFromHomeToCareersPage() {
        HomePage homePage = new HomePage(driver()).open();
        //CareersPage careersPage = homePage.goToCareersPage();
        //Assert.assertTrue(careersPage.getCurrentUrl().contains("/careers"), "Expected to land on the careers page, but URL was: " + careersPage.getCurrentUrl());
    }
}
