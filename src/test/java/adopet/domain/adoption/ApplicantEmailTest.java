package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicantEmailTest {

    @Test
    void shouldCreateValidEmail() {
        ApplicantEmail email = new ApplicantEmail("maria@email.com");

        assertEquals("maria@email.com", email.value());
    }

    @Test
    void shouldTrimAndLowercaseEmail() {
        ApplicantEmail email = new ApplicantEmail("  MARIA@EMAIL.COM  ");

        assertEquals("maria@email.com", email.value());
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantEmail(null));
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantEmail("   "));
    }

    @Test
    void shouldFailWhenEmailDoesNotContainAt() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantEmail("mariaemail.com"));
    }

    @Test
    void shouldFailWhenEmailStartsWithAt() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantEmail("@email.com"));
    }

    @Test
    void shouldFailWhenEmailEndsWithAt() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantEmail("maria@"));
    }

    @Test
    void shouldFailWhenEmailIsTooLong() {
        String local = "a".repeat(121 - "@x.com".length());
        String tooLong = local + "@x.com";

        assertThrows(InvalidUserInputException.class, () -> new ApplicantEmail(tooLong));
    }

    @Test
    void shouldImplementToString() {
        ApplicantEmail email = new ApplicantEmail("maria@email.com");

        assertEquals("maria@email.com", email.toString());
    }

    @Test
    void shouldImplementEqualsAndHashCode() {
        ApplicantEmail a = new ApplicantEmail("maria@email.com");
        ApplicantEmail b = new ApplicantEmail("maria@email.com");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotBeEqualToDifferentEmail() {
        ApplicantEmail a = new ApplicantEmail("maria@email.com");
        ApplicantEmail b = new ApplicantEmail("joao@email.com");

        assertNotEquals(a, b);
    }

    @Test
    void shouldReturnTrueWhenComparingSameInstance() {
        ApplicantEmail email = new ApplicantEmail("maria@email.com");

        assertEquals(email, email);
    }

    @Test
    void shouldReturnFalseWhenComparingWithDifferentType() {
        ApplicantEmail email = new ApplicantEmail("maria@email.com");

        assertNotEquals(email, "string");
    }

    @Test
    void shouldReturnFalseWhenComparingWithNull() {
        ApplicantEmail email = new ApplicantEmail("maria@email.com");

        assertNotEquals(email, null);
    }
}