package adopet.application.adoption;

import adopet.domain.adoption.AdoptionRequest;
import adopet.domain.adoption.ApplicantDocument;
import adopet.domain.adoption.ApplicantEmail;
import adopet.domain.adoption.ApplicantName;
import adopet.domain.adoption.ApplicantPhone;
import adopet.domain.adoption.EligibilityAnalysis;
import adopet.domain.adoption.HousingType;
import adopet.domain.adoption.ReasonText;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetStatus;
import adopet.exception.DuplicateEntityException;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.AdoptionRequestGateway;
import adopet.gateway.EligibilityAnalysisGateway;
import adopet.gateway.NotificationGateway;
import adopet.gateway.PetGateway;

public class CreateAdoptionRequestCommandHandler {

    private final AdoptionRequestGateway adoptionRequestGateway;
    private final EligibilityAnalysisGateway eligibilityAnalysisGateway;
    private final NotificationGateway notificationGateway;
    private final PetGateway petGateway;

    public CreateAdoptionRequestCommandHandler(
            AdoptionRequestGateway adoptionRequestGateway,
            EligibilityAnalysisGateway eligibilityAnalysisGateway,
            NotificationGateway notificationGateway,
            PetGateway petGateway
    ) {
        this.adoptionRequestGateway = adoptionRequestGateway;
        this.eligibilityAnalysisGateway = eligibilityAnalysisGateway;
        this.notificationGateway = notificationGateway;
        this.petGateway = petGateway;
    }

    public AdoptionRequestOperationResult execute(
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

        if (petId == null || petId <= 0) {
            throw new InvalidUserInputException("Id do pet é obrigatório.");
        }
        if (shelterId == null || shelterId <= 0) {
            throw new InvalidUserInputException("Id do abrigo é obrigatório.");
        }
        if (applicantName == null || applicantName.isBlank()) {
            throw new InvalidUserInputException("Nome do solicitante é obrigatório.");
        }
        if (applicantEmail == null || applicantEmail.isBlank()) {
            throw new InvalidUserInputException("Email do solicitante é obrigatório.");
        }
        if (applicantPhone == null || applicantPhone.isBlank()) {
            throw new InvalidUserInputException("Telefone do solicitante é obrigatório.");
        }
        if (applicantDocument == null || applicantDocument.isBlank()) {
            throw new InvalidUserInputException("Documento do solicitante é obrigatório.");
        }
        if (housingType == null || housingType.isBlank()) {
            throw new InvalidUserInputException("Tipo de moradia é obrigatório.");
        }
        if (reason == null || reason.isBlank()) {
            throw new InvalidUserInputException("Motivo da adoção é obrigatório.");
        }

        Pet pet = petGateway.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado (id=" + petId + ")."));

        if (pet.status() == PetStatus.ADOPTED) {
            throw new InvalidUserInputException("Este pet já foi adotado e não aceita novas solicitações.");
        }

        ApplicantDocument document = new ApplicantDocument(applicantDocument.trim());

        if (adoptionRequestGateway.existsActiveRequestForPetAndDocument(petId, document.value())) {
            throw new DuplicateEntityException("Já existe uma solicitação ativa para este pet com o mesmo documento.");
        }

        EligibilityAnalysis eligibilityAnalysis;
        AnalysisExecutionStatus analysisExecutionStatus;

        try {
            eligibilityAnalysis = eligibilityAnalysisGateway.analyze(
                    petId,
                    shelterId,
                    document.value(),
                    hasOtherPets,
                    reason.trim()
            );
            analysisExecutionStatus = AnalysisExecutionStatus.SUCCESS;
        } catch (RuntimeException exception) {
            eligibilityAnalysis = EligibilityAnalysis.UNAVAILABLE;
            analysisExecutionStatus = AnalysisExecutionStatus.FAILED;
        }

        AdoptionRequest created = adoptionRequestGateway.registerAdoptionRequest(
                AdoptionRequest.newRequest(
                        petId,
                        shelterId,
                        new ApplicantName(applicantName.trim()),
                        new ApplicantEmail(applicantEmail.trim()),
                        new ApplicantPhone(applicantPhone.trim()),
                        document,
                        HousingType.from(housingType.trim()),
                        hasOtherPets,
                        new ReasonText(reason.trim()),
                        eligibilityAnalysis
                )
        );

        boolean notificationSent = true;
        try {
            notificationGateway.notifyCreation(created.id(), created.applicantEmail().value());
        } catch (RuntimeException ignored) {
            notificationSent = false;
        }

        return new AdoptionRequestOperationResult(
                created,
                notificationSent,
                analysisExecutionStatus,
                eligibilityAnalysis
        );
    }
}