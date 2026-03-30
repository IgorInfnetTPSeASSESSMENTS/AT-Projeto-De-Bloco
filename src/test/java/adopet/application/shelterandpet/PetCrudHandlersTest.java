package adopet.application.shelterandpet;

import adopet.domain.shelterandpet.Email;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PhoneNumber;
import adopet.domain.shelterandpet.Shelter;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.infrastructure.memory.InMemoryPetGateway;
import adopet.infrastructure.memory.InMemoryShelterGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PetCrudHandlersTest {

    @Test
    void listShelterPetsQueryShouldRejectBlank() {
        InMemoryShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        ListShelterPetsQuery query = new ListShelterPetsQuery(pets);

        assertThrows(InvalidUserInputException.class, () -> query.execute(" "));
    }

    @Test
    void registerPetHandlerShouldCreate() {
        InMemoryShelterGateway shelters = new InMemoryShelterGateway();
        var s = shelters.registerShelter(new Shelter(null, "Abrigo X", new PhoneNumber("31999999999"), new Email("a@b.com")));
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        RegisterPetCommandHandler handler = new RegisterPetCommandHandler(pets);

        Pet created = handler.execute(
                String.valueOf(s.id()),
                "GATO",
                "Mimi",
                "SRD",
                2,
                "Branco",
                3.2
        );

        assertNotNull(created.id());
        assertEquals("Mimi", created.name().value());
        assertEquals(1, pets.listPets(String.valueOf(s.id())).size());
    }

    @Test
    void registerPetHandlerShouldRejectInvalidInputs() {
        InMemoryShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        RegisterPetCommandHandler handler = new RegisterPetCommandHandler(pets);

        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(" ", "GATO", "Mimi", "SRD", 2, "Branco", 3.2)
        );
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute("1", " ", "Mimi", "SRD", 2, "Branco", 3.2)
        );
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute("1", "GATO", " ", "SRD", 2, "Branco", 3.2)
        );
    }

    @Test
    void updatePetHandlerShouldThrowWhenNotFound() {
        InMemoryShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        UpdatePetCommandHandler handler = new UpdatePetCommandHandler(pets);

        assertThrows(EntityNotFoundException.class, () ->
                handler.execute(999L, "GATO", "Mimi", "SRD", 2, "Branco", 3.2)
        );
    }

    @Test
    void deletePetHandlerShouldThrowWhenNotFound() {
        InMemoryShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        DeletePetCommandHandler handler = new DeletePetCommandHandler(pets);

        assertThrows(EntityNotFoundException.class, () -> handler.execute(1L));
    }

    @Test
    void importHandlerShouldRejectBlankInputs() {
        InMemoryShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        ImportShelterPetsCommandHandler handler = new ImportShelterPetsCommandHandler(pets);

        ByteArrayInputStream stream = new ByteArrayInputStream("x".getBytes());

        assertThrows(InvalidUserInputException.class, () -> handler.execute(" ", "pets.csv", stream));
        assertThrows(InvalidUserInputException.class, () -> handler.execute("1", " ", stream));
    }

    @Test
    void importHandlerShouldImport(@TempDir Path tempDir) throws Exception {
        InMemoryShelterGateway shelters = new InMemoryShelterGateway();
        var s = shelters.registerShelter(new Shelter(null, "Abrigo X", new PhoneNumber("31999999999"), new Email("a@b.com")));
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        Path csv = tempDir.resolve("pets.csv");
        java.nio.file.Files.writeString(csv, "GATO,Mimi,SRD,2,Branco,3.2\n");

        ImportShelterPetsCommandHandler handler = new ImportShelterPetsCommandHandler(pets);

        int count = handler.execute(
                String.valueOf(s.id()),
                "pets.csv",
                new ByteArrayInputStream(java.nio.file.Files.readAllBytes(csv))
        );
        assertEquals(1, count);
    }
}
