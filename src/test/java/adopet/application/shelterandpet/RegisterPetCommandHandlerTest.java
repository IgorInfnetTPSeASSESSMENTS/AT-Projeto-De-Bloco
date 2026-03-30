package adopet.application.shelterandpet;

import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetType;
import adopet.exception.InvalidUserInputException;
import adopet.fakes.AbstractFakePetGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterPetCommandHandlerTest {

    @Test
    void shouldRejectBlankShelterIdOrName() {
        RegisterPetCommandHandler handler = new RegisterPetCommandHandler(new NoopPetGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(null, "GATO", "Mimi", "SRD", 2, "Branco", 3.2));
        assertEquals("Id ou nome do abrigo não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("   ", "GATO", "Mimi", "SRD", 2, "Branco", 3.2));
        assertEquals("Id ou nome do abrigo não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldRejectBlankType() {
        RegisterPetCommandHandler handler = new RegisterPetCommandHandler(new NoopPetGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("1", null, "Mimi", "SRD", 2, "Branco", 3.2));
        assertEquals("Tipo não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("1", "   ", "Mimi", "SRD", 2, "Branco", 3.2));
        assertEquals("Tipo não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldRejectBlankName() {
        RegisterPetCommandHandler handler = new RegisterPetCommandHandler(new NoopPetGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("1", "GATO", null, "SRD", 2, "Branco", 3.2));
        assertEquals("Nome do pet não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("1", "GATO", "   ", "SRD", 2, "Branco", 3.2));
        assertEquals("Nome do pet não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldRejectBlankBreed() {
        RegisterPetCommandHandler handler = new RegisterPetCommandHandler(new NoopPetGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("1", "GATO", "Mimi", null, 2, "Branco", 3.2));
        assertEquals("Raça não pode ser vazia.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("1", "GATO", "Mimi", "   ", 2, "Branco", 3.2));
        assertEquals("Raça não pode ser vazia.", ex2.getMessage());
    }

    @Test
    void shouldRejectBlankColor() {
        RegisterPetCommandHandler handler = new RegisterPetCommandHandler(new NoopPetGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("1", "GATO", "Mimi", "SRD", 2, null, 3.2));
        assertEquals("Cor não pode ser vazia.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("1", "GATO", "Mimi", "SRD", 2, "   ", 3.2));
        assertEquals("Cor não pode ser vazia.", ex2.getMessage());
    }

    @Test
    void shouldTrimInputsAndCallGateway() {
        class SpyGateway extends NoopPetGateway {
            String lastShelter;
            Pet lastPet;

            @Override
            public Pet registerPet(String shelterIdOrName, Pet pet) {
                this.lastShelter = shelterIdOrName;
                this.lastPet = pet;

                return new Pet(
                        99L,
                        pet.type(),
                        pet.name(),
                        pet.breed(),
                        pet.age(),
                        pet.color(),
                        pet.weight(),
                        pet.status()
                );
            }
        }

        SpyGateway gateway = new SpyGateway();
        RegisterPetCommandHandler handler = new RegisterPetCommandHandler(gateway);

        Pet created = handler.execute("  s1  ", "  GATO ", "  Mimi  ", "  SRD  ", 2, "  Branco ", 3.2);

        assertEquals(99L, created.id());
        assertEquals("s1", gateway.lastShelter);

        assertNotNull(gateway.lastPet);
        assertNull(gateway.lastPet.id());
        assertEquals(PetType.GATO, gateway.lastPet.type());
        assertEquals("Mimi", gateway.lastPet.name().value());
        assertEquals("SRD", gateway.lastPet.breed());
        assertEquals(2, gateway.lastPet.age().value());
        assertEquals("Branco", gateway.lastPet.color());
        assertEquals(3.2, gateway.lastPet.weight().value(), 0.0001);
    }

    private static class NoopPetGateway extends AbstractFakePetGateway {
    }
}