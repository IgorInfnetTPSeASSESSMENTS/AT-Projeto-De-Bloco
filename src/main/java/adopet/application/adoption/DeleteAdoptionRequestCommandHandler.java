package adopet.application.adoption;

import adopet.domain.adoption.AdoptionRequest;
import adopet.domain.adoption.AdoptionRequestStatus;
import adopet.domain.shelterandpet.PetStatus;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.AdoptionRequestGateway;
import adopet.gateway.PetGateway;

public class DeleteAdoptionRequestCommandHandler {

    private final AdoptionRequestGateway adoptionRequestGateway;
    private final PetGateway petGateway;

    public DeleteAdoptionRequestCommandHandler(
            AdoptionRequestGateway adoptionRequestGateway,
            PetGateway petGateway
    ) {
        this.adoptionRequestGateway = adoptionRequestGateway;
        this.petGateway = petGateway;
    }

    public void execute(Long id) {
        if (id == null) {
            throw new InvalidUserInputException("Id da solicitação é obrigatório.");
        }

        AdoptionRequest existing = adoptionRequestGateway.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de adoção não encontrada (id=" + id + ")."));

        if (existing.status() == AdoptionRequestStatus.APPROVED) {
            petGateway.findById(existing.petId())
                    .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado (id=" + existing.petId() + ")."));

            petGateway.updatePetStatus(existing.petId(), PetStatus.AVAILABLE);
        }

        adoptionRequestGateway.deleteAdoptionRequest(id);
    }
}