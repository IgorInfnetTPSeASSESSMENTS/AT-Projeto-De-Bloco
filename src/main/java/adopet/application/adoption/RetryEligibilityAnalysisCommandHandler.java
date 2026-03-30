package adopet.application.adoption;

import adopet.domain.adoption.AdoptionRequest;
import adopet.domain.adoption.EligibilityAnalysis;
import adopet.domain.shelterandpet.PetStatus;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.AdoptionRequestGateway;
import adopet.gateway.EligibilityAnalysisGateway;
import adopet.gateway.PetGateway;

public class RetryEligibilityAnalysisCommandHandler {

    private final AdoptionRequestGateway adoptionRequestGateway;
    private final EligibilityAnalysisGateway eligibilityAnalysisGateway;
    private final PetGateway petGateway;

    public RetryEligibilityAnalysisCommandHandler(
            AdoptionRequestGateway adoptionRequestGateway,
            EligibilityAnalysisGateway eligibilityAnalysisGateway,
            PetGateway petGateway
    ) {
        this.adoptionRequestGateway = adoptionRequestGateway;
        this.eligibilityAnalysisGateway = eligibilityAnalysisGateway;
        this.petGateway = petGateway;
    }

    public AdoptionRequest execute(Long id) {
        if (id == null) {
            throw new InvalidUserInputException("Id da solicitação é obrigatório.");
        }

        AdoptionRequest existing = adoptionRequestGateway.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de adoção não encontrada (id=" + id + ")."));

        petGateway.findById(existing.petId())
                .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado (id=" + existing.petId() + ")."));

        EligibilityAnalysis newAnalysis;
        try {
            newAnalysis = eligibilityAnalysisGateway.analyze(
                    existing.petId(),
                    existing.shelterId(),
                    existing.applicantDocument().value(),
                    existing.hasOtherPets(),
                    existing.reason().value()
            );
        } catch (RuntimeException exception) {
            newAnalysis = EligibilityAnalysis.UNAVAILABLE;
        }

        AdoptionRequest updated = adoptionRequestGateway.updateAdoptionRequest(
                id,
                existing.withEligibilityAnalysis(newAnalysis)
        );

        petGateway.updatePetStatus(existing.petId(), PetStatus.AVAILABLE);

        return updated;
    }
}