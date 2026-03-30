package adopet.infrastructure.memory;

import adopet.domain.adoption.EligibilityAnalysis;
import adopet.exception.ExternalServiceException;
import adopet.gateway.EligibilityAnalysisGateway;

public class ProgrammableEligibilityAnalysisGateway implements EligibilityAnalysisGateway {

    private volatile ScenarioMode scenarioMode = ScenarioMode.SUCCESS;
    private volatile EligibilityAnalysis successResult = EligibilityAnalysis.REQUIRES_MANUAL_REVIEW;

    public void setScenarioMode(ScenarioMode scenarioMode) {
        this.scenarioMode = scenarioMode;
    }

    public ScenarioMode getScenarioMode() {
        return scenarioMode;
    }

    public void setSuccessResult(EligibilityAnalysis successResult) {
        this.successResult = successResult;
    }

    @Override
    public EligibilityAnalysis analyze(Long petId, Long shelterId, String applicantDocument, boolean hasOtherPets, String reason) {
        return switch (scenarioMode) {
            case SUCCESS -> successResult;
            case TIMEOUT -> throw new ExternalServiceException("Timeout na análise de elegibilidade.");
            case NETWORK_ERROR -> throw new ExternalServiceException("Erro de rede na análise de elegibilidade.");
            case SERVICE_UNAVAILABLE -> throw new ExternalServiceException("Serviço de análise indisponível.");
            case INVALID_RESPONSE -> throw new ExternalServiceException("Resposta inválida do serviço de análise.");
            case UNEXPECTED_FAILURE -> throw new RuntimeException("Falha inesperada na análise automática.");
        };
    }
}