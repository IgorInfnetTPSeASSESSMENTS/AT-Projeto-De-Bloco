package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberTest {

    @Test
    void shouldTrim() {
        PhoneNumber p = new PhoneNumber("  31999999999 ");
        assertEquals("31999999999", p.value());
        assertEquals("31999999999", p.toString());
    }

    @Test
    void shouldRejectNull() {
        InvalidUserInputException ex =
                assertThrows(InvalidUserInputException.class, () -> new PhoneNumber(null));
        assertEquals("Telefone não pode ser vazio.", ex.getMessage());
    }

    @Test
    void shouldRejectBlank() {
        InvalidUserInputException ex =
                assertThrows(InvalidUserInputException.class, () -> new PhoneNumber(" "));
        assertEquals("Telefone não pode ser vazio.", ex.getMessage());
    }

    @Test
    void shouldRejectTooShort() {
        InvalidUserInputException ex =
                assertThrows(InvalidUserInputException.class, () -> new PhoneNumber("1234567"));
        assertEquals("Telefone inválido: 1234567", ex.getMessage());
    }

    @Test
    void shouldSupportEquality() {
        PhoneNumber a = new PhoneNumber("31999999999");
        PhoneNumber b = new PhoneNumber("31999999999");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnTrueForSameInstance() {
        PhoneNumber a = new PhoneNumber("31999999999");
        assertEquals(a, a); // cobre: if (this == o) return true;
    }

    @Test
    void equalsShouldReturnFalseForDifferentType() {
        PhoneNumber a = new PhoneNumber("31999999999");
        assertNotEquals(a, "31999999999"); // cobre: if (!(o instanceof PhoneNumber)) return false;
    }
}