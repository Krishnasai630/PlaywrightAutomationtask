package playwrightTrianing;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;

import com.microsoft.playwright.Page;
import com.playwright.BaseSetup;

public class MultipleWindowHandlingTest extends BaseSetup {

    @org.testng.annotations.BeforeMethod
    public void setUp() {
        init();
    }

    @Test
    public void testMultipleWindowHandling() {
        try {
            // Start with the main page
            testReporter.log(Status.INFO, "Starting multiple window handling test");
            
            // Navigate to a demo site that has multiple windows
            navigate("https://the-internet.herokuapp.com/windows");
            testReporter.log(Status.PASS, "Navigated to the demo site successfully");

            // Get initial page title
            String mainPageTitle = title();
            testReporter.log(Status.INFO, "Main page title: " + mainPageTitle);

            Page mainPage = getPage();
            
            // Click the link and wait for the new window
            mainPage.waitForSelector("a[href='/windows/new']");
            Page newWindow = getContext().waitForPage(() -> {
                mainPage.click("a[href='/windows/new']");
            });
            
            testReporter.log(Status.PASS, "Clicked link and opened new window");
            
            // Wait for the new window to load
            newWindow.waitForLoadState();
            String newWindowTitle = newWindow.title();
            testReporter.log(Status.INFO, "New window title: " + newWindowTitle);
            
            // Read content in the new window
            String windowText = newWindow.textContent("body");
            if (windowText != null && !windowText.isEmpty()) {
                testReporter.log(Status.PASS, "Successfully read new window content: " + windowText);
            }
            
            // Close the new window
            newWindow.close();
            testReporter.log(Status.PASS, "Closed new window");
            
            // Switch back to main page
            mainPage.bringToFront();
            testReporter.log(Status.PASS, "Switched back to main page successfully");

            // Verify we're back on main page
            String currentTitle = mainPage.title();
            if (currentTitle.equals(mainPageTitle)) {
                testReporter.log(Status.PASS, "Successfully verified return to main page");
            } else {
                testReporter.log(Status.FAIL, "Failed to return to main page. Current title: " + currentTitle);
            }

            // Try to find and click a link that opens in a new tab
            // First make sure we have a link that opens in a new window
            mainPage.waitForSelector("a[href='/windows/new']");
            Page popup = getContext().waitForPage(() -> {
                mainPage.click("a[href='/windows/new']");
            });
            
            if (popup != null) {
                testReporter.log(Status.PASS, "Successfully opened and captured new popup window");
                popup.waitForLoadState();
                testReporter.log(Status.INFO, "New window title: " + popup.title());
                
                // Verify the new window content
                String popupText = popup.textContent("body");
                if (popupText != null && !popupText.isEmpty()) {
                    testReporter.log(Status.PASS, "Successfully read popup window content: " + popupText);
                } else {
                    testReporter.log(Status.FAIL, "Failed to read popup window content");
                }
                
                // Close the popup
                popup.close();
                testReporter.log(Status.PASS, "Closed popup window");
            }

            // Final verification on main page
            if (mainPage.title().equals(mainPageTitle)) {
                testReporter.log(Status.PASS, "Test completed successfully on main page");
            }

        } catch (Exception e) {
            testReporter.log(Status.FAIL, "Test failed with exception: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testMultipleContexts() {
        try {
            testReporter.log(Status.INFO, "Starting multiple browser contexts test");
            
            // Create a new incognito browser context
            Browser browser = getBrowser();
            try (BrowserContext incognitoContext = browser.newContext()) {
                testReporter.log(Status.PASS, "Created new incognito context");
                
                // Create a page in the incognito context
                Page incognitoPage = incognitoContext.newPage();
                testReporter.log(Status.PASS, "Created new page in incognito context");
                
                // Navigate to a site in incognito
                incognitoPage.navigate("https://www.selenium.dev/");
                testReporter.log(Status.PASS, "Navigated to site in incognito context");
                
                // Get the title in incognito
                String incognitoTitle = incognitoPage.title();
                testReporter.log(Status.INFO, "Incognito page title: " + incognitoTitle);
                
                // Switch back to regular context and verify
                Page regularPage = getPage();
                regularPage.bringToFront();
                testReporter.log(Status.PASS, "Switched back to regular context");
                
                testReporter.log(Status.PASS, "Successfully completed multiple contexts test");
            }
            
        } catch (Exception e) {
            testReporter.log(Status.FAIL, "Test failed with exception: " + e.getMessage());
            throw e;
        }
    }
}