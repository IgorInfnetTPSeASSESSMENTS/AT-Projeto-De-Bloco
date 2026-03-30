package adopet.application.shelterandpet;

import adopet.domain.shelterandpet.AgeYears;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetName;
import adopet.domain.shelterandpet.PetType;
import adopet.domain.shelterandpet.WeightKg;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.PetGateway;

public class UpdatePetCommandHandler {

    private final PetGateway petGateway;

    public UpdatePetCommandHandler(PetGateway petGateway) {
        this.petGateway = petGateway;
    }

    public Pet execute(Long petId, String type, String name, String breed, int age, String color, double weight) {
        if (petId == null) {
            throw new InvalidUserInputException("Id do pet não pode ser vazio.");
        }
        if (type == null || type.isBlank()) {
            throw new InvalidUserInputException("Tipo não pode ser vazio.");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidUserInputException("Nome do pet não pode ser vazio.");
        }
        if (breed == null || breed.isBlank()) {
            throw new InvalidUserInputException("Raça não pode ser vazia.");
        }
        if (color == null || color.isBlank()) {
            throw new InvalidUserInputException("Cor não pode ser vazia.");
        }

        Pet existing = petGateway.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado (id=" + petId + ")."));

        Pet updated = new Pet(
                petId,
                PetType.from(type.trim()),
                new PetName(name.trim()),
                breed.trim(),
                new AgeYears(age),
                color.trim(),
                new WeightKg(weight),
                existing.status()
        );

        return petGateway.updatePet(petId, updated);
    }
}
