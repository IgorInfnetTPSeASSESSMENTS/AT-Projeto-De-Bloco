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

public class AdoptionRequestValidationTest extends BaseWebTest {

    @ParameterizedTest
    @CsvSource({
            "'',maria@test.com,21999999999,12345678900,HOUSE,true,Quero adotar com responsabilidade.",
            "'Maria',email-invalido,21999999999,12345678900,HOUSE,true,Quero adotar com responsabilidade.",
            "'Maria',maria@test.com,'',12345678900,HOUSE,true,Quero adotar com responsabilidade.",
            "'Maria',maria@test.com,21999999999,'',HOUSE,true,Quero adotar com responsabilidade.",
            "'Maria',maria@test.com,21999999999,12345678900,'',true,Quero adotar com responsabilidade.",
            "'Maria',maria@test.com,21999999999,12345678900,HOUSE,true,'curto'"
    })
    void shouldValidateAdoptionRequestCreationFields(
            String applicantName,
            String applicantEmail,
            String applicantPhone,
            String applicantDocument,
            String housingType,
            boolean hasOtherPets,
            String reason
    ) {
        FlowUniqueData context = SeleniumFlowDataFactory.create("validation");

        AdoptionRequestsListPage listPage = createContextOnly(context);
        AdoptionRequestCreatePage formPage = listPage.goToCreate();

        formPage
                .fillApplicantName(applicantName)
                .fillApplicantEmail(applicantEmail)
                .fillApplicantPhone(applicantPhone)
                .fillApplicantDocument(applicantDocument)
                .selectHousingType(housingType)
                .setHasOtherPets(hasOtherPets)
                .fillReason(reason)
                .submit();

        String pageSource = driver.getPageSource();

        assertTrue(
                pageSource.contains("Nova solicitação de adoção")
                        || pageSource.contains("adoption-request-form"),
                pageSource
        );

        assertFalse(
                pageSource.contains("Solicitação de adoção cadastrada com sucesso"),
                pageSource
        );

        assertFalse(
                pageSource.contains(context.applicantName()),
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