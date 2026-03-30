package adopet.application.adoption;

import adopet.domain.adoption.AdoptionRequest;
import adopet.gateway.AdoptionRequestGateway;

import java.util.List;

public class ListAdoptionRequestsQuery {

    private final AdoptionRequestGateway adoptionRequestGateway;

    public ListAdoptionRequestsQuery(AdoptionRequestGateway adoptionRequestGateway) {
        this.adoptionRequestGateway = adoptionRequestGateway;
    }

    public List<AdoptionRequest> execute(String status, Long petId, Long shelterId) {
        return adoptionRequestGateway.listAdoptionRequests(status, petId, shelterId);
    }
}