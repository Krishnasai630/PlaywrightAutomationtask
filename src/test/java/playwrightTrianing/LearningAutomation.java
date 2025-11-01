package playwrightTrianing;

import org.testng.annotations.Test;

import com.playwright.BaseSetup;

import pages.HomePage;
import pages.LoginPage;

/** TestNG test that demonstrates login using the POM and BaseSetup. */
public class LearningAutomation extends BaseSetup {

	@Test(description = "Smoke test: login to OrangeHRM and verify dashboard")
	public void loginSmokeTest() {
		String url = "https://opensource-demo.orangehrmlive.com/";
		String username = "Admin";
		String password = "admin123";

		LoginPage login = new LoginPage(getPage());
		HomePage home = new HomePage(getPage());

		login.login(url, username, password);

		System.out.println("Page title after login: " + title());
		System.out.println("Dashboard visible: " + home.isDashboardVisible());
		System.out.println("Welcome message visible: " + home.isWelcomeMessageVisible());
	}
}

