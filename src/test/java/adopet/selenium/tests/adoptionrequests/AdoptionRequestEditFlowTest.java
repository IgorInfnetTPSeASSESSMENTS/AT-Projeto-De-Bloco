package adopet.selenium.tests.adoptionrequests;

import adopet.selenium.base.BaseWebTest;
import adopet.selenium.flows.AdoptionRequestFlow;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestDetailsPage;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestEditPage;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestsListPage;
import adopet.selenium.support.SeleniumFlowDataFactory;
import adopet.selenium.support.SeleniumFlowDataFactory.FlowUniqueData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdoptionRequestEditFlowTest extends BaseWebTest {

    @Test
    void shouldCompleteAdoptionFlowAndEditRequest() {
        FlowUniqueData original = SeleniumFlowDataFactory.create("editar-original");
        FlowUniqueData edited = SeleniumFlowDataFactory.create("editar-editado");

        AdoptionRequestsListPage requestsListPage = new AdoptionRequestFlow(driver, baseUrl)
                .createFullFlowUntilRequestCreation(
                        original.shelterName(),
                        original.shelterPhone(),
                        original.shelterEmail(),
                        "CACHORRO",
                        original.petName(),
                        "Labrador",
                        "4",
                        "Dourado",
                        "20.0",
                        original.applicantName(),
                        original.applicantEmail(),
                        original.applicantPhone(),
                        original.applicantDocument(),
                        "HOUSE",
                        true,
                        "Motivo original."
                );

        assertTrue(requestsListPage.containsApplicantName(original.applicantName()));

        AdoptionRequestEditPage editPage = requestsListPage.editRequestOfApplicantNamed(original.applicantName());

        requestsListPage = editPage
                .fillApplicantName(edited.applicantName())
                .fillApplicantEmail(edited.applicantEmail())
                .fillApplicantPhone(edited.applicantPhone())
                .fillApplicantDocument(edited.applicantDocument())
                .selectHousingType("FARM")
                .setHasOtherPets(false)
                .fillReason("Motivo editado.")
                .submitSuccess();

        AdoptionRequestDetailsPage detailsPage = requestsListPage.detailsOfApplicantNamed(edited.applicantName());

        assertEquals(edited.applicantName(), detailsPage.applicantName());
        assertEquals(edited.applicantEmail(), detailsPage.applicantEmail());
        assertEquals(edited.applicantPhone(), detailsPage.applicantPhone());
        assertEquals(edited.applicantDocument(), detailsPage.applicantDocument());
        assertEquals("SÍTIO / CHÁCARA", detailsPage.housingType());
        assertEquals("Não", detailsPage.hasOtherPets());
        assertEquals("Motivo editado.", detailsPage.reason());
    }
}