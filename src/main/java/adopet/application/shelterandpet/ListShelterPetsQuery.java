package adopet.application.shelterandpet;


import adopet.domain.shelterandpet.Pet;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.PetGateway;

import java.util.List;

/*
 * Query para listar pets de um determinado abrigo. Valida a entrada e delega ao PetGateway.
 */
public class ListShelterPetsQuery {

    private final PetGateway petGateway;

    public ListShelterPetsQuery(PetGateway petGateway) {
        this.petGateway = petGateway;
    }

    public List<Pet> execute(String shelterIdOrName) {
        if (shelterIdOrName == null || shelterIdOrName.isBlank()) {
            throw new InvalidUserInputException("Id ou nome do abrigo não pode ser vazio.");
        }
        return petGateway.listPets(shelterIdOrName.trim());
    }
}