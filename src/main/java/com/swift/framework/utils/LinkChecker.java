package com.swift.framework.utils;

import com.swift.framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Collects every hyperlink on the current page and validates that each one
 * responds with a non-error HTTP status code.
 */
public final class LinkChecker {

    private static final Logger LOG = LogManager.getLogger(LinkChecker.class);

    private LinkChecker() {
    }

    /** Result of checking a single link. */
    public record LinkResult(String url, int statusCode, String error) {
        public boolean isBroken() {
            return error != null || statusCode <= 0 || statusCode >= 400;
        }
    }

    /** Collects unique, checkable http(s) href values from all &lt;a&gt; tags on the page. */
    public static List<String> collectLinks(WebDriver driver) {
        Set<String> links = new LinkedHashSet<>();
        List<WebElement> anchors = driver.findElements(By.tagName("a"));
        for (WebElement anchor : anchors) {
            String href = anchor.getDomAttribute("href");
            if (href == null || href.isBlank()) {
                continue;
            }
            String trimmed = href.trim();
            if (trimmed.startsWith("mailto:") || trimmed.startsWith("tel:")
                    || trimmed.startsWith("javascript:") || trimmed.equals("#")) {
                continue;
            }
            if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                links.add(trimmed);
            }
        }
        int max = ConfigManager.linkCheckMax();
        if (links.size() > max) {
            List<String> limited = new ArrayList<>(links);
            return limited.subList(0, max);
        }
        return new ArrayList<>(links);
    }

    /** Checks a list of URLs and returns only the ones considered broken. */
    public static List<LinkResult> findBrokenLinks(List<String> urls) {
        List<LinkResult> broken = new ArrayList<>();
        Duration timeout = Duration.ofMillis(ConfigManager.linkCheckTimeoutMs());
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        for (String url : urls) {
            LinkResult result = checkLink(client, url, timeout);
            LOG.debug("Checked link {} -> status {}", url, result.statusCode());
            if (result.isBroken()) {
                broken.add(result);
            }
        }
        return broken;
    }

    private static LinkResult checkLink(HttpClient client, String url, Duration timeout) {
        try {
            HttpRequest headRequest = HttpRequest.newBuilder(URI.create(url))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .timeout(timeout)
                    .header("User-Agent", "Mozilla/5.0 (SwiftSeleniumFramework LinkChecker)")
                    .build();
            HttpResponse<Void> response = client.send(headRequest, HttpResponse.BodyHandlers.discarding());
            int status = response.statusCode();

            // Some servers don't support HEAD; retry with GET before declaring broken.
            if (status == 405 || status == 501 || status >= 400) {
                HttpRequest getRequest = HttpRequest.newBuilder(URI.create(url))
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .timeout(timeout)
                        .header("User-Agent", "Mozilla/5.0 (SwiftSeleniumFramework LinkChecker)")
                        .build();
                response = client.send(getRequest, HttpResponse.BodyHandlers.discarding());
                status = response.statusCode();
            }
            return new LinkResult(url, status, null);
        } catch (Exception e) {
            return new LinkResult(url, -1, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
