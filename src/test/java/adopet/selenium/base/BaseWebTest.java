package adopet.selenium.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseWebTest {

    protected WebDriver driver;

    @LocalServerPort
    protected int port;

    protected String baseUrl;

    @BeforeEach
    void setUpDriver() {
        driver = DriverFactory.createChrome();
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}