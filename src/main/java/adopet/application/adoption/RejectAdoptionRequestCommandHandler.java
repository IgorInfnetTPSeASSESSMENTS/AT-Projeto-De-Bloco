package adopet.application.adoption;

import adopet.domain.adoption.AdoptionRequest;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.AdoptionRequestGateway;
import adopet.gateway.NotificationGateway;

public class RejectAdoptionRequestCommandHandler {

    private final AdoptionRequestGateway adoptionRequestGateway;
    private final NotificationGateway notificationGateway;

    public RejectAdoptionRequestCommandHandler(
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

        AdoptionRequest rejected = adoptionRequestGateway.updateAdoptionRequest(id, existing.reject());

        boolean notificationSent = true;
        try {
            notificationGateway.notifyRejection(rejected.id(), rejected.applicantEmail().value());
        } catch (RuntimeException ignored) {
            notificationSent = false;
        }

        return new AdoptionRequestOperationResult(
                rejected,
                notificationSent,
                AnalysisExecutionStatus.SUCCESS,
                rejected.eligibilityAnalysis()
        );
    }
}