package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdoptionRequestStatusTest {

    @Test
    void shouldParsePendingInEnglish() {
        assertEquals(AdoptionRequestStatus.PENDING, AdoptionRequestStatus.from("PENDING"));
    }

    @Test
    void shouldParsePendingInPortuguese() {
        assertEquals(AdoptionRequestStatus.PENDING, AdoptionRequestStatus.from("PENDENTE"));
    }

    @Test
    void shouldParseUnderReviewInEnglish() {
        assertEquals(AdoptionRequestStatus.UNDER_REVIEW, AdoptionRequestStatus.from("UNDER_REVIEW"));
    }

    @Test
    void shouldParseUnderReviewWithAlias() {
        assertEquals(AdoptionRequestStatus.UNDER_REVIEW, AdoptionRequestStatus.from("EM_ANALISE"));
    }

    @Test
    void shouldParseUnderReviewWithAccentAndSpaces() {
        assertEquals(AdoptionRequestStatus.UNDER_REVIEW, AdoptionRequestStatus.from("EM ANÁLISE"));
    }

    @Test
    void shouldParseApprovedInEnglish() {
        assertEquals(AdoptionRequestStatus.APPROVED, AdoptionRequestStatus.from("APPROVED"));
    }

    @Test
    void shouldParseApprovedInPortugueseFemale() {
        assertEquals(AdoptionRequestStatus.APPROVED, AdoptionRequestStatus.from("APROVADA"));
    }

    @Test
    void shouldParseApprovedInPortugueseMale() {
        assertEquals(AdoptionRequestStatus.APPROVED, AdoptionRequestStatus.from("APROVADO"));
    }

    @Test
    void shouldParseRejectedInEnglish() {
        assertEquals(AdoptionRequestStatus.REJECTED, AdoptionRequestStatus.from("REJECTED"));
    }

    @Test
    void shouldParseRejectedInPortugueseFemale() {
        assertEquals(AdoptionRequestStatus.REJECTED, AdoptionRequestStatus.from("REJEITADA"));
    }

    @Test
    void shouldParseRejectedInPortugueseMale() {
        assertEquals(AdoptionRequestStatus.REJECTED, AdoptionRequestStatus.from("REJEITADO"));
    }

    @Test
    void shouldParseCancelledInEnglish() {
        assertEquals(AdoptionRequestStatus.CANCELLED, AdoptionRequestStatus.from("CANCELLED"));
    }

    @Test
    void shouldParseCancelledInPortugueseFemale() {
        assertEquals(AdoptionRequestStatus.CANCELLED, AdoptionRequestStatus.from("CANCELADA"));
    }

    @Test
    void shouldParseCancelledInPortugueseMale() {
        assertEquals(AdoptionRequestStatus.CANCELLED, AdoptionRequestStatus.from("CANCELADO"));
    }

    @Test
    void shouldTrimAndUppercaseInput() {
        assertEquals(AdoptionRequestStatus.PENDING, AdoptionRequestStatus.from("  pending  "));
    }

    @Test
    void shouldFailWhenRawIsNull() {
        assertThrows(InvalidUserInputException.class, () -> AdoptionRequestStatus.from(null));
    }

    @Test
    void shouldFailWhenRawIsBlank() {
        assertThrows(InvalidUserInputException.class, () -> AdoptionRequestStatus.from("   "));
    }

    @Test
    void shouldFailWhenRawIsInvalid() {
        assertThrows(InvalidUserInputException.class, () -> AdoptionRequestStatus.from("UNKNOWN"));
    }
}