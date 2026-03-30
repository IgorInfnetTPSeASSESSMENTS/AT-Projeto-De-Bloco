package adopet.selenium.pages.adoptionrequests;

import adopet.selenium.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;

public class AdoptionRequestEditPage extends BasePage {

    private static final By PAGE_TITLE = By.id("page-title");

    private static final By APPLICANT_NAME = By.id("applicantName");
    private static final By APPLICANT_EMAIL = By.id("applicantEmail");
    private static final By APPLICANT_PHONE = By.id("applicantPhone");
    private static final By APPLICANT_DOCUMENT = By.id("applicantDocument");
    private static final By HOUSING_TYPE = By.id("housingType");
    private static final By HAS_OTHER_PETS = By.id("hasOtherPets");
    private static final By REASON = By.id("reason");

    private static final By SUBMIT = By.id("submit-adoption-request");

    public AdoptionRequestEditPage(WebDriver driver) {
        super(driver);
        ensureLoaded();
    }

    public void ensureLoaded() {
        ensureTitle(PAGE_TITLE, "Editar solicitação", "Adoption request edit page");
    }

    public AdoptionRequestEditPage fillApplicantName(String value) {
        type(APPLICANT_NAME, value);
        return this;
    }

    public AdoptionRequestEditPage fillApplicantEmail(String value) {
        type(APPLICANT_EMAIL, value);
        return this;
    }

    public AdoptionRequestEditPage fillApplicantPhone(String value) {
        type(APPLICANT_PHONE, value);
        return this;
    }

    public AdoptionRequestEditPage fillApplicantDocument(String value) {
        type(APPLICANT_DOCUMENT, value);
        return this;
    }

    public AdoptionRequestEditPage selectHousingType(String value) {
        selectByValue(HOUSING_TYPE, value);
        return this;
    }

    public AdoptionRequestEditPage setHasOtherPets(boolean checked) {
        setCheckbox(HAS_OTHER_PETS, checked);
        return this;
    }

    public AdoptionRequestEditPage fillReason(String value) {
        type(REASON, value);
        return this;
    }

    public AdoptionRequestsListPage submitSuccess() {
        click(SUBMIT);

        wait.until(driver -> {
            try {
                String title = driver.findElement(PAGE_TITLE).getText();
                return "Solicitações de adoção".equals(title);
            } catch (StaleElementReferenceException ignored) {
                return false;
            } catch (Exception ignored) {
                return false;
            }
        });

        return new AdoptionRequestsListPage(driver);
    }
}