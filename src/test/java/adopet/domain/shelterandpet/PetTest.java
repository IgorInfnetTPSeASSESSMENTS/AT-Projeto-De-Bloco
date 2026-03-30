package adopet.domain.shelterandpet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PetTest {

    @Test
    void shouldCreateValidPet() {
        Pet pet = new Pet(
                1L,
                PetType.GATO,
                new PetName("Mimi"),
                "SRD",
                new AgeYears(2),
                "Branco",
                new WeightKg(3.2),
                PetStatus.AVAILABLE
        );

        assertEquals(1L, pet.id());
        assertEquals(PetType.GATO, pet.type());
        assertEquals("Mimi", pet.name().value());
    }

    @Test
    void shouldRejectNullBreed() {
        assertThrows(IllegalArgumentException.class, () -> new Pet(
                1L,
                PetType.GATO,
                new PetName("Mimi"),
                null, // cobre branch breed == null
                new AgeYears(2),
                "Branco",
                new WeightKg(3.2),
                PetStatus.AVAILABLE
        ));
    }

    @Test
    void shouldRejectBlankBreed() {
        assertThrows(IllegalArgumentException.class, () -> new Pet(
                1L,
                PetType.GATO,
                new PetName("Mimi"),
                " ",
                new AgeYears(2),
                "Branco",
                new WeightKg(3.2),
                PetStatus.AVAILABLE
        ));
    }

    @Test
    void shouldRejectNullColor() {
        assertThrows(IllegalArgumentException.class, () -> new Pet(
                1L,
                PetType.GATO,
                new PetName("Mimi"),
                "SRD",
                new AgeYears(2),
                null, // cobre branch color == null
                new WeightKg(3.2),
                PetStatus.AVAILABLE
        ));
    }

    @Test
    void shouldRejectBlankColor() {
        assertThrows(IllegalArgumentException.class, () -> new Pet(
                1L,
                PetType.GATO,
                new PetName("Mimi"),
                "SRD",
                new AgeYears(2),
                " ",
                new WeightKg(3.2),
                PetStatus.AVAILABLE
        ));
    }
}