package adopet.application.shelterandpet;

import adopet.exception.InvalidUserInputException;
import adopet.gateway.PetGateway;

public class DeletePetCommandHandler {

    private final PetGateway petGateway;

    public DeletePetCommandHandler(PetGateway petGateway) {
        this.petGateway = petGateway;
    }

    public void execute(Long petId) {
        if (petId == null) throw new InvalidUserInputException("Id do pet não pode ser vazio.");
        petGateway.deletePet(petId);
    }
}