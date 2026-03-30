package adopet.selenium.pages.shelters;

import adopet.selenium.base.BasePage;
import adopet.selenium.pages.pets.PetsListPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SheltersListPage extends BasePage {

    private static final By PAGE_TITLE = By.id("page-title");
    private static final By SHELTERS_TABLE = By.id("shelters-table");
    private static final By LINK_CREATE_SHELTER = By.id("link-create-shelter");

    public SheltersListPage(WebDriver driver) {
        super(driver);
        ensureLoaded();
    }

    public void ensureLoaded() {
        ensureTitle(PAGE_TITLE, "Abrigos", "Shelters list page");
        visible(SHELTERS_TABLE);
    }

    public ShelterCreatePage goToCreateShelter() {
        click(LINK_CREATE_SHELTER);
        return new ShelterCreatePage(driver);
    }


    public PetsListPage viewPetsOfShelterNamed(String shelterName) {
        click(linkInsideRowContainingText("shelters-table", shelterName, "view-pets-"));
        return new PetsListPage(driver);
    }

    public String pageSource() {
        return driver.getPageSource();
    }
}