package adopet.selenium.pages.shelters;

import adopet.selenium.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class ShelterCreatePage extends BasePage {

    private static final By PAGE_TITLE = By.id("page-title");
    private static final By NAME = By.id("name");
    private static final By PHONE_NUMBER = By.id("phoneNumber");
    private static final By EMAIL = By.id("email");
    private static final By SUBMIT = By.id("submit-shelter");
    private static final By ERROR_MESSAGE = By.id("error-message");

    private static final By NAME_FIELD_ERROR =
            By.xpath("//input[@id='name']/following-sibling::div");
    private static final By PHONE_FIELD_ERROR =
            By.xpath("//input[@id='phoneNumber']/following-sibling::div");
    private static final By EMAIL_FIELD_ERROR =
            By.xpath("//input[@id='email']/following-sibling::div");

    public ShelterCreatePage(WebDriver driver) {
        super(driver);
        ensureLoaded();
    }

    public void ensureLoaded() {
        ensureTitle(PAGE_TITLE, "Cadastrar abrigo", "Shelter create page");
    }

    public ShelterCreatePage fillName(String value) {
        type(NAME, value);
        return this;
    }

    public ShelterCreatePage fillPhoneNumber(String value) {
        type(PHONE_NUMBER, value);
        return this;
    }

    public ShelterCreatePage fillEmail(String value) {
        type(EMAIL, value);
        return this;
    }

    public SheltersListPage submitSuccess() {
        click(SUBMIT);

        wait.until(driver -> {
            try {
                return "Abrigos".equals(
                        driver.findElement(By.id("page-title")).getText()
                );
            } catch (Exception e) {
                return false;
            }
        });

        return new SheltersListPage(driver);
    }

    public String currentPageTitle() {
        return textOf(PAGE_TITLE);
    }

    public boolean hasErrorMessage() {
        return exists(ERROR_MESSAGE) && isVisible(ERROR_MESSAGE);
    }


    public String errorMessage() {
        return textOf(ERROR_MESSAGE);
    }

    public boolean hasNameFieldError() {
        return exists(NAME_FIELD_ERROR) && isVisible(NAME_FIELD_ERROR);
    }

    public boolean hasPhoneFieldError() {
        return exists(PHONE_FIELD_ERROR) && isVisible(PHONE_FIELD_ERROR);
    }

    public boolean hasEmailFieldError() {
        return exists(EMAIL_FIELD_ERROR) && isVisible(EMAIL_FIELD_ERROR);
    }

    public String nameFieldError() {
        return textOf(NAME_FIELD_ERROR);
    }

    public String phoneFieldError() {
        return textOf(PHONE_FIELD_ERROR);
    }

    public String emailFieldError() {
        return textOf(EMAIL_FIELD_ERROR);
    }

    public String debugValidationErrors() {
        StringBuilder sb = new StringBuilder();

        if (hasErrorMessage()) {
            sb.append("globalError=").append(errorMessage());
        }
        if (hasNameFieldError()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append("nameError=").append(nameFieldError());
        }
        if (hasPhoneFieldError()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append("phoneError=").append(phoneFieldError());
        }
        if (hasEmailFieldError()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append("emailError=").append(emailFieldError());
        }

        return sb.toString();
    }
}