package adopet.application.adoption;

import adopet.domain.adoption.AdoptionRequest;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.AdoptionRequestGateway;
import adopet.gateway.NotificationGateway;

public class CancelAdoptionRequestCommandHandler {

    private final AdoptionRequestGateway adoptionRequestGateway;
    private final NotificationGateway notificationGateway;

    public CancelAdoptionRequestCommandHandler(
            AdoptionRequestGateway adoptionRequestGateway,
            NotificationGateway notificationGateway
    ) {
        this.adoptionRequestGateway = adoptionRequestGateway;
        this.notificationGateway = notificationGateway;
    }

    public AdoptionRequestOperationResult execute(Long id) {
        if (id == null) {
            throw new InvalidUserInputException("Id da solicitação é obrigatório.");
        }

        AdoptionRequest existing = adoptionRequestGateway.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de adoção não encontrada (id=" + id + ")."));

        AdoptionRequest cancelled = adoptionRequestGateway.updateAdoptionRequest(id, existing.cancel());

        boolean notificationSent = true;
        try {
            notificationGateway.notifyCancellation(cancelled.id(), cancelled.applicantEmail().value());
        } catch (RuntimeException ignored) {
            notificationSent = false;
        }

        return new AdoptionRequestOperationResult(
                cancelled,
                notificationSent,
                AnalysisExecutionStatus.SUCCESS,
                cancelled.eligibilityAnalysis()
        );
    }
}