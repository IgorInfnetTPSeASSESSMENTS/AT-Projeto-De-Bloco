package adopet.application.adoption;

import adopet.domain.adoption.AdoptionRequest;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.AdoptionRequestGateway;

public class GetAdoptionRequestDetailsQuery {

    private final AdoptionRequestGateway adoptionRequestGateway;

    public GetAdoptionRequestDetailsQuery(AdoptionRequestGateway adoptionRequestGateway) {
        this.adoptionRequestGateway = adoptionRequestGateway;
    }

    public AdoptionRequest execute(Long id) {
        if (id == null) {
            throw new InvalidUserInputException("Id da solicitação é obrigatório.");
        }

        return adoptionRequestGateway.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de adoção não encontrada (id=" + id + ")."));
    }
}