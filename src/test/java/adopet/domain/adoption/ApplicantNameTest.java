package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicantNameTest {

    @Test
    void shouldCreateValidName() {
        ApplicantName name = new ApplicantName("Maria Silva");

        assertEquals("Maria Silva", name.value());
    }

    @Test
    void shouldTrimName() {
        ApplicantName name = new ApplicantName("  Maria Silva  ");

        assertEquals("Maria Silva", name.value());
    }

    @Test
    void shouldFailWhenNameIsNull() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantName(null));
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantName("   "));
    }

    @Test
    void shouldFailWhenNameIsTooShort() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantName("Al"));
    }

    @Test
    void shouldFailWhenNameIsTooLong() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantName("a".repeat(101)));
    }

    @Test
    void shouldImplementToString() {
        ApplicantName name = new ApplicantName("Maria Silva");

        assertEquals("Maria Silva", name.toString());
    }
}