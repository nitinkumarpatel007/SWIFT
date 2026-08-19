package com.swift.tests;

import com.swift.framework.base.BaseTest;
import com.swift.framework.pages.CareersPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Key functional checks for the Swift.com careers page. */
@Epic("Swift.com Website")
@Feature("Careers Page")
public class CareersPageTests extends BaseTest {

    @Test(description = "Careers page loads successfully with the expected title")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies https://www.swift.com/about-us/careers loads correctly")
    public void careersPageLoadsWithCorrectTitle() {
        CareersPage careersPage = new CareersPage(driver()).open();
        Assert.assertTrue(careersPage.getTitle().toLowerCase().contains("career"),
                "Expected page title to reference 'career' but was: " + careersPage.getTitle());
    }

    @Test(description = "Careers page displays the 'Careers at Swift' heading")
    @Severity(SeverityLevel.CRITICAL)
    public void careersPageDisplaysHeading() {
        CareersPage careersPage = new CareersPage(driver()).open();
        Assert.assertTrue(careersPage.getMainHeadingText().toLowerCase().contains("career"),
                "Expected heading to mention 'career' but was: " + careersPage.getMainHeadingText());
    }

    @Test(description = "Careers page lists the 'Our positions' section")
    @Severity(SeverityLevel.NORMAL)
    public void careersPageDisplaysPositionsSection() {
        CareersPage careersPage = new CareersPage(driver()).open();
        Assert.assertTrue(careersPage.isOurPositionsSectionDisplayed(), "'Our positions' section should be present");
    }

    @Test(description = "Careers page provides a working link to open job positions")
    @Severity(SeverityLevel.CRITICAL)
    public void careersPageHasOpenPositionsLink() {
        CareersPage careersPage = new CareersPage(driver()).open();
        Assert.assertTrue(careersPage.isOpenPositionsLinkPresent(), "Open positions link should be present");
        Assert.assertTrue(careersPage.getOpenPositionsLinkHref().contains("myworkdayjobs.com"),
                "Open positions link should point to the Workday job board");
    }

    @Test(description = "Careers page renders a functional footer")
    @Severity(SeverityLevel.MINOR)
    public void careersPageDisplaysFooter() {
        CareersPage careersPage = new CareersPage(driver()).open();
        Assert.assertTrue(careersPage.isFooterDisplayed(), "Footer should be visible on the careers page");
    }
}
