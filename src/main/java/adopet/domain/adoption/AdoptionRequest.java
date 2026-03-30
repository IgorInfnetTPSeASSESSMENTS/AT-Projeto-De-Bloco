package adopet.domain.adoption;

import adopet.exception.InvalidStateTransitionException;
import adopet.exception.InvalidUserInputException;

import java.time.LocalDateTime;

public record AdoptionRequest(
        Long id,
        Long petId,
        Long shelterId,
        ApplicantName applicantName,
        ApplicantEmail applicantEmail,
        ApplicantPhone applicantPhone,
        ApplicantDocument applicantDocument,
        HousingType housingType,
        boolean hasOtherPets,
        ReasonText reason,
        AdoptionRequestStatus status,
        EligibilityAnalysis eligibilityAnalysis,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public AdoptionRequest {
        if (petId == null || petId <= 0) {
            throw new InvalidUserInputException("Id do pet é obrigatório.");
        }
        if (shelterId == null || shelterId <= 0) {
            throw new InvalidUserInputException("Id do abrigo é obrigatório.");
        }
        if (applicantName == null) {
            throw new InvalidUserInputException("Nome do solicitante é obrigatório.");
        }
        if (applicantEmail == null) {
            throw new InvalidUserInputException("Email do solicitante é obrigatório.");
        }
        if (applicantPhone == null) {
            throw new InvalidUserInputException("Telefone do solicitante é obrigatório.");
        }
        if (applicantDocument == null) {
            throw new InvalidUserInputException("Documento do solicitante é obrigatório.");
        }
        if (housingType == null) {
            throw new InvalidUserInputException("Tipo de moradia é obrigatório.");
        }
        if (reason == null) {
            throw new InvalidUserInputException("Motivo da adoção é obrigatório.");
        }
        if (status == null) {
            throw new InvalidUserInputException("Status da solicitação é obrigatório.");
        }
        if (eligibilityAnalysis == null) {
            throw new InvalidUserInputException("Resultado da análise é obrigatório.");
        }
        if (createdAt == null) {
            throw new InvalidUserInputException("Data de criação é obrigatória.");
        }
        if (updatedAt == null) {
            throw new InvalidUserInputException("Data de atualização é obrigatória.");
        }
        if (updatedAt.isBefore(createdAt)) {
            throw new InvalidUserInputException("Data de atualização não pode ser anterior à criação.");
        }
    }

    public static AdoptionRequest newRequest(
            Long petId,
            Long shelterId,
            ApplicantName applicantName,
            ApplicantEmail applicantEmail,
            ApplicantPhone applicantPhone,
            ApplicantDocument applicantDocument,
            HousingType housingType,
            boolean hasOtherPets,
            ReasonText reason,
            EligibilityAnalysis eligibilityAnalysis
    ) {
        LocalDateTime now = LocalDateTime.now();
        AdoptionRequestStatus initialStatus = eligibilityAnalysis == EligibilityAnalysis.ELIGIBLE
                ? AdoptionRequestStatus.UNDER_REVIEW
                : AdoptionRequestStatus.PENDING;

        return new AdoptionRequest(
                null,
                petId,
                shelterId,
                applicantName,
                applicantEmail,
                applicantPhone,
                applicantDocument,
                housingType,
                hasOtherPets,
                reason,
                initialStatus,
                eligibilityAnalysis,
                now,
                now
        );
    }

    public AdoptionRequest updateDraft(
            Long petId,
            Long shelterId,
            ApplicantName applicantName,
            ApplicantEmail applicantEmail,
            ApplicantPhone applicantPhone,
            ApplicantDocument applicantDocument,
            HousingType housingType,
            boolean hasOtherPets,
            ReasonText reason
    ) {
        if (!(status == AdoptionRequestStatus.PENDING || status == AdoptionRequestStatus.UNDER_REVIEW)) {
            throw new InvalidStateTransitionException("Somente solicitações pendentes ou em análise podem ser editadas.");
        }

        return new AdoptionRequest(
                id,
                petId,
                shelterId,
                applicantName,
                applicantEmail,
                applicantPhone,
                applicantDocument,
                housingType,
                hasOtherPets,
                reason,
                status,
                eligibilityAnalysis,
                createdAt,
                LocalDateTime.now()
        );
    }

    public AdoptionRequest approve() {
        if (!(status == AdoptionRequestStatus.PENDING || status == AdoptionRequestStatus.UNDER_REVIEW)) {
            throw new InvalidStateTransitionException("Somente solicitações pendentes ou em análise podem ser aprovadas.");
        }

        return new AdoptionRequest(
                id,
                petId,
                shelterId,
                applicantName,
                applicantEmail,
                applicantPhone,
                applicantDocument,
                housingType,
                hasOtherPets,
                reason,
                AdoptionRequestStatus.APPROVED,
                eligibilityAnalysis,
                createdAt,
                LocalDateTime.now()
        );
    }

    public AdoptionRequest reject() {
        if (!(status == AdoptionRequestStatus.PENDING || status == AdoptionRequestStatus.UNDER_REVIEW)) {
            throw new InvalidStateTransitionException("Somente solicitações pendentes ou em análise podem ser rejeitadas.");
        }

        return new AdoptionRequest(
                id,
                petId,
                shelterId,
                applicantName,
                applicantEmail,
                applicantPhone,
                applicantDocument,
                housingType,
                hasOtherPets,
                reason,
                AdoptionRequestStatus.REJECTED,
                eligibilityAnalysis,
                createdAt,
                LocalDateTime.now()
        );
    }

    public AdoptionRequest cancel() {
        if (status == AdoptionRequestStatus.APPROVED) {
            throw new InvalidStateTransitionException("Solicitações aprovadas não podem ser canceladas.");
        }
        if (status == AdoptionRequestStatus.REJECTED) {
            throw new InvalidStateTransitionException("Solicitações rejeitadas não podem ser canceladas.");
        }
        if (status == AdoptionRequestStatus.CANCELLED) {
            throw new InvalidStateTransitionException("Solicitação já está cancelada.");
        }

        return new AdoptionRequest(
                id,
                petId,
                shelterId,
                applicantName,
                applicantEmail,
                applicantPhone,
                applicantDocument,
                housingType,
                hasOtherPets,
                reason,
                AdoptionRequestStatus.CANCELLED,
                eligibilityAnalysis,
                createdAt,
                LocalDateTime.now()
        );
    }

    public AdoptionRequest withEligibilityAnalysis(EligibilityAnalysis newAnalysis) {
        AdoptionRequestStatus recalculatedStatus = switch (newAnalysis) {
            case ELIGIBLE -> AdoptionRequestStatus.UNDER_REVIEW;
            case NOT_ELIGIBLE -> AdoptionRequestStatus.REJECTED;
            case REQUIRES_MANUAL_REVIEW, UNAVAILABLE, NOT_REQUESTED -> AdoptionRequestStatus.PENDING;
        };

        return new AdoptionRequest(
                id,
                petId,
                shelterId,
                applicantName,
                applicantEmail,
                applicantPhone,
                applicantDocument,
                housingType,
                hasOtherPets,
                reason,
                recalculatedStatus,
                newAnalysis,
                createdAt,
                LocalDateTime.now()
        );
    }
}
