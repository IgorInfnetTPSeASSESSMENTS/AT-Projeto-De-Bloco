package adopet.infrastructure.memory;

import adopet.exception.ExternalServiceException;
import adopet.gateway.NotificationGateway;

import java.util.ArrayList;
import java.util.List;

public class ProgrammableNotificationGateway implements NotificationGateway {

    private volatile ScenarioMode scenarioMode = ScenarioMode.SUCCESS;
    private final List<String> sentEvents = new ArrayList<>();

    public void setScenarioMode(ScenarioMode scenarioMode) {
        this.scenarioMode = scenarioMode;
    }

    public ScenarioMode getScenarioMode() {
        return scenarioMode;
    }

    public List<String> sentEvents() {
        return List.copyOf(sentEvents);
    }

    private void maybeFail(String event) {
        switch (scenarioMode) {
            case SUCCESS -> sentEvents.add(event);
            case TIMEOUT -> throw new ExternalServiceException("Timeout no serviço de notificação.");
            case NETWORK_ERROR -> throw new ExternalServiceException("Erro de rede no serviço de notificação.");
            case SERVICE_UNAVAILABLE -> throw new ExternalServiceException("Serviço de notificação indisponível.");
            case INVALID_RESPONSE -> throw new ExternalServiceException("Resposta inválida do serviço de notificação.");
            case UNEXPECTED_FAILURE -> throw new RuntimeException("Falha inesperada no serviço de notificação.");
        }
    }

    @Override
    public void notifyCreation(Long requestId, String email) {
        maybeFail("CREATED:" + requestId + ":" + email);
    }

    @Override
    public void notifyApproval(Long requestId, String email) {
        maybeFail("APPROVED:" + requestId + ":" + email);
    }

    @Override
    public void notifyRejection(Long requestId, String email) {
        maybeFail("REJECTED:" + requestId + ":" + email);
    }

    @Override
    public void notifyCancellation(Long requestId, String email) {
        maybeFail("CANCELLED:" + requestId + ":" + email);
    }
}