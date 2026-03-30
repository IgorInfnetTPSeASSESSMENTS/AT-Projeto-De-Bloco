package adopet.selenium.pages.adoptionrequests;

import adopet.selenium.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AdoptionRequestCreatePage extends BasePage {

    private static final By PAGE_TITLE = By.id("page-title");
    private static final By APPLICANT_NAME = By.id("applicantName");
    private static final By APPLICANT_EMAIL = By.id("applicantEmail");
    private static final By APPLICANT_PHONE = By.id("applicantPhone");
    private static final By APPLICANT_DOCUMENT = By.id("applicantDocument");
    private static final By HOUSING_TYPE = By.id("housingType");
    private static final By HAS_OTHER_PETS = By.id("hasOtherPets");
    private static final By REASON = By.id("reason");
    private static final By SUBMIT = By.id("submit-adoption-request");

    public AdoptionRequestCreatePage(WebDriver driver) {
        super(driver);
        ensureLoaded();
    }

    public void ensureLoaded() {
        ensureTitle(PAGE_TITLE, "Nova solicitação de adoção", "Adoption request create page");
    }

    public AdoptionRequestCreatePage fillApplicantName(String value) {
        type(APPLICANT_NAME, value);
        return this;
    }

    public AdoptionRequestCreatePage fillApplicantEmail(String value) {
        type(APPLICANT_EMAIL, value);
        return this;
    }

    public AdoptionRequestCreatePage fillApplicantPhone(String value) {
        type(APPLICANT_PHONE, value);
        return this;
    }

    public AdoptionRequestCreatePage fillApplicantDocument(String value) {
        type(APPLICANT_DOCUMENT, value);
        return this;
    }

    public AdoptionRequestCreatePage selectHousingType(String value) {
        selectByValue(HOUSING_TYPE, value);
        return this;
    }

    public AdoptionRequestCreatePage setHasOtherPets(boolean checked) {
        setCheckbox(HAS_OTHER_PETS, checked);
        return this;
    }

    public AdoptionRequestCreatePage fillReason(String value) {
        type(REASON, value);
        return this;
    }

    public AdoptionRequestsListPage submitSuccess() {
        click(SUBMIT);

        wait.until(driver -> {
            try {
                return "Solicitações de adoção".equals(
                        driver.findElement(By.id("page-title")).getText()
                );
            } catch (Exception e) {
                return false;
            }
        });

        return new AdoptionRequestsListPage(driver);
    }

    public void submit() {
        click(SUBMIT);
    }
}