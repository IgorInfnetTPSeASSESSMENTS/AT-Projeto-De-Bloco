package adopet.domain.shelterandpet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PetTypeTest {

    @Test
    void shouldParseGato() {
        assertEquals(PetType.GATO, PetType.from("gato"));
        assertEquals(PetType.GATO, PetType.from(" GATO "));
    }

    @Test
    void shouldParseCachorro() {
        assertEquals(PetType.CACHORRO, PetType.from("cachorro"));
        assertEquals(PetType.CACHORRO, PetType.from(" CACHORRO "));
    }

    @Test
    void shouldRejectNull() {
        assertThrows(IllegalArgumentException.class, () -> PetType.from(null));
    }

    @Test
    void shouldRejectUnknown() {
        assertThrows(IllegalArgumentException.class, () -> PetType.from("papagaio"));
    }
}