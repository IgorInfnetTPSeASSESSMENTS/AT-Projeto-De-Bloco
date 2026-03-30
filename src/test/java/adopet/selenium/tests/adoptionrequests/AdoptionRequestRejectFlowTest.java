package adopet.selenium.tests.adoptionrequests;

import adopet.selenium.base.BaseWebTest;
import adopet.selenium.flows.AdoptionRequestFlow;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestDetailsPage;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestsListPage;
import adopet.selenium.support.SeleniumFlowDataFactory;
import adopet.selenium.support.SeleniumFlowDataFactory.FlowUniqueData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdoptionRequestRejectFlowTest extends BaseWebTest {

    @Test
    void shouldCompleteAdoptionFlowAndRejectRequest() {
        FlowUniqueData data = SeleniumFlowDataFactory.create("rejeitar");

        AdoptionRequestsListPage requestsListPage = new AdoptionRequestFlow(driver, baseUrl)
                .createFullFlowUntilRequestCreation(
                        data.shelterName(),
                        data.shelterPhone(),
                        data.shelterEmail(),
                        "CACHORRO",
                        data.petName(),
                        "Vira-lata",
                        "3",
                        "Preto",
                        "11.0",
                        data.applicantName(),
                        data.applicantEmail(),
                        data.applicantPhone(),
                        data.applicantDocument(),
                        "APARTMENT",
                        false,
                        "Tenho disponibilidade para cuidar."
                );

        assertTrue(requestsListPage.containsApplicantName(data.applicantName()));

        requestsListPage = requestsListPage.rejectRequestOfApplicantNamed(data.applicantName());

        AdoptionRequestDetailsPage detailsPage = requestsListPage.detailsOfApplicantNamed(data.applicantName());

        assertEquals("REJEITADA", detailsPage.status());
        assertEquals(data.applicantName(), detailsPage.applicantName());
    }
}