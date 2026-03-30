package adopet.application.shelterandpet;

import adopet.exception.InvalidUserInputException;
import adopet.gateway.PetGateway;

import java.io.InputStream;

public class ImportShelterPetsCommandHandler {

    private final PetGateway petGateway;

    public ImportShelterPetsCommandHandler(PetGateway petGateway) {
        this.petGateway = petGateway;
    }

    public int execute(String shelterIdOrName, String originalFilename, InputStream csvInputStream) {
        if (shelterIdOrName == null || shelterIdOrName.isBlank()) {
            throw new InvalidUserInputException("Id ou nome do abrigo não pode ser vazio.");
        }
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new InvalidUserInputException("O arquivo CSV é obrigatório.");
        }
        if (!originalFilename.trim().toLowerCase().endsWith(".csv")) {
            throw new InvalidUserInputException("O arquivo enviado deve possuir extensão .csv.");
        }
        if (csvInputStream == null) {
            throw new InvalidUserInputException("O conteúdo do arquivo CSV é obrigatório.");
        }
        return petGateway.importPets(shelterIdOrName.trim(), csvInputStream);
    }
}
