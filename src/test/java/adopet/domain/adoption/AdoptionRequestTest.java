package adopet.domain.adoption;

import adopet.exception.InvalidStateTransitionException;
import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AdoptionRequestTest {

    @Test
    void shouldCreateNewRequestWithPendingWhenAnalysisIsNotEligibleOrUnavailableType() {
        AdoptionRequest request = AdoptionRequest.newRequest(
                1L,
                10L,
                new ApplicantName("Maria Silva"),
                new ApplicantEmail("maria@email.com"),
                new ApplicantPhone("31999999999"),
                new ApplicantDocument("12345678900"),
                HousingType.HOUSE,
                true,
                new ReasonText("Quero adotar com responsabilidade."),
                EligibilityAnalysis.REQUIRES_MANUAL_REVIEW
        );

        assertNull(request.id());
        assertEquals(AdoptionRequestStatus.PENDING, request.status());
        assertEquals(EligibilityAnalysis.REQUIRES_MANUAL_REVIEW, request.eligibilityAnalysis());
        assertNotNull(request.createdAt());
        assertNotNull(request.updatedAt());
    }

    @Test
    void shouldCreateNewRequestWithUnderReviewWhenAnalysisIsEligible() {
        AdoptionRequest request = AdoptionRequest.newRequest(
                1L,
                10L,
                new ApplicantName("Maria Silva"),
                new ApplicantEmail("maria@email.com"),
                new ApplicantPhone("31999999999"),
                new ApplicantDocument("12345678900"),
                HousingType.HOUSE,
                true,
                new ReasonText("Quero adotar com responsabilidade."),
                EligibilityAnalysis.ELIGIBLE
        );

        assertEquals(AdoptionRequestStatus.UNDER_REVIEW, request.status());
    }

    @Test
    void shouldUpdateDraftWhenPending() {
        AdoptionRequest request = basePendingRequest();

        AdoptionRequest updated = request.updateDraft(
                2L,
                20L,
                new ApplicantName("Maria Editada"),
                new ApplicantEmail("editada@email.com"),
                new ApplicantPhone("31888888888"),
                new ApplicantDocument("11122233344"),
                HousingType.APARTMENT,
                false,
                new ReasonText("Agora tenho uma justificativa melhor.")
        );

        assertEquals(2L, updated.petId());
        assertEquals(20L, updated.shelterId());
        assertEquals("Maria Editada", updated.applicantName().value());
        assertEquals("editada@email.com", updated.applicantEmail().value());
        assertEquals("31888888888", updated.applicantPhone().value());
        assertEquals("11122233344", updated.applicantDocument().value());
        assertEquals(HousingType.APARTMENT, updated.housingType());
        assertFalse(updated.hasOtherPets());
        assertEquals("Agora tenho uma justificativa melhor.", updated.reason().value());
        assertEquals(request.createdAt(), updated.createdAt());
        assertFalse(updated.updatedAt().isBefore(request.updatedAt()));
    }

    @Test
    void shouldUpdateDraftWhenUnderReview() {
        AdoptionRequest request = baseEligibleRequest();

        AdoptionRequest updated = request.updateDraft(
                2L,
                20L,
                new ApplicantName("Maria Editada"),
                new ApplicantEmail("editada@email.com"),
                new ApplicantPhone("31888888888"),
                new ApplicantDocument("11122233344"),
                HousingType.OTHER,
                false,
                new ReasonText("Texto atualizado com dados válidos.")
        );

        assertEquals(AdoptionRequestStatus.UNDER_REVIEW, updated.status());
    }

    @Test
    void shouldNotUpdateDraftWhenApproved() {
        AdoptionRequest request = basePendingRequest().approve();

        assertThrows(InvalidStateTransitionException.class, () ->
                request.updateDraft(
                        2L,
                        20L,
                        new ApplicantName("Maria Editada"),
                        new ApplicantEmail("editada@email.com"),
                        new ApplicantPhone("31888888888"),
                        new ApplicantDocument("11122233344"),
                        HousingType.APARTMENT,
                        false,
                        new ReasonText("Texto atualizado com dados válidos.")
                )
        );
    }

    @Test
    void shouldApproveWhenPending() {
        AdoptionRequest approved = basePendingRequest().approve();

        assertEquals(AdoptionRequestStatus.APPROVED, approved.status());
    }

    @Test
    void shouldApproveWhenUnderReview() {
        AdoptionRequest approved = baseEligibleRequest().approve();

        assertEquals(AdoptionRequestStatus.APPROVED, approved.status());
    }

    @Test
    void shouldNotApproveWhenRejected() {
        AdoptionRequest request = basePendingRequest().reject();

        assertThrows(InvalidStateTransitionException.class, request::approve);
    }

    @Test
    void shouldRejectWhenPending() {
        AdoptionRequest rejected = basePendingRequest().reject();

        assertEquals(AdoptionRequestStatus.REJECTED, rejected.status());
    }

    @Test
    void shouldRejectWhenUnderReview() {
        AdoptionRequest rejected = baseEligibleRequest().reject();

        assertEquals(AdoptionRequestStatus.REJECTED, rejected.status());
    }

    @Test
    void shouldNotRejectWhenApproved() {
        AdoptionRequest request = basePendingRequest().approve();

        assertThrows(InvalidStateTransitionException.class, request::reject);
    }

    @Test
    void shouldCancelWhenPending() {
        AdoptionRequest cancelled = basePendingRequest().cancel();

        assertEquals(AdoptionRequestStatus.CANCELLED, cancelled.status());
    }

    @Test
    void shouldCancelWhenUnderReview() {
        AdoptionRequest cancelled = baseEligibleRequest().cancel();

        assertEquals(AdoptionRequestStatus.CANCELLED, cancelled.status());
    }

    @Test
    void shouldNotCancelWhenApproved() {
        AdoptionRequest request = basePendingRequest().approve();

        assertThrows(InvalidStateTransitionException.class, request::cancel);
    }

    @Test
    void shouldNotCancelWhenRejected() {
        AdoptionRequest request = basePendingRequest().reject();

        assertThrows(InvalidStateTransitionException.class, request::cancel);
    }

    @Test
    void shouldNotCancelWhenAlreadyCancelled() {
        AdoptionRequest request = basePendingRequest().cancel();

        assertThrows(InvalidStateTransitionException.class, request::cancel);
    }

    @Test
    void shouldRecalculateStatusWhenAnalysisBecomesEligible() {
        AdoptionRequest request = basePendingRequest();

        AdoptionRequest updated = request.withEligibilityAnalysis(EligibilityAnalysis.ELIGIBLE);

        assertEquals(EligibilityAnalysis.ELIGIBLE, updated.eligibilityAnalysis());
        assertEquals(AdoptionRequestStatus.UNDER_REVIEW, updated.status());
    }

    @Test
    void shouldRecalculateStatusWhenAnalysisBecomesNotEligible() {
        AdoptionRequest request = basePendingRequest();

        AdoptionRequest updated = request.withEligibilityAnalysis(EligibilityAnalysis.NOT_ELIGIBLE);

        assertEquals(EligibilityAnalysis.NOT_ELIGIBLE, updated.eligibilityAnalysis());
        assertEquals(AdoptionRequestStatus.REJECTED, updated.status());
    }

    @Test
    void shouldRecalculateStatusWhenAnalysisRequiresManualReview() {
        AdoptionRequest request = baseEligibleRequest();

        AdoptionRequest updated = request.withEligibilityAnalysis(EligibilityAnalysis.REQUIRES_MANUAL_REVIEW);

        assertEquals(AdoptionRequestStatus.PENDING, updated.status());
    }

    @Test
    void shouldRecalculateStatusWhenAnalysisIsUnavailable() {
        AdoptionRequest request = baseEligibleRequest();

        AdoptionRequest updated = request.withEligibilityAnalysis(EligibilityAnalysis.UNAVAILABLE);

        assertEquals(AdoptionRequestStatus.PENDING, updated.status());
    }

    @Test
    void shouldRecalculateStatusWhenAnalysisIsNotRequested() {
        AdoptionRequest request = baseEligibleRequest();

        AdoptionRequest updated = request.withEligibilityAnalysis(EligibilityAnalysis.NOT_REQUESTED);

        assertEquals(AdoptionRequestStatus.PENDING, updated.status());
    }

    @Test
    void shouldFailWhenPetIdIsNull() {
        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, null, 10L,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        AdoptionRequestStatus.PENDING,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );

        assertEquals("Id do pet é obrigatório.", ex.getMessage());
    }

    @Test
    void shouldFailWhenPetIdIsZero() {
        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 0L, 10L,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        AdoptionRequestStatus.PENDING,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void shouldFailWhenShelterIdIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 1L, null,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        AdoptionRequestStatus.PENDING,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void shouldFailWhenShelterIdIsZero() {
        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 1L, 0L,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        AdoptionRequestStatus.PENDING,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void shouldFailWhenApplicantNameIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 1L, 10L,
                        null,
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        AdoptionRequestStatus.PENDING,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void shouldFailWhenApplicantEmailIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 1L, 10L,
                        new ApplicantName("Maria"),
                        null,
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        AdoptionRequestStatus.PENDING,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void shouldFailWhenApplicantPhoneIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 1L, 10L,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        null,
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        AdoptionRequestStatus.PENDING,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void shouldFailWhenApplicantDocumentIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 1L, 10L,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        null,
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        AdoptionRequestStatus.PENDING,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void shouldFailWhenHousingTypeIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 1L, 10L,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        null,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        AdoptionRequestStatus.PENDING,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void shouldFailWhenReasonIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 1L, 10L,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        null,
                        AdoptionRequestStatus.PENDING,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void shouldFailWhenStatusIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 1L, 10L,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        null,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void shouldFailWhenEligibilityAnalysisIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 1L, 10L,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        AdoptionRequestStatus.PENDING,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void shouldFailWhenCreatedAtIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 1L, 10L,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        AdoptionRequestStatus.PENDING,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        null,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void shouldFailWhenUpdatedAtIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 1L, 10L,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        AdoptionRequestStatus.PENDING,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        LocalDateTime.now(),
                        null
                )
        );
    }

    @Test
    void shouldFailWhenUpdatedAtIsBeforeCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = createdAt.minusSeconds(1);

        assertThrows(InvalidUserInputException.class, () ->
                new AdoptionRequest(
                        1L, 1L, 10L,
                        new ApplicantName("Maria"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade."),
                        AdoptionRequestStatus.PENDING,
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                        createdAt,
                        updatedAt
                )
        );
    }

    private AdoptionRequest basePendingRequest() {
        LocalDateTime now = LocalDateTime.now();
        return new AdoptionRequest(
                1L,
                1L,
                10L,
                new ApplicantName("Maria Silva"),
                new ApplicantEmail("maria@email.com"),
                new ApplicantPhone("31999999999"),
                new ApplicantDocument("12345678900"),
                HousingType.HOUSE,
                true,
                new ReasonText("Quero adotar com responsabilidade."),
                AdoptionRequestStatus.PENDING,
                EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                now,
                now
        );
    }

    private AdoptionRequest baseEligibleRequest() {
        LocalDateTime now = LocalDateTime.now();
        return new AdoptionRequest(
                1L,
                1L,
                10L,
                new ApplicantName("Maria Silva"),
                new ApplicantEmail("maria@email.com"),
                new ApplicantPhone("31999999999"),
                new ApplicantDocument("12345678900"),
                HousingType.HOUSE,
                true,
                new ReasonText("Quero adotar com responsabilidade."),
                AdoptionRequestStatus.UNDER_REVIEW,
                EligibilityAnalysis.ELIGIBLE,
                now,
                now
        );
    }
}