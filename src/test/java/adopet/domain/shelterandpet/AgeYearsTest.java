package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AgeYearsTest {

    @Test
    void shouldAcceptLowerBound() {
        assertEquals(0, new AgeYears(0).value());
    }

    @Test
    void shouldAcceptUpperBound() {
        assertEquals(40, new AgeYears(40).value());
    }

    @Test
    void shouldRejectNegative() {
        assertThrows(InvalidUserInputException.class, () -> new AgeYears(-1));
    }

    @Test
    void shouldRejectAboveUpperBound() {
        assertThrows(InvalidUserInputException.class, () -> new AgeYears(41));
    }
}
