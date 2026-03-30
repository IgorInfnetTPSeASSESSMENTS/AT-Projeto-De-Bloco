package adopet.selenium.tests.postdeploy;

import adopet.selenium.base.BaseRemoteWebTest;
import adopet.selenium.pages.HomePage;
import adopet.selenium.pages.shelters.SheltersListPage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("postdeploy")
public class PostDeploySmokeIT extends BaseRemoteWebTest {

    @Test
    void shouldLoadHomePageAndNavigateToShelters() {
        HomePage homePage = new HomePage(driver).open(baseUrl);

        assertEquals("ADOPET", driver.getTitle());
        assertTrue(driver.getPageSource().contains("ADOPET"));

        SheltersListPage sheltersListPage = homePage.goToShelters();

        assertTrue(sheltersListPage.pageSource().contains("Abrigos"));
    }
}