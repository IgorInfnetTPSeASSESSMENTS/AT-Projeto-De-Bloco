package adopet.application.adoption;

import adopet.domain.adoption.AdoptionRequest;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetStatus;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.AdoptionRequestGateway;
import adopet.gateway.NotificationGateway;
import adopet.gateway.PetGateway;

public class ApproveAdoptionRequestCommandHandler {

  private final AdoptionRequestGateway adoptionRequestGateway;
  private final NotificationGateway notificationGateway;
  private final PetGateway petGateway;

  public ApproveAdoptionRequestCommandHandler(
      AdoptionRequestGateway adoptionRequestGateway,
      NotificationGateway notificationGateway,
      PetGateway petGateway) {
    this.adoptionRequestGateway = adoptionRequestGateway;
    this.notificationGateway = notificationGateway;
    this.petGateway = petGateway;
  }

  public AdoptionRequestOperationResult execute(Long id) {
    if (id == null) {
      throw new InvalidUserInputException("Id da solicitação é obrigatório.");
    }

    AdoptionRequest existing =
        adoptionRequestGateway
            .findById(id)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Solicitação de adoção não encontrada (id=" + id + ")."));

    Pet pet =
        petGateway
            .findById(existing.petId())
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Pet não encontrado (id=" + existing.petId() + ")."));

    if (pet.status() == PetStatus.ADOPTED) {
      throw new InvalidUserInputException("Este pet já está adotado.");
    }

    AdoptionRequest approved = adoptionRequestGateway.updateAdoptionRequest(id, existing.approve());
    petGateway.updatePetStatus(approved.petId(), PetStatus.ADOPTED);

    boolean notificationSent = true;
    try {
      notificationGateway.notifyApproval(approved.id(), approved.applicantEmail().value());
    } catch (RuntimeException ignored) {
      notificationSent = false;
    }

    return new AdoptionRequestOperationResult(
        approved,
        notificationSent,
        AnalysisExecutionStatus.SUCCESS,
        approved.eligibilityAnalysis());
  }
}
