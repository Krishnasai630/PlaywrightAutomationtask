package pages;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.KeyboardModifier;
import com.playwright.BaseSetup;
import com.playwright.utils.ScreenshotUtil;
import java.util.List;

/**
 * Page object that contains actions performed on the main page: keyboard,
 * fill, and opening a new tab.
 */
public class MainPageActions {
    private final Page page;

    public MainPageActions(Page page) {
        this.page = page;
    }

    public void typeTextWithKeyboard(String selector, String text) {
        try {
            BaseSetup.getTestReporter().info("Typing text into " + selector + ": " + text);
            page.click(selector);
            page.keyboard().type(text);
            BaseSetup.getTestReporter().pass("Typed text successfully");
        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(page, "typeTextWithKeyboard");
            if (shot != null) BaseSetup.getTestReporter().fail("Failed to type text", MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
            else BaseSetup.getTestReporter().fail("Failed to type text: " + e.getMessage());
        }
    }

    public void enterTextDirect(String selector, String text) {
        try {
            BaseSetup.getTestReporter().info("Filling text into " + selector + ": " + text);
            page.fill(selector, text);
            BaseSetup.getTestReporter().pass("Filled text successfully");
        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(page, "enterTextDirect");
            if (shot != null) BaseSetup.getTestReporter().fail("Failed to fill text", MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
            else BaseSetup.getTestReporter().fail("Failed to fill text: " + e.getMessage());
        }
    }

    /**
     * Open a new tab by clicking a link that opens in new window or using
     * ctrl/cmd+click. Returns the new Page instance.
     */
    public Page openLinkInNewTab(String selector) {
        try {
            BaseSetup.getTestReporter().info("Opening link in new tab: " + selector);
            // Use modifier to open new tab (Ctrl+Click on Windows)
            page.click(selector, new Page.ClickOptions().setModifiers(List.of(KeyboardModifier.CONTROL)));
            // Wait for the new page in the browser context
            Page newPage = page.context().waitForPage(() -> {});
            BaseSetup.getTestReporter().pass("New tab opened");
            return newPage;
        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(page, "openLinkInNewTab");
            if (shot != null) BaseSetup.getTestReporter().fail("Failed to open link in new tab", MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
            else BaseSetup.getTestReporter().fail("Failed to open link in new tab: " + e.getMessage());
            return null;
        }
    }
}
