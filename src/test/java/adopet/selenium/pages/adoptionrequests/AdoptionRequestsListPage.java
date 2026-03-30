package adopet.selenium.pages.adoptionrequests;

import adopet.selenium.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AdoptionRequestsListPage extends BasePage {

    private static final By PAGE_TITLE = By.id("page-title");
    private static final By REQUESTS_TABLE = By.id("adoption-requests-table");

    private static final By LINK_CREATE_CONTEXT = By.id("link-create-adoption-request-context");
    private static final By LINK_CREATE_GLOBAL = By.id("link-create-adoption-request-global");
    private static final By SUCCESS_MESSAGE = By.id("success-message");
    private static final By ANALYSIS_MODE = By.id("analysis-mode");
    private static final By NOTIFICATION_MODE = By.id("notification-mode");
    private static final By ELIGIBILITY_SUCCESS = By.id("eligibility-success");
    private static final By ELIGIBILITY_TIMEOUT = By.id("eligibility-timeout");
    private static final By NOTIFICATION_TIMEOUT = By.id("notification-timeout");

    public AdoptionRequestsListPage(WebDriver driver) {
        super(driver);
        ensureLoaded();
    }

    public void ensureLoaded() {
        ensureTitle(PAGE_TITLE, "Solicitações de adoção", "Adoption requests list page");
        visible(REQUESTS_TABLE);
    }

    public AdoptionRequestCreatePage goToCreate() {
        if (exists(LINK_CREATE_CONTEXT)) {
            click(LINK_CREATE_CONTEXT);
        } else {
            click(LINK_CREATE_GLOBAL);
        }
        return new AdoptionRequestCreatePage(driver);
    }

    public AdoptionRequestDetailsPage detailsOfApplicantNamed(String applicantName) {
        click(linkInsideRowContainingText("adoption-requests-table", applicantName, "details-adoption-request-"));
        return new AdoptionRequestDetailsPage(driver);
    }

    public AdoptionRequestEditPage editRequestOfApplicantNamed(String applicantName) {
        click(linkInsideRowContainingText("adoption-requests-table", applicantName, "edit-adoption-request-"));
        return new AdoptionRequestEditPage(driver);
    }

    public AdoptionRequestsListPage approveRequestOfApplicantNamed(String applicantName) {
        click(buttonInsideRowContainingText("adoption-requests-table", applicantName, "approve-adoption-request-"));
        return new AdoptionRequestsListPage(driver);
    }

    public AdoptionRequestsListPage rejectRequestOfApplicantNamed(String applicantName) {
        click(buttonInsideRowContainingText("adoption-requests-table", applicantName, "reject-adoption-request-"));
        return new AdoptionRequestsListPage(driver);
    }

    public AdoptionRequestsListPage deleteRequestOfApplicantNamed(String applicantName) {
        click(buttonInsideRowContainingText("adoption-requests-table", applicantName, "delete-adoption-request-"));
        acceptAlertWhenPresent();
        return new AdoptionRequestsListPage(driver);
    }

    public String successMessage() {
        return textOf(SUCCESS_MESSAGE);
    }

    public boolean containsApplicantName(String applicantName) {
        return exists(rowContainingText("adoption-requests-table", applicantName));
    }


    public String analysisMode() {
        return textOf(ANALYSIS_MODE);
    }

    public String notificationMode() {
        return textOf(NOTIFICATION_MODE);
    }

    public AdoptionRequestsListPage simulateEligibilitySuccess() {
        click(ELIGIBILITY_SUCCESS);
        return new AdoptionRequestsListPage(driver);
    }

    public AdoptionRequestsListPage simulateEligibilityTimeout() {
        click(ELIGIBILITY_TIMEOUT);

        wait.until(driver -> {
            try {
                String mode = driver.findElement(By.id("analysis-mode")).getText();
                return driver.findElement(By.id("adoption-requests-table")).isDisplayed()
                        && mode.contains("TIMEOUT");
            } catch (Exception e) {
                return false;
            }
        });

        return new AdoptionRequestsListPage(driver);
    }


    public AdoptionRequestsListPage simulateNotificationTimeout() {
        click(NOTIFICATION_TIMEOUT);
        return new AdoptionRequestsListPage(driver);
    }

    public String pageSource() {
        return driver.getPageSource();
    }

}