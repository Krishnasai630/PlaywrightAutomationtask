package playwrightTrianing;

import org.testng.annotations.Test;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.microsoft.playwright.Page;
import com.playwright.BaseSetup;
import com.playwright.utils.ScreenshotUtil;

import pages.MainPageActions;
import pages.OtherTabPage;

/**
 * Test demonstrates keyboard actions, text entry, opening another tab,
 * extracting text and mouse interactions. Uses https://the-internet.herokuapp.com
 * as a sample site (contains pages for new window and inputs).
 */
public class ActionsTest extends BaseSetup {

    @Test
    public void keyboardAndTabActions() {
        try {
            String base = "https://the-internet.herokuapp.com";
            Page p = getPage();
            navigate(base + "/inputs");

            MainPageActions main = new MainPageActions(p);
            // type numbers using keyboard (inputs page has a single input)
            try {
                main.typeTextWithKeyboard("input[type='number']", "12345");
            } catch (Exception e) {
                String shot = ScreenshotUtil.takeScreenshot(p, "step-typeText");
                if (shot != null) getTestReporter().fail("type step failed", MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
            }

            // direct fill
            try {
                main.enterTextDirect("input[type='number']", "67890");
            } catch (Exception e) {
                String shot = ScreenshotUtil.takeScreenshot(p, "step-fillText");
                if (shot != null) getTestReporter().fail("fill step failed", MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
            }

            // open a page that opens a new window/tab
            navigate(base + "/windows");
            // click the link that opens a new window — use the link selector
            Page newPage = null;
            try {
                MainPageActions actions = new MainPageActions(p);
                newPage = actions.openLinkInNewTab("a[href='/windows/new']");
            } catch (Exception e) {
                String shot = ScreenshotUtil.takeScreenshot(p, "step-openNewTab");
                if (shot != null) getTestReporter().fail("open new tab failed", MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
            }

            // If newPage is null, try to get it from context pages
            if (newPage == null) {
                if (p.context().pages().size() > 1) {
                    newPage = p.context().pages().get(p.context().pages().size() - 1);
                }
            }

            if (newPage != null) {
                OtherTabPage other = new OtherTabPage(newPage);
                try {
                    String heading = other.extractHeadingText("h3");
                    getTestReporter().info("Heading on other tab: " + heading);
                } catch (Exception e) {
                    String shot = ScreenshotUtil.takeScreenshot(newPage, "step-extract");
                    if (shot != null) getTestReporter().fail("extract failed", MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
                }

                try {
                    other.performMouseAction("h3");
                } catch (Exception e) {
                    String shot = ScreenshotUtil.takeScreenshot(newPage, "step-mouse");
                    if (shot != null) getTestReporter().fail("mouse action failed", MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
                }

                // close new page and switch back
                try {
                    newPage.close();
                    getTestReporter().pass("Closed other tab and returned to main page");
                } catch (Exception e) {
                    String shot = ScreenshotUtil.takeScreenshot(p, "step-close");
                    if (shot != null) getTestReporter().fail("close tab failed", MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
                }
            } else {
                getTestReporter().warning("Other tab was not available to perform actions");
            }

        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(getPage(), "test-exception");
            if (shot != null) getTestReporter().fail("Test failed unexpectedly", MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
            else getTestReporter().fail("Test failed unexpectedly: " + e.getMessage());
            throw e;
        }
    }
}