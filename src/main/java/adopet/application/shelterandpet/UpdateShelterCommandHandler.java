package adopet.application.shelterandpet;

import adopet.domain.shelterandpet.Email;
import adopet.domain.shelterandpet.PhoneNumber;
import adopet.domain.shelterandpet.Shelter;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.ShelterGateway;

public class UpdateShelterCommandHandler {

    private final ShelterGateway shelterGateway;

    public UpdateShelterCommandHandler(ShelterGateway shelterGateway) {
        this.shelterGateway = shelterGateway;
    }

    public Shelter execute(Long id, String name, String phoneNumber, String email) {
        if (id == null) throw new InvalidUserInputException("Id do abrigo não pode ser vazio.");
        if (name == null || name.isBlank()) throw new InvalidUserInputException("Nome do abrigo não pode ser vazio.");
        if (phoneNumber == null || phoneNumber.isBlank()) throw new InvalidUserInputException("Telefone do abrigo não pode ser vazio.");
        if (email == null || email.isBlank()) throw new InvalidUserInputException("Email do abrigo não pode ser vazio.");

        return shelterGateway.updateShelter(
                id,
                name.trim(),
                new PhoneNumber(phoneNumber.trim()),
                new Email(email.trim())
        );
    }
}