# Swift Selenium Test Automation Framework

A modern, modular, cross-browser Selenium WebDriver + TestNG automation
framework built to test **[swift.com](https://www.swift.com/)**. Follows the
Page Object Model, supports parallel execution (local and remote/cloud), and
produces both **ExtentReports** and **Allure** reports.

## Tech stack

| Concern            | Tool |
|---------------------|------|
| Language            | Java 21 |
| Build tool          | Maven |
| Test runner         | TestNG |
| Browser automation  | Selenium WebDriver 4.x |
| Driver management   | WebDriverManager (auto-downloads browser drivers) |
| Design pattern      | Page Object Model (POM) |
| Logging             | Log4j2 (SLF4J bridge) |
| Reporting           | ExtentReports + Allure |
| Remote/cloud grid   | LambdaTest (also supports a generic Selenium Grid) |
| CI/CD               | Jenkins (`Jenkinsfile`) + GitHub Actions (`.github/workflows/ci.yml`) |

## Project structure

```
├── pom.xml
├── testng.xml                     # default suite (Chrome, local, parallel by class)
├── testng-cross-browser.xml       # same suite across Chrome/Firefox/Edge in parallel
├── testng-lambdatest.xml          # same suite on LambdaTest cloud grid
├── Jenkinsfile
├── .github/workflows/ci.yml
├── src/main/java/com/swift/framework
│   ├── base/BaseTest.java         # @BeforeMethod/@AfterMethod driver lifecycle
│   ├── config/ConfigManager.java  # config.properties + system property overrides
│   ├── driver/DriverFactory.java  # local + remote(Grid/LambdaTest) driver creation
│   ├── driver/DriverManager.java  # ThreadLocal<WebDriver> for safe parallel runs
│   ├── enums/                     # BrowserType, ExecutionMode
│   ├── listeners/                 # TestListener (Extent+Allure+screenshots), RetryAnalyzer
│   ├── pages/                     # BasePage, HomePage, CareersPage (POM)
│   ├── reports/ExtentManager.java
│   └── utils/                     # WaitUtils, ScreenshotUtils, LinkChecker, JsonDataReader
├── src/test/java/com/swift/tests
│   ├── HomePageTests.java
│   ├── CareersPageTests.java
│   ├── BrokenLinksTest.java        # "no broken links" check (data-driven: home + careers)
│   └── ImageCountTest.java         # "more than 1 image" check (home + careers)
└── src/test/resources
    ├── config.properties
    ├── log4j2.xml
    └── testdata/*.json             # test data kept separate from test logic
```

## Prerequisites

- JDK 21+
- Maven 3.9+
- Chrome/Firefox/Edge installed locally (only needed for local execution — driver
  binaries are downloaded automatically by WebDriverManager)

## Configuration

All settings live in [`src/test/resources/config.properties`](src/test/resources/config.properties)
and can be overridden from the command line with `-D<key>=<value>`:

| Key | Default | Description |
|---|---|---|
| `browser` | `chrome` | `chrome` \| `firefox` \| `edge` |
| `headless` | `false` | Run browser headless |
| `execution.mode` | `local` | `local` \| `remote` |
| `remote.provider` | `lambdatest` | `lambdatest` \| `grid` |
| `grid.url` | `http://localhost:4444/wd/hub` | Selenium Grid hub URL |
| `base.url` | `https://www.swift.com` | Home page under test |
| `careers.url` | `https://www.swift.com/about-us/careers` | Careers page under test |

LambdaTest credentials are read from environment variables (never hard-coded):

```bash
export LT_USERNAME=your_lambdatest_username
export LT_ACCESS_KEY=your_lambdatest_access_key
```

## Running tests

```bash
# Default suite, Chrome, local
mvn clean test

# Explicit browser / headless mode
mvn clean test -Dbrowser=firefox -Dheadless=true

# Cross-browser suite (Chrome + Firefox + Edge in parallel)
mvn clean test -Dsuite=testng-cross-browser.xml

# Run on LambdaTest (requires LT_USERNAME / LT_ACCESS_KEY)
mvn clean test -Dsuite=testng-lambdatest.xml -Denv=remote
```

Parallel execution is controlled by the `parallel` / `thread-count` attributes
in the TestNG suite XML files; `DriverManager` keeps one `WebDriver` per thread
so parallel runs never share a browser session.

## Reports

- **ExtentReports** — generated at `test-output/extent-report/SwiftAutomationReport-<timestamp>.html`
- **Allure** — raw results at `target/allure-results`; view locally with:
  ```bash
  mvn allure:serve
  ```
- **TestNG native HTML report** — generated automatically by TestNG's default
  listeners at `test-output/index.html` (and `test-output/emailable-report.html`)
  after every run.
- **Surefire HTML report** — a browsable summary built from the Surefire/TestNG
  XML results via the `maven-surefire-report-plugin`:
  ```bash
  mvn surefire-report:report-only
  ```
  outputs `target/site/surefire-report.html`.
- Screenshots on failure are saved to `test-output/screenshots` and embedded
  into both the Extent and Allure reports automatically via `TestListener`.

### Opening the report after a run

- **Extent**: open the newest file in `test-output/extent-report/` (double-click
  it in File Explorer, or run `Start-Process test-output\extent-report\<file>.html`
  in PowerShell / `start test-output\extent-report\<file>.html` in Command Prompt).
- **Allure**: run `mvn allure:serve` (spins up a local server and opens the
  report in your browser) or `mvn allure:report` followed by opening
  `target/site/allure-maven-plugin/index.html`.
- **TestNG/Surefire HTML**: open `test-output/index.html` directly, or run
  `mvn surefire-report:report-only` and open `target/site/surefire-report.html`.

In CI:
- **Jenkins** publishes the Allure report via the Allure Jenkins plugin and the
  Extent report via the HTML Publisher plugin; links are printed at the end of
  each build (see `Jenkinsfile`).
- **GitHub Actions** publishes the Allure report to GitHub Pages (`gh-pages`
  branch) and uploads the Extent report as a downloadable artifact; both links
  are written to the workflow run's Job Summary (see `.github/workflows/ci.yml`).

## Test scenarios included

| Test class | Scenario |
|---|---|
| `HomePageTests` | Title, logo, hero heading, footer/social links, nav menu, navigation to careers page |
| `CareersPageTests` | Title, "Careers at Swift" heading, "Our positions" section, open-positions (Workday) link, footer |
| `BrokenLinksTest` | Collects every `<a href>` on the home page and careers page and asserts none return an HTTP 4xx/5xx (or connection error) |
| `ImageCountTest` | Asserts more than 1 visible `<img>` renders on the home page and the careers page |

Login-related scenarios are intentionally out of scope for this suite.

## Writing a new test (example)

```java
public class SampleTest extends BaseTest {

    @Test
    public void homePageHasLogo() {
        HomePage homePage = new HomePage(driver()).open();
        Assert.assertTrue(homePage.isLogoDisplayed());
    }
}
```

1. Extend `BaseTest` — it wires up the browser via `@BeforeMethod`/`@AfterMethod`.
2. Interact with the page only through a page object under `pages/`.
3. Add new locators/actions to the relevant page object, not the test class.
4. Put static test data in `src/test/resources/testdata/*.json` and read it via `JsonDataReader`.
