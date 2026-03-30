package adopet.application.shelterandpet;

import adopet.exception.InvalidUserInputException;
import adopet.gateway.PetGateway;

public class ExportShelterPetsCommandHandler {

    private final PetGateway petGateway;

    public ExportShelterPetsCommandHandler(PetGateway petGateway) {
        this.petGateway = petGateway;
    }

    public byte[] execute(String shelterIdOrName) {
        if (shelterIdOrName == null || shelterIdOrName.isBlank()) {
            throw new InvalidUserInputException("Id ou nome do abrigo não pode ser vazio.");
        }
        return petGateway.exportPets(shelterIdOrName.trim());
    }
}
