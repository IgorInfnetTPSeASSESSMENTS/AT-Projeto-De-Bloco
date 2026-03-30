package adopet.gateway;

import adopet.domain.adoption.AdoptionRequest;

import java.util.List;
import java.util.Optional;

public interface AdoptionRequestGateway {
    List<AdoptionRequest> listAdoptionRequests(String status, Long petId, Long shelterId);
    Optional<AdoptionRequest> findById(Long id);
    AdoptionRequest registerAdoptionRequest(AdoptionRequest adoptionRequest);
    AdoptionRequest updateAdoptionRequest(Long id, AdoptionRequest updated);
    void deleteAdoptionRequest(Long id);
    boolean existsActiveRequestForPetAndDocument(Long petId, String applicantDocument);
    void clear();
}