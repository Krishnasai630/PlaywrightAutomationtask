package com.playwright;

import java.nio.file.Paths;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * BaseSetup provides TestNG lifecycle hooks and manages Playwright and
 * reporting resources. It initializes ExtentReports in @BeforeSuite and
 * creates a Playwright BrowserContext with video recording enabled per-test
 * in @BeforeTest. Use getPage() in page objects and tests.
 */
public class BaseSetup implements AutoCloseable {
	private Playwright playwright;
	private Browser browser;
	private BrowserContext context;
	private Page page;
	private static String platformName;
	private static String platformVersion;

	// Extent reporting
	protected static ExtentReports extent;
	protected static ExtentTest testReporter;

	/**
	 * Public accessor for tests and page objects to log to the current ExtentTest.
	 */
	public static ExtentTest getTestReporter() {
		return testReporter;
	}

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite() {
		// initialize ExtentReports
	String reportPath = Paths.get("target", "reports", "AutomationReport.html").toString();
	ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
	spark.config().setTheme(Theme.STANDARD);
	spark.config().setDocumentTitle("Automation Report");
	spark.config().setReportName("Playwright Java Tests");

	extent = new ExtentReports();
	extent.attachReporter(spark);
	}

	@BeforeTest(alwaysRun = true)
	public void beforeTest() {
		init();
		// create a default ExtentTest for this test
		testReporter = extent.createTest(this.getClass().getSimpleName());
	}

	/**
	 * Initialize Playwright, launch a browser and create a new page.
	 * BrowserContext will be created with video recording enabled and
	 * videos will land under target/videos.
	 */
	public void init() {
        // Get platform configuration from system properties
        platformName = System.getProperty("platformName", "windows10");
        platformVersion = System.getProperty("platformVersion", "10");

        // Allow tests to explicitly choose a browser via -Dbrowser=(chrome|edge|chromium|firefox|webkit)
        String browserProp = System.getProperty("browser", "").trim().toLowerCase();
        String headlessProp = System.getProperty("headless", "false").trim().toLowerCase();
        boolean headless = headlessProp.equals("true");

        playwright = Playwright.create();

        BrowserType browserType = playwright.chromium();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless);

        if (!browserProp.isEmpty()) {
            switch (browserProp) {
                case "edge":
                    // Edge is Chromium-based, use chromium with msedge channel
                    browserType = playwright.chromium();
                    launchOptions.setChannel("msedge");
                    break;
                case "chrome":
                    browserType = playwright.chromium();
                    launchOptions.setChannel("chrome");
                    break;
                case "chromium":
                    browserType = playwright.chromium();
                    // no channel, use bundled/system chromium
                    break;
                case "firefox":
                    browserType = playwright.firefox();
                    break;
                case "webkit":
                    browserType = playwright.webkit();
                    break;
                default:
                    // unknown value - fall back to platform mapping below
                    browserProp = "";
            }
        }

        // If browser not explicitly set, choose by platform
        if (browserProp.isEmpty()) {
            if (platformName.equalsIgnoreCase("windows10")) {
                browserType = playwright.chromium();
                launchOptions.setChannel("msedge");  // Use Edge on Windows 10
            } else if (platformName.equalsIgnoreCase("windows11")) {
                browserType = playwright.chromium();
                launchOptions.setChannel("chrome");  // Use Chrome on Windows 11
            }
        }

        browser = browserType.launch(launchOptions);		// Create context with platform-specific settings
		Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
				.setRecordVideoDir(Paths.get("target", "videos"))
				.setViewportSize(1920, 1080);
				
		// Add platform metadata
		contextOptions.setUserAgent("Playwright-Java/" + platformName + "/" + platformVersion);

		context = browser.newContext(contextOptions);
		page = context.newPage();
	}

	/** Returns the single Page instance for use by page objects and tests. */
	public Page getPage() {
		return page;
	}

	public Browser getBrowser() {
		return browser;
	}

	public BrowserContext getContext() {
		return context;
	}

	/** Simple helper to navigate using the shared page. */
	public void navigate(String url) {
		page.navigate(url);
	}

	/** Returns the current page title. */
	public String title() {
		return page.title();
	}

	@AfterTest(alwaysRun = true)
	public void afterTest() {
		// close Playwright resources for this test
		close();
		// flush extent for this test
		if (extent != null) {
			extent.flush();
		}
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		// final flush
		if (extent != null) {
			extent.flush();
		}
	}

	/** Cleanly close page, context, browser and playwright. Safe to call multiple times. */
	@Override
	public void close() {
		try {
			if (page != null) {
				page.close();
				page = null;
			}
		} catch (Exception ignored) {
		}
		try {
			if (context != null) {
				context.close();
				context = null;
			}
		} catch (Exception ignored) {
		}
		try {
			if (browser != null) {
				browser.close();
				browser = null;
			}
		} catch (Exception ignored) {
		}
		try {
			if (playwright != null) {
				playwright.close();
				playwright = null;
			}
		} catch (Exception ignored) {
		}
	}
}

