package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicantDocumentTest {

    @Test
    void shouldCreateValidDocument() {
        ApplicantDocument document = new ApplicantDocument("12345678900");

        assertEquals("12345678900", document.value());
    }

    @Test
    void shouldTrimDocument() {
        ApplicantDocument document = new ApplicantDocument("  12345678900  ");

        assertEquals("12345678900", document.value());
    }

    @Test
    void shouldFailWhenDocumentIsNull() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantDocument(null));
    }

    @Test
    void shouldFailWhenDocumentIsBlank() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantDocument("   "));
    }

    @Test
    void shouldFailWhenDocumentIsTooShort() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantDocument("1234"));
    }

    @Test
    void shouldFailWhenDocumentIsTooLong() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantDocument("1234567890123456789012345678901"));
    }

    @Test
    void shouldImplementToString() {
        ApplicantDocument document = new ApplicantDocument("12345678900");

        assertEquals("12345678900", document.toString());
    }

    @Test
    void shouldImplementEqualsAndHashCode() {
        ApplicantDocument a = new ApplicantDocument("12345678900");
        ApplicantDocument b = new ApplicantDocument("12345678900");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotBeEqualToDifferentDocument() {
        ApplicantDocument a = new ApplicantDocument("12345678900");
        ApplicantDocument b = new ApplicantDocument("99999999999");

        assertNotEquals(a, b);
    }

    @Test
    void shouldReturnTrueWhenComparingSameInstance() {
        ApplicantDocument document = new ApplicantDocument("12345678900");

        assertEquals(document, document);
    }

    @Test
    void shouldReturnFalseWhenComparingWithDifferentType() {
        ApplicantDocument document = new ApplicantDocument("12345678900");

        assertNotEquals(document, "string");
    }

    @Test
    void shouldReturnFalseWhenComparingWithNull() {
        ApplicantDocument document = new ApplicantDocument("12345678900");

        assertNotEquals(document, null);
    }
}