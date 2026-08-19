package com.swift.tests;

import com.swift.framework.base.BaseTest;
import com.swift.framework.config.ConfigManager;
import com.swift.framework.utils.LinkChecker;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

/** Generic "no broken links" check, applied to both the home page and the careers page. */
@Epic("Swift.com Website")
@Feature("Link Integrity")
public class BrokenLinksTest extends BaseTest {

    @DataProvider(name = "pages")
    public Object[][] pages() {
        return new Object[][]{
                {"Home Page", ConfigManager.baseUrl()},
                {"Careers Page", ConfigManager.careersUrl()}
        };
    }

    @Test(dataProvider = "pages", description = "None of the hyperlinks on the page should be broken")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Collects every <a href> on the page and asserts each one responds without a 4xx/5xx status")
    public void pageHasNoBrokenLinks(String pageName, String url) {
        driver().get(url);

        List<String> links = LinkChecker.collectLinks(driver());
        Assert.assertFalse(links.isEmpty(), pageName + " should contain at least one link to validate");

        List<LinkChecker.LinkResult> broken = LinkChecker.findBrokenLinks(links);

        String brokenSummary = broken.stream()
                .map(r -> r.url() + " -> " + (r.error() != null ? r.error() : "HTTP " + r.statusCode()))
                .collect(Collectors.joining(System.lineSeparator()));

        Assert.assertTrue(broken.isEmpty(),
                pageName + ": found " + broken.size() + " broken link(s) out of " + links.size() + " checked:"
                        + System.lineSeparator() + brokenSummary);
    }
}
