package adopet.application.adoption;

import adopet.domain.adoption.AdoptionRequest;
import adopet.domain.adoption.EligibilityAnalysis;

public record AdoptionRequestOperationResult(
        AdoptionRequest adoptionRequest,
        boolean notificationSent,
        AnalysisExecutionStatus analysisExecutionStatus,
        EligibilityAnalysis analysisResult
) {
}