package adopet.infrastructure.memory;

import adopet.domain.adoption.AdoptionRequest;
import adopet.domain.adoption.AdoptionRequestStatus;
import adopet.exception.EntityNotFoundException;
import adopet.gateway.AdoptionRequestGateway;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryAdoptionRequestGateway implements AdoptionRequestGateway {

    private final Map<Long, AdoptionRequest> byId = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public List<AdoptionRequest> listAdoptionRequests(String status, Long petId, Long shelterId) {
        return byId.values().stream()
                .filter(request -> status == null || status.isBlank() || request.status() == AdoptionRequestStatus.from(status))
                .filter(request -> petId == null || Objects.equals(request.petId(), petId))
                .filter(request -> shelterId == null || Objects.equals(request.shelterId(), shelterId))
                .toList();
    }

    @Override
    public Optional<AdoptionRequest> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public AdoptionRequest registerAdoptionRequest(AdoptionRequest adoptionRequest) {
        long id = sequence.getAndIncrement();
        AdoptionRequest created = new AdoptionRequest(
                id,
                adoptionRequest.petId(),
                adoptionRequest.shelterId(),
                adoptionRequest.applicantName(),
                adoptionRequest.applicantEmail(),
                adoptionRequest.applicantPhone(),
                adoptionRequest.applicantDocument(),
                adoptionRequest.housingType(),
                adoptionRequest.hasOtherPets(),
                adoptionRequest.reason(),
                adoptionRequest.status(),
                adoptionRequest.eligibilityAnalysis(),
                adoptionRequest.createdAt(),
                adoptionRequest.updatedAt()
        );
        byId.put(id, created);
        return created;
    }

    @Override
    public AdoptionRequest updateAdoptionRequest(Long id, AdoptionRequest updated) {
        AdoptionRequest existing = byId.get(id);
        if (existing == null) {
            throw new EntityNotFoundException("Solicitação de adoção não encontrada (id=" + id + ").");
        }

        AdoptionRequest merged = new AdoptionRequest(
                existing.id(),
                updated.petId(),
                updated.shelterId(),
                updated.applicantName(),
                updated.applicantEmail(),
                updated.applicantPhone(),
                updated.applicantDocument(),
                updated.housingType(),
                updated.hasOtherPets(),
                updated.reason(),
                updated.status(),
                updated.eligibilityAnalysis(),
                existing.createdAt(),
                updated.updatedAt()
        );

        byId.put(existing.id(), merged);
        return merged;
    }

    @Override
    public void deleteAdoptionRequest(Long id) {
        AdoptionRequest removed = byId.remove(id);
        if (removed == null) {
            throw new EntityNotFoundException("Solicitação de adoção não encontrada (id=" + id + ").");
        }
    }

    @Override
    public boolean existsActiveRequestForPetAndDocument(Long petId, String applicantDocument) {
        return byId.values().stream()
                .filter(request -> Objects.equals(request.petId(), petId))
                .filter(request -> Objects.equals(request.applicantDocument().value(), applicantDocument))
                .anyMatch(request -> request.status() == AdoptionRequestStatus.PENDING
                        || request.status() == AdoptionRequestStatus.UNDER_REVIEW
                        || request.status() == AdoptionRequestStatus.APPROVED);
    }

    @Override
    public void clear() {
        byId.clear();
        sequence.set(1);
    }
}