package adopet.selenium.tests.postdeploy;

import adopet.selenium.base.BaseRemoteWebTest;
import adopet.selenium.flows.AdoptionRequestFlow;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestDetailsPage;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestsListPage;
import adopet.selenium.support.SeleniumFlowDataFactory;
import adopet.selenium.support.SeleniumFlowDataFactory.FlowUniqueData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("postdeploy")
public class PostDeployFunctionalIT extends BaseRemoteWebTest {

    @Test
    void shouldCreateAndApproveAdoptionRequestInPublishedEnvironment() {
        FlowUniqueData data = SeleniumFlowDataFactory.create("postdeploy-functional");

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
                        "Fluxo pós-deploy automatizado para validar a publicação."
                );

        assertTrue(requestsListPage.containsApplicantName(data.applicantName()));

        requestsListPage = requestsListPage.approveRequestOfApplicantNamed(data.applicantName());
        AdoptionRequestDetailsPage detailsPage = requestsListPage.detailsOfApplicantNamed(data.applicantName());

        assertEquals(data.applicantName(), detailsPage.applicantName());
        assertEquals("APROVADA", detailsPage.status());
    }
}
