package adopet.selenium.pages.pets;

import adopet.selenium.base.BasePage;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestsListPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PetsListPage extends BasePage {

    private static final By PAGE_TITLE = By.id("page-title");
    private static final By PETS_TABLE = By.id("pets-table");
    private static final By LINK_CREATE_PET = By.id("link-create-pet");

    public PetsListPage(WebDriver driver) {
        super(driver);
        ensureLoaded();
    }

    public void ensureLoaded() {
        ensureTitleStartsWith(PAGE_TITLE, "Pets do abrigo: ", "Pets list page");
        visible(PETS_TABLE);
    }

    public PetCreatePage goToCreatePet() {
        click(LINK_CREATE_PET);
        return new PetCreatePage(driver);
    }


    public AdoptionRequestsListPage manageAdoptionsOfPetNamed(String petName) {
        click(linkInsideRowContainingText("pets-table", petName, "manage-adoptions-pet-"));
        return new AdoptionRequestsListPage(driver);
    }

}