package adopet.selenium.tests.advanced;

import adopet.selenium.base.BaseWebTest;
import adopet.selenium.flows.AdoptionRequestFlow;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestDetailsPage;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestsListPage;
import adopet.selenium.support.SeleniumFlowDataFactory;
import adopet.selenium.support.SeleniumFlowDataFactory.FlowUniqueData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdoptionRequestSimulationTest extends BaseWebTest {

    @Test
    void shouldHandleEligibilityTimeoutGracefully() {
        FlowUniqueData context = SeleniumFlowDataFactory.create("eligibility-timeout");
        String applicantName = "CandTimeout";
        String applicantEmail = "timeout@test.com";

        AdoptionRequestsListPage listPage = createContextOnly(context)
                .simulateEligibilityTimeout();

        assertTrue(listPage.analysisMode().contains("TIMEOUT"), listPage.pageSource());

        listPage = listPage
                .goToCreate()
                .fillApplicantName(applicantName)
                .fillApplicantEmail(applicantEmail)
                .fillApplicantPhone("21999999999")
                .fillApplicantDocument("12345678900")
                .selectHousingType("HOUSE")
                .setHasOtherPets(true)
                .fillReason("Quero adotar com responsabilidade e carinho.")
                .submitSuccess();

        assertTrue(
                listPage.successMessage().contains("análise automática falhou")
                        || listPage.successMessage().contains("UNAVAILABLE"),
                listPage.pageSource()
        );

        AdoptionRequestDetailsPage detailsPage = listPage.detailsOfApplicantNamed(applicantName);
        assertTrue(detailsPage.analysis().contains("INDISPONÍVEL"), detailsPage.pageSource());
    }

    @Test
    void shouldHandleNotificationTimeoutGracefully() {
        FlowUniqueData context = SeleniumFlowDataFactory.create("notification-timeout");
        String applicantName = "CandNotif";
        String applicantEmail = "notif@test.com";

        AdoptionRequestsListPage listPage = createContextOnly(context)
                .simulateNotificationTimeout();

        assertTrue(listPage.notificationMode().contains("TIMEOUT"), listPage.pageSource());

        listPage = listPage
                .goToCreate()
                .fillApplicantName(applicantName)
                .fillApplicantEmail(applicantEmail)
                .fillApplicantPhone("21999999999")
                .fillApplicantDocument("12345678900")
                .selectHousingType("HOUSE")
                .setHasOtherPets(true)
                .fillReason("Quero adotar com responsabilidade e carinho.")
                .submitSuccess();

        assertTrue(
                listPage.successMessage().contains("notificação não pôde ser enviada")
                        || listPage.successMessage().contains("Notificação"),
                listPage.pageSource()
        );
    }

    @Test
    void shouldRetryAnalysisSuccessfully() {
        FlowUniqueData context = SeleniumFlowDataFactory.create("retry-analysis");
        String applicantName = "CandRetry";
        String applicantEmail = "retry@test.com";

        AdoptionRequestsListPage listPage = createContextOnly(context)
                .simulateEligibilityTimeout();

        listPage = listPage
                .goToCreate()
                .fillApplicantName(applicantName)
                .fillApplicantEmail(applicantEmail)
                .fillApplicantPhone("21999999999")
                .fillApplicantDocument("12345678900")
                .selectHousingType("HOUSE")
                .setHasOtherPets(true)
                .fillReason("Quero adotar com responsabilidade e carinho.")
                .submitSuccess();

        AdoptionRequestDetailsPage detailsPage = listPage.detailsOfApplicantNamed(applicantName);
        assertTrue(detailsPage.analysis().contains("INDISPONÍVEL"), detailsPage.pageSource());

        listPage = detailsPage.backToList().simulateEligibilitySuccess();
        detailsPage = listPage.detailsOfApplicantNamed(applicantName).retryAnalysis();

        assertTrue(
                detailsPage.analysis().contains("ELEGÍVEL")
                        || detailsPage.analysis().contains("REQUER ANÁLISE MANUAL"),
                detailsPage.pageSource()
        );

        assertTrue(
                detailsPage.status().contains("EM ANÁLISE")
                        || detailsPage.status().contains("PENDENTE"),
                detailsPage.pageSource()
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