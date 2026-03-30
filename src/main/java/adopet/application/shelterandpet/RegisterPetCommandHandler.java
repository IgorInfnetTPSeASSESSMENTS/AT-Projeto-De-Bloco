package adopet.application.shelterandpet;

import adopet.domain.shelterandpet.AgeYears;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetName;
import adopet.domain.shelterandpet.PetStatus;
import adopet.domain.shelterandpet.PetType;
import adopet.domain.shelterandpet.WeightKg;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.PetGateway;

public class RegisterPetCommandHandler {

  private final PetGateway petGateway;

  public RegisterPetCommandHandler(PetGateway petGateway) {
    this.petGateway = petGateway;
  }

  public Pet execute(
      String shelterIdOrName,
      String type,
      String name,
      String breed,
      int age,
      String color,
      double weight) {
    if (shelterIdOrName == null || shelterIdOrName.isBlank())
      throw new InvalidUserInputException("Id ou nome do abrigo não pode ser vazio.");
    if (type == null || type.isBlank())
      throw new InvalidUserInputException("Tipo não pode ser vazio.");
    if (name == null || name.isBlank())
      throw new InvalidUserInputException("Nome do pet não pode ser vazio.");
    if (breed == null || breed.isBlank())
      throw new InvalidUserInputException("Raça não pode ser vazia.");
    if (color == null || color.isBlank())
      throw new InvalidUserInputException("Cor não pode ser vazia.");

    Pet pet =
        new Pet(
            null,
            PetType.from(type.trim()),
            new PetName(name.trim()),
            breed.trim(),
            new AgeYears(age),
            color.trim(),
            new WeightKg(weight),
            PetStatus.AVAILABLE);

    return petGateway.registerPet(shelterIdOrName.trim(), pet);
  }
}
