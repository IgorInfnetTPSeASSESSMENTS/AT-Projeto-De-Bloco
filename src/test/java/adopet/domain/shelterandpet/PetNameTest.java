package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PetNameTest {

    @Test
    void shouldTrim() {
        PetName name = new PetName("  Mimi ");
        assertEquals("Mimi", name.value());
        assertEquals("Mimi", name.toString());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(InvalidUserInputException.class, () -> new PetName(null));
    }

    @Test
    void shouldRejectBlank() {
        assertThrows(InvalidUserInputException.class, () -> new PetName("   "));
    }

    @Test
    void shouldRejectTooLong() {
        String longName = "a".repeat(61);
        assertThrows(InvalidUserInputException.class, () -> new PetName(longName));
    }
}