package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeightKgTest {

    @Test
    void shouldAcceptSmallPositive() {
        assertEquals(0.1, new WeightKg(0.1).value(), 1e-9);
    }

    @Test
    void shouldRejectZero() {
        assertThrows(InvalidUserInputException.class, () -> new WeightKg(0));
    }

    @Test
    void shouldRejectNegative() {
        assertThrows(InvalidUserInputException.class, () -> new WeightKg(-0.1));
    }

    @Test
    void shouldRejectHuge() {
        assertThrows(InvalidUserInputException.class, () -> new WeightKg(151));
    }
}