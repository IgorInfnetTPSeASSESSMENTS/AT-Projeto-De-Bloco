package adopet.application.shelterandpet;

import adopet.exception.InvalidUserInputException;
import adopet.gateway.ShelterGateway;

public class DeleteShelterCommandHandler {

    private final ShelterGateway shelterGateway;

    public DeleteShelterCommandHandler(ShelterGateway shelterGateway) {
        this.shelterGateway = shelterGateway;
    }

    public void execute(Long id) {
        if (id == null) throw new InvalidUserInputException("Id do abrigo não pode ser vazio.");
        shelterGateway.deleteShelter(id);
    }
}