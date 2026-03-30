package adopet.gateway;

import adopet.domain.adoption.EligibilityAnalysis;

public interface EligibilityAnalysisGateway {
    EligibilityAnalysis analyze(Long petId, Long shelterId, String applicantDocument, boolean hasOtherPets, String reason);
}