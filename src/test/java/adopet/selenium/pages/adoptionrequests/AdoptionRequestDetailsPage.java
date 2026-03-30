package adopet.selenium.pages.adoptionrequests;

import adopet.selenium.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AdoptionRequestDetailsPage extends BasePage {

    private static final By PAGE_TITLE = By.id("page-title");

    private static final By REQUEST_PET_ID = By.id("request-petId");
    private static final By REQUEST_SHELTER_ID = By.id("request-shelterId");
    private static final By REQUEST_APPLICANT_NAME = By.id("request-applicantName");
    private static final By REQUEST_APPLICANT_EMAIL = By.id("request-applicantEmail");
    private static final By REQUEST_APPLICANT_PHONE = By.id("request-applicantPhone");
    private static final By REQUEST_APPLICANT_DOCUMENT = By.id("request-applicantDocument");
    private static final By REQUEST_HOUSING_TYPE = By.id("request-housingType");
    private static final By REQUEST_HAS_OTHER_PETS = By.id("request-hasOtherPets");
    private static final By REQUEST_REASON = By.id("request-reason");
    private static final By REQUEST_STATUS = By.id("request-status");
    private static final By RETRY_ANALYSIS = By.id("retry-analysis");
    private static final By LINK_BACK = By.id("link-back-to-list");

    public AdoptionRequestDetailsPage(WebDriver driver) {
        super(driver);
        ensureLoaded();
    }

    public void ensureLoaded() {
        ensureTitle(PAGE_TITLE, "Detalhes da solicitação", "Adoption request details page");
    }

    public String petId() {
        return textOf(REQUEST_PET_ID);
    }

    public String applicantName() {
        return textOf(REQUEST_APPLICANT_NAME);
    }

    public String applicantEmail() {
        return textOf(REQUEST_APPLICANT_EMAIL);
    }

    public String applicantPhone() {
        return textOf(REQUEST_APPLICANT_PHONE);
    }

    public String applicantDocument() {
        return textOf(REQUEST_APPLICANT_DOCUMENT);
    }

    public String housingType() {
        return textOf(REQUEST_HOUSING_TYPE);
    }

    public String hasOtherPets() {
        return textOf(REQUEST_HAS_OTHER_PETS);
    }

    public String reason() {
        return textOf(REQUEST_REASON);
    }

    public String status() {
        return textOf(REQUEST_STATUS);
    }

    public AdoptionRequestDetailsPage retryAnalysis() {
        click(RETRY_ANALYSIS);
        return new AdoptionRequestDetailsPage(driver);
    }

    public AdoptionRequestsListPage backToList() {
        click(LINK_BACK);
        return new AdoptionRequestsListPage(driver);
    }

    public String analysis() {
        return textOf(By.id("request-analysis"));
    }

    public String pageSource() {
        return driver.getPageSource();
    }
}