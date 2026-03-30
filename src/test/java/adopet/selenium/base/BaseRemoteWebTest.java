package adopet.selenium.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

public abstract class BaseRemoteWebTest {

    protected WebDriver driver;
    protected String baseUrl;

    @BeforeEach
    void setUpDriver() {
        driver = DriverFactory.createChrome();

        String configuredBaseUrl = System.getProperty("app.baseUrl");
        if (configuredBaseUrl == null || configuredBaseUrl.isBlank()) {
            configuredBaseUrl = System.getenv("APP_BASE_URL");
        }

        if (configuredBaseUrl == null || configuredBaseUrl.isBlank()) {
            throw new IllegalStateException(
                    "Post-deploy tests require 'app.baseUrl' or environment variable 'APP_BASE_URL'."
            );
        }

        baseUrl = configuredBaseUrl;
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}