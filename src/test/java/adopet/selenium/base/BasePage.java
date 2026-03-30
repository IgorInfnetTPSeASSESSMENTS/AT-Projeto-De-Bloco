package adopet.selenium.base;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    protected WebElement visible(By locator) {
        return wait.until(driver -> {
            try {
                WebElement element = driver.findElement(locator);
                return element.isDisplayed() ? element : null;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });
    }

    protected WebElement clickable(By locator) {
        return wait.until(driver -> {
            try {
                WebElement element = driver.findElement(locator);
                return (element.isDisplayed() && element.isEnabled()) ? element : null;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });
    }

    protected List<WebElement> all(By locator) {
        return driver.findElements(locator);
    }

    protected void click(By locator) {
        clickable(locator).click();
    }

    protected void type(By locator, String value) {
        WebElement element = visible(locator);
        element.clear();
        if (value != null) {
            element.sendKeys(value);
        }
    }

    protected void selectByValue(By locator, String value) {
        new Select(visible(locator)).selectByValue(value);
    }

    protected String textOf(By locator) {
        return wait.until(driver -> {
            try {
                return driver.findElement(locator).getText();
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });
    }

    protected String valueOf(By locator) {
        return wait.until(driver -> {
            try {
                return driver.findElement(locator).getDomProperty("value");
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });
    }

    protected boolean isVisible(By locator) {
        try {
            visible(locator);
            return true;
        } catch (TimeoutException exception) {
            return false;
        }
    }

    protected boolean exists(By locator) {
        return !all(locator).isEmpty();
    }

    protected void setCheckbox(By locator, boolean checked) {
        WebElement checkbox = clickable(locator);
        if (checkbox.isSelected() != checked) {
            checkbox.click();
        }
    }

    protected Alert waitForAlert() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    protected void acceptAlertWhenPresent() {
        waitForAlert().accept();
    }

    protected void ensureTitle(By titleLocator, String expectedTitle, String pageName) {
        String actual = textOf(titleLocator);
        if (!expectedTitle.equals(actual)) {
            throw new IllegalStateException(
                    pageName + " was not loaded. Expected title '" + expectedTitle + "', but found '" + actual + "'."
            );
        }
    }

    protected void ensureTitleStartsWith(By titleLocator, String expectedPrefix, String pageName) {
        String actual = textOf(titleLocator);
        if (!actual.startsWith(expectedPrefix)) {
            throw new IllegalStateException(
                    pageName + " was not loaded. Expected title starting with '" + expectedPrefix + "', but found '" + actual + "'."
            );
        }
    }

    protected By rowContainingText(String tableId, String text) {
        return By.xpath("//table[@id='" + tableId + "']//tr[td[normalize-space()='" + text + "']]");
    }

    protected By linkInsideRowContainingText(String tableId, String text, String linkIdPrefix) {
        return By.xpath("//table[@id='" + tableId + "']//tr[td[normalize-space()='" + text + "']]//a[starts-with(@id,'" + linkIdPrefix + "')]");
    }

    protected By buttonInsideRowContainingText(String tableId, String text, String buttonIdPrefix) {
        return By.xpath("//table[@id='" + tableId + "']//tr[td[normalize-space()='" + text + "']]//button[starts-with(@id,'" + buttonIdPrefix + "')]");
    }

    protected long extractTrailingId(String value, String prefix) {
        if (!value.startsWith(prefix)) {
            throw new IllegalArgumentException("Value '" + value + "' does not start with prefix '" + prefix + "'.");
        }
        return Long.parseLong(value.substring(prefix.length()));
    }

    protected long extractIdFromElementId(By locator, String prefix) {
        String id = visible(locator).getDomAttribute("id");
        return extractTrailingId(id, prefix);
    }
}