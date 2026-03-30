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

public class AdoptionRequestApproveFlowTest extends BaseWebTest {

    @Test
    void shouldCompleteAdoptionFlowAndApproveRequest() {
        FlowUniqueData data = SeleniumFlowDataFactory.create("aprovar");

        AdoptionRequestsListPage requestsListPage = new AdoptionRequestFlow(driver, baseUrl)
                .createFullFlowUntilRequestCreation(
                        data.shelterName(),
                        data.shelterPhone(),
                        data.shelterEmail(),
                        "CACHORRO",
                        data.petName(),
                        "Vira-lata",
                        "2",
                        "Caramelo",
                        "10.5",
                        data.applicantName(),
                        data.applicantEmail(),
                        data.applicantPhone(),
                        data.applicantDocument(),
                        "HOUSE",
                        true,
                        "Quero adotar com responsabilidade."
                );

        assertTrue(requestsListPage.containsApplicantName(data.applicantName()));

        requestsListPage = requestsListPage.approveRequestOfApplicantNamed(data.applicantName());

        AdoptionRequestDetailsPage detailsPage = requestsListPage.detailsOfApplicantNamed(data.applicantName());

        assertEquals("APROVADA", detailsPage.status());
        assertEquals(data.applicantName(), detailsPage.applicantName());
    }
}