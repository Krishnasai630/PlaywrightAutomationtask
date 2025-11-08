package pages;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.microsoft.playwright.Page;
import com.playwright.BaseSetup;
import com.playwright.utils.ScreenshotUtil;

public class OtherTabPage {
    private final Page page;

    public OtherTabPage(Page page) {
        this.page = page;
    }

    public String extractHeadingText(String selector) {
        try {
            BaseSetup.getTestReporter().info("Extracting text from selector: " + selector);
            String text = page.textContent(selector);
            BaseSetup.getTestReporter().pass("Extracted text: " + text);
            return text;
        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(page, "extractHeadingText");
            if (shot != null) BaseSetup.getTestReporter().fail("Failed to extract text", MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
            else BaseSetup.getTestReporter().fail("Failed to extract text: " + e.getMessage());
            return null;
        }
    }

    public void performMouseAction(String selector) {
        try {
            BaseSetup.getTestReporter().info("Performing mouse move & click on: " + selector);
            page.hover(selector);
            page.click(selector);
            BaseSetup.getTestReporter().pass("Mouse actions performed");
        } catch (Exception e) {
            String shot = ScreenshotUtil.takeScreenshot(page, "performMouseAction");
            if (shot != null) BaseSetup.getTestReporter().fail("Failed mouse action", MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
            else BaseSetup.getTestReporter().fail("Failed mouse action: " + e.getMessage());
        }
    }
}
