package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldNormalizeEmail() {
        Email email = new Email("  TeSt@Example.COM ");
        assertEquals("test@example.com", email.value());
        assertEquals("test@example.com", email.toString());
    }

    @Test
    void shouldRejectNull() {
        InvalidUserInputException ex =
                assertThrows(InvalidUserInputException.class, () -> new Email(null));
        assertEquals("Email não pode ser vazio.", ex.getMessage());
    }

    @Test
    void shouldRejectBlank() {
        InvalidUserInputException ex =
                assertThrows(InvalidUserInputException.class, () -> new Email("   "));
        assertEquals("Email não pode ser vazio.", ex.getMessage());
    }

    @Test
    void shouldRejectWithoutAt() {
        InvalidUserInputException ex =
                assertThrows(InvalidUserInputException.class, () -> new Email("abc.com"));
        assertEquals("Email inválido: abc.com", ex.getMessage());
    }

    @Test
    void shouldSupportEquality() {
        Email a = new Email("a@b.com");
        Email b = new Email("A@B.COM");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnTrueForSameInstance() {
        Email a = new Email("a@b.com");
        assertEquals(a, a); // cobre: if (this == o) return true;
    }

    @Test
    void equalsShouldReturnFalseForDifferentType() {
        Email a = new Email("a@b.com");
        assertNotEquals(a, "a@b.com"); // cobre: if (!(o instanceof Email)) return false;
    }
}