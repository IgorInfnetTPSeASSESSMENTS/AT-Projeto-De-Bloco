package adopet.selenium.tests.adoptionrequests;

import adopet.selenium.base.BaseWebTest;
import adopet.selenium.flows.AdoptionRequestFlow;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestsListPage;
import adopet.selenium.support.SeleniumFlowDataFactory;
import adopet.selenium.support.SeleniumFlowDataFactory.FlowUniqueData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdoptionRequestDeleteFlowTest extends BaseWebTest {

    @Test
    void shouldCompleteAdoptionFlowAndDeleteRequest() {
        FlowUniqueData data = SeleniumFlowDataFactory.create("remover");

        AdoptionRequestsListPage requestsListPage = new AdoptionRequestFlow(driver, baseUrl)
                .createFullFlowUntilRequestCreation(
                        data.shelterName(),
                        data.shelterPhone(),
                        data.shelterEmail(),
                        "CACHORRO",
                        data.petName(),
                        "Pastor",
                        "5",
                        "Marrom",
                        "18.0",
                        data.applicantName(),
                        data.applicantEmail(),
                        data.applicantPhone(),
                        data.applicantDocument(),
                        "OTHER",
                        true,
                        "Solicitação para remoção."
                );

        assertTrue(requestsListPage.containsApplicantName(data.applicantName()));

        requestsListPage = requestsListPage.deleteRequestOfApplicantNamed(data.applicantName());

        assertFalse(requestsListPage.containsApplicantName(data.applicantName()));
    }
}