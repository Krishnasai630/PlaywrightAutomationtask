package pages;

import com.microsoft.playwright.Page;

public class HomePage {
    private final Page page;

    private final String dashboardText = "text=Dashboard";
    private final String userDropdown = ".oxd-userdropdown-name";

    public HomePage(Page page) {
        this.page = page;
    }

    public boolean isDashboardVisible() {
        return safeIsVisible(dashboardText);
    }

    public String getPageTitle() {
        return page.title();
    }

    public boolean isWelcomeMessageVisible() {
        return safeIsVisible(userDropdown);
    }

    private boolean safeIsVisible(String selector) {
        try {
            return page.isVisible(selector);
        } catch (Exception e) {
            return false;
        }
    }
}

