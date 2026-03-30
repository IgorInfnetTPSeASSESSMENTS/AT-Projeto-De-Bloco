package adopet.selenium.tests.advanced;

import adopet.selenium.base.BaseWebTest;
import adopet.selenium.flows.AdoptionRequestFlow;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestCreatePage;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestsListPage;
import adopet.selenium.support.SeleniumFlowDataFactory;
import adopet.selenium.support.SeleniumFlowDataFactory.FlowUniqueData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdoptionRequestMaliciousInputTest extends BaseWebTest {

    @ParameterizedTest
    @CsvSource({
            "'<script>alert(1)</script>'",
            "' OR ''1''=''1'",
            "'../../etc/passwd'",
            "'DROP TABLE adoption_request;'",
            "'AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'"
    })
    void shouldHandleMaliciousApplicantNameGracefully(String maliciousName) {
        FlowUniqueData context = SeleniumFlowDataFactory.create("malicious");

        AdoptionRequestCreatePage formPage = createContextOnly(context).goToCreate();

        formPage
                .fillApplicantName(maliciousName)
                .fillApplicantEmail(context.applicantEmail())
                .fillApplicantPhone(context.applicantPhone())
                .fillApplicantDocument(context.applicantDocument())
                .selectHousingType("HOUSE")
                .setHasOtherPets(true)
                .fillReason("Quero adotar com responsabilidade e carinho pelo animal.")
                .submit();

        String pageSource = driver.getPageSource();
        String currentTitle = driver.getTitle();

        assertTrue(pageSource != null && !pageSource.isBlank(), "A página não deveria ficar vazia.");
        assertFalse(pageSource.toLowerCase().contains("whitelabel"), pageSource);
        assertFalse(pageSource.toLowerCase().contains("exception"), pageSource);
        assertFalse(pageSource.toLowerCase().contains("stack trace"), pageSource);
        assertFalse(currentTitle.toLowerCase().contains("error"), currentTitle);

        boolean stayedOnCreatePage = pageSource.contains("Nova solicitação de adoção");
        boolean returnedToListPage = pageSource.contains("Solicitações de adoção");

        assertTrue(
                stayedOnCreatePage || returnedToListPage,
                pageSource
        );
    }

    private AdoptionRequestsListPage createContextOnly(FlowUniqueData data) {
        return new AdoptionRequestFlow(driver, baseUrl).createContextWithoutRequest(
                data.shelterName(),
                data.shelterPhone(),
                data.shelterEmail(),
                "CACHORRO",
                data.petName(),
                "Vira-lata",
                "2",
                "Caramelo",
                "10.5"
        );
    }
}