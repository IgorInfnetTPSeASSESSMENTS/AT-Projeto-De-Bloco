package adopet.selenium.pages;

import adopet.selenium.base.BasePage;
import adopet.selenium.pages.shelters.SheltersListPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private static final By PAGE_TITLE = By.id("page-title");
    private static final By LINK_SHELTERS = By.id("link-shelters");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open(String baseUrl) {
        driver.get(baseUrl + "/");
        ensureLoaded();
        return this;
    }

    public void ensureLoaded() {
        ensureTitle(PAGE_TITLE, "ADOPET", "Home page");
    }

    public SheltersListPage goToShelters() {
        click(LINK_SHELTERS);
        return new SheltersListPage(driver);
    }
}