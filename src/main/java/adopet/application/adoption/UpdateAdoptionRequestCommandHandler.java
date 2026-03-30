package adopet.application.adoption;

import adopet.domain.adoption.AdoptionRequest;
import adopet.domain.adoption.ApplicantDocument;
import adopet.domain.adoption.ApplicantEmail;
import adopet.domain.adoption.ApplicantName;
import adopet.domain.adoption.ApplicantPhone;
import adopet.domain.adoption.HousingType;
import adopet.domain.adoption.ReasonText;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.AdoptionRequestGateway;

public class UpdateAdoptionRequestCommandHandler {

    private final AdoptionRequestGateway adoptionRequestGateway;

    public UpdateAdoptionRequestCommandHandler(AdoptionRequestGateway adoptionRequestGateway) {
        this.adoptionRequestGateway = adoptionRequestGateway;
    }

    public AdoptionRequest execute(
            Long id,
            Long petId,
            Long shelterId,
            String applicantName,
            String applicantEmail,
            String applicantPhone,
            String applicantDocument,
            String housingType,
            boolean hasOtherPets,
            String reason
    ) {
        if (id == null) {
            throw new InvalidUserInputException("Id da solicitação é obrigatório.");
        }

        AdoptionRequest existing = adoptionRequestGateway.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de adoção não encontrada (id=" + id + ")."));

        AdoptionRequest updated = existing.updateDraft(
                petId,
                shelterId,
                new ApplicantName(applicantName),
                new ApplicantEmail(applicantEmail),
                new ApplicantPhone(applicantPhone),
                new ApplicantDocument(applicantDocument),
                HousingType.from(housingType),
                hasOtherPets,
                new ReasonText(reason)
        );

        return adoptionRequestGateway.updateAdoptionRequest(id, updated);
    }
}
