package adopet.domain.shelterandpet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShelterTest {

    @Test
    void shouldCreateValidShelter() {
        Shelter shelter = new Shelter(1L, "Abrigo X", new PhoneNumber("31999999999"), new Email("a@b.com"));

        assertEquals(1L, shelter.id());
        assertEquals("Abrigo X", shelter.name());
    }

    @Test
    void shouldRejectNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Shelter(1L, null, new PhoneNumber("31999999999"), new Email("a@b.com")));
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Shelter(1L, "   ", new PhoneNumber("31999999999"), new Email("a@b.com")));
    }
}