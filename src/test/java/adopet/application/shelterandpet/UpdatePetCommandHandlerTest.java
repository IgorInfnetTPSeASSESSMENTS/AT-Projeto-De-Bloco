package adopet.application.shelterandpet;

import adopet.domain.shelterandpet.AgeYears;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetName;
import adopet.domain.shelterandpet.PetStatus;
import adopet.domain.shelterandpet.PetType;
import adopet.domain.shelterandpet.WeightKg;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.fakes.AbstractFakePetGateway;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdatePetCommandHandlerTest {

  @Test
  void shouldUpdatePetPreservingStatus() {
    InMemoryUpdatePetGateway gateway = new InMemoryUpdatePetGateway(samplePet(1L, PetStatus.ADOPTED));
    UpdatePetCommandHandler handler = new UpdatePetCommandHandler(gateway);

    Pet updated = handler.execute(1L, "GATO", "Mimi", "Siamês", 3, "Branco", 4.2);

    assertEquals(PetType.GATO, updated.type());
    assertEquals("Mimi", updated.name().value());
    assertEquals("Siamês", updated.breed());
    assertEquals(3, updated.age().value());
    assertEquals("Branco", updated.color());
    assertEquals(4.2, updated.weight().value());
    assertEquals(PetStatus.ADOPTED, updated.status());
  }

  @Test
  void shouldRejectNullPetId() {
    UpdatePetCommandHandler handler = new UpdatePetCommandHandler(new InMemoryUpdatePetGateway(samplePet(1L, PetStatus.AVAILABLE)));

    InvalidUserInputException exception = assertThrows(
        InvalidUserInputException.class,
        () -> handler.execute(null, "GATO", "Mimi", "Siamês", 3, "Branco", 4.2)
    );

    assertEquals("Id do pet não pode ser vazio.", exception.getMessage());
  }

  @Test
  void shouldRejectBlankType() {
    UpdatePetCommandHandler handler = new UpdatePetCommandHandler(new InMemoryUpdatePetGateway(samplePet(1L, PetStatus.AVAILABLE)));

    InvalidUserInputException exception = assertThrows(
        InvalidUserInputException.class,
        () -> handler.execute(1L, " ", "Mimi", "Siamês", 3, "Branco", 4.2)
    );

    assertEquals("Tipo não pode ser vazio.", exception.getMessage());
  }

  @Test
  void shouldRejectBlankName() {
    UpdatePetCommandHandler handler = new UpdatePetCommandHandler(new InMemoryUpdatePetGateway(samplePet(1L, PetStatus.AVAILABLE)));

    InvalidUserInputException exception = assertThrows(
        InvalidUserInputException.class,
        () -> handler.execute(1L, "GATO", " ", "Siamês", 3, "Branco", 4.2)
    );

    assertEquals("Nome do pet não pode ser vazio.", exception.getMessage());
  }

  @Test
  void shouldRejectBlankBreed() {
    UpdatePetCommandHandler handler = new UpdatePetCommandHandler(new InMemoryUpdatePetGateway(samplePet(1L, PetStatus.AVAILABLE)));

    InvalidUserInputException exception = assertThrows(
        InvalidUserInputException.class,
        () -> handler.execute(1L, "GATO", "Mimi", " ", 3, "Branco", 4.2)
    );

    assertEquals("Raça não pode ser vazia.", exception.getMessage());
  }

  @Test
  void shouldRejectBlankColor() {
    UpdatePetCommandHandler handler = new UpdatePetCommandHandler(new InMemoryUpdatePetGateway(samplePet(1L, PetStatus.AVAILABLE)));

    InvalidUserInputException exception = assertThrows(
        InvalidUserInputException.class,
        () -> handler.execute(1L, "GATO", "Mimi", "Siamês", 3, " ", 4.2)
    );

    assertEquals("Cor não pode ser vazia.", exception.getMessage());
  }

  @Test
  void shouldRejectWhenPetDoesNotExist() {
    UpdatePetCommandHandler handler = new UpdatePetCommandHandler(new InMemoryUpdatePetGateway(null));

    EntityNotFoundException exception = assertThrows(
        EntityNotFoundException.class,
        () -> handler.execute(1L, "GATO", "Mimi", "Siamês", 3, "Branco", 4.2)
    );

    assertEquals("Pet não encontrado (id=1).", exception.getMessage());
  }

  private static Pet samplePet(Long id, PetStatus status) {
    return new Pet(
        id,
        PetType.CACHORRO,
        new PetName("Rex"),
        "Vira-lata",
        new AgeYears(2),
        "Caramelo",
        new WeightKg(10.5),
        status
    );
  }

  private static final class InMemoryUpdatePetGateway extends AbstractFakePetGateway {

    private Pet storedPet;

    private InMemoryUpdatePetGateway(Pet storedPet) {
      this.storedPet = storedPet;
    }

    @Override
    public Optional<Pet> findById(Long petId) {
      return storedPet != null && storedPet.id().equals(petId) ? Optional.of(storedPet) : Optional.empty();
    }

    @Override
    public Pet updatePet(Long petId, Pet updated) {
      storedPet = updated;
      return updated;
    }
  }
}
