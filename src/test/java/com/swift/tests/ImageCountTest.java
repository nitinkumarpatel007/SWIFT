package com.swift.tests;

import com.swift.framework.base.BaseTest;
import com.swift.framework.config.ConfigManager;
import com.swift.framework.pages.CareersPage;
import com.swift.framework.pages.HomePage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Verifies each page under test renders more than one visible image. */
@Epic("Swift.com Website")
@Feature("Image Rendering")
public class ImageCountTest extends BaseTest {

    @Test(description = "Home page displays more than one image")
    @Severity(SeverityLevel.NORMAL)
    @Description("Counts visible <img> elements on the home page and asserts more than 1 is rendered")
    public void homePageHasMoreThanOneImage() {
        HomePage homePage = new HomePage(driver()).open();
        int imageCount = homePage.getVisibleImageCount();
        Assert.assertTrue(imageCount > 1,
                "Expected more than 1 visible image on the home page (" + ConfigManager.baseUrl() + "), but found " + imageCount);
    }

    @Test(description = "Careers page displays more than one image")
    @Severity(SeverityLevel.NORMAL)
    @Description("Counts visible <img> elements on the careers page and asserts more than 1 is rendered")
    public void careersPageHasMoreThanOneImage() {
        CareersPage careersPage = new CareersPage(driver()).open();
        int imageCount = careersPage.getVisibleImageCount();
        Assert.assertTrue(imageCount > 1,
                "Expected more than 1 visible image on the careers page (" + ConfigManager.careersUrl() + "), but found " + imageCount);
    }
}
