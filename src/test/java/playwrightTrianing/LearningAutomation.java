package playwrightTrianing;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class LearningAutomation {

	public static void main(String[] args) {
		
		Playwright pl= Playwright.create();
		
		BrowserType bt= pl.chromium();// TODO Auto-generated method stub
		
		Browser br = bt.launch(new BrowserType.LaunchOptions().setHeadless(false));
		
		Page pg = br.newPage();
		
		 pg.navigate("https://opensource-demo.orangehrmlive.com/");
		 
		 System.out.println("=== VALIDATION: Page Title is >>> " + pg.title() + " ===");
		 
		 br.close();
		 
		 pl.close();
		 
	}

}
