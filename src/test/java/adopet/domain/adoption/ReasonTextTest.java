package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReasonTextTest {

    @Test
    void shouldCreateValidReason() {
        ReasonText reason = new ReasonText("Quero adotar com responsabilidade.");

        assertEquals("Quero adotar com responsabilidade.", reason.value());
    }

    @Test
    void shouldTrimReason() {
        ReasonText reason = new ReasonText("  Quero adotar com responsabilidade.  ");

        assertEquals("Quero adotar com responsabilidade.", reason.value());
    }

    @Test
    void shouldFailWhenReasonIsNull() {
        assertThrows(InvalidUserInputException.class, () -> new ReasonText(null));
    }

    @Test
    void shouldFailWhenReasonIsBlank() {
        assertThrows(InvalidUserInputException.class, () -> new ReasonText("   "));
    }

    @Test
    void shouldFailWhenReasonIsTooShort() {
        assertThrows(InvalidUserInputException.class, () -> new ReasonText("curto"));
    }

    @Test
    void shouldFailWhenReasonIsTooLong() {
        assertThrows(InvalidUserInputException.class, () -> new ReasonText("a".repeat(1001)));
    }

    @Test
    void shouldImplementToString() {
        ReasonText reason = new ReasonText("Quero adotar com responsabilidade.");

        assertEquals("Quero adotar com responsabilidade.", reason.toString());
    }
}