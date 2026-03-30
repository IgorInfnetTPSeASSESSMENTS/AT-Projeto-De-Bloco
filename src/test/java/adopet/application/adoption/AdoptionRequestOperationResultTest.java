package adopet.application.adoption;

import adopet.domain.adoption.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdoptionRequestOperationResultTest {

    @Test
    void shouldExposeAllRecordFields() {
        AdoptionRequest request = AdoptionRequest.newRequest(
                1L,
                10L,
                new ApplicantName("Maria"),
                new ApplicantEmail("maria@email.com"),
                new ApplicantPhone("31999999999"),
                new ApplicantDocument("12345678900"),
                HousingType.HOUSE,
                true,
                new ReasonText("Quero adotar com responsabilidade e carinho."),
                EligibilityAnalysis.ELIGIBLE
        );

        AdoptionRequestOperationResult result = new AdoptionRequestOperationResult(
                request,
                true,
                AnalysisExecutionStatus.SUCCESS,
                EligibilityAnalysis.ELIGIBLE
        );

        assertEquals(request, result.adoptionRequest());
        assertTrue(result.notificationSent());
        assertEquals(AnalysisExecutionStatus.SUCCESS, result.analysisExecutionStatus());
        assertEquals(EligibilityAnalysis.ELIGIBLE, result.analysisResult());
    }
}