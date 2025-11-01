package pages;

import com.microsoft.playwright.Page;

/**
 * Page object for the OrangeHRM login page. Encapsulates selectors and
 * actions so tests remain readable and maintainable.
 */
public class LoginPage {
	private final Page page;

	// Locators
	private final String usernameInput = "input[name='username'], input#txtUsername, input[placeholder='Username']";
	private final String passwordInput = "input[name='password'], input#txtPassword, input[placeholder='Password']";
	private final String loginButton = "button[type='submit'], input[type='submit'], button#btnLogin, .orangehrm-login-button";

	public LoginPage(Page page) {
		this.page = page;
	}

	public void navigateTo(String url) {
		page.navigate(url);
	}

	public void enterUsername(String username) {
		page.fill(usernameInput, username);
	}

	public void enterPassword(String password) {
		page.fill(passwordInput, password);
	}

	public void clickLogin() {
		page.click(loginButton);
	}

	/** Convenience method: perform login end-to-end */
	public void login(String url, String username, String password) {
		navigateTo(url);
		enterUsername(username);
		enterPassword(password);
		clickLogin();
		// wait for either dashboard navigation or the user dropdown to be visible
		try {
			// prefer waiting for dashboard URL
			page.waitForURL("**/dashboard**", new Page.WaitForURLOptions().setTimeout(10000));
		} catch (Exception e) {
			try {
				page.waitForSelector(".oxd-userdropdown-name", new Page.WaitForSelectorOptions().setTimeout(8000));
			} catch (Exception ignored) {
				// if both waits fail, continue — callers should handle visibility assertions
			}
		}
	}
}
