package adopet.application.adoption;

import adopet.domain.adoption.AdoptionRequest;
import adopet.domain.adoption.AdoptionRequestStatus;
import adopet.domain.adoption.ApplicantDocument;
import adopet.domain.adoption.ApplicantEmail;
import adopet.domain.adoption.ApplicantName;
import adopet.domain.adoption.ApplicantPhone;
import adopet.domain.adoption.EligibilityAnalysis;
import adopet.domain.adoption.HousingType;
import adopet.domain.adoption.ReasonText;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidStateTransitionException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.AdoptionRequestGateway;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAdoptionRequestCommandHandlerTest {

  @Test
  void shouldUpdatePendingRequest() {
    InMemoryAdoptionRequestGateway gateway = new InMemoryAdoptionRequestGateway(sampleRequest(1L, AdoptionRequestStatus.PENDING));
    UpdateAdoptionRequestCommandHandler handler = new UpdateAdoptionRequestCommandHandler(gateway);

    AdoptionRequest updated = handler.execute(
        1L,
        2L,
        20L,
        "Maria Souza",
        "maria@email.com",
        "31988887777",
        "12345678900",
        "CASA",
        true,
        "Tenho espaço e experiência com adoção responsável."
    );

    assertEquals(2L, updated.petId());
    assertEquals(20L, updated.shelterId());
    assertEquals("Maria Souza", updated.applicantName().value());
    assertEquals("maria@email.com", updated.applicantEmail().value());
    assertEquals("31988887777", updated.applicantPhone().value());
    assertEquals("12345678900", updated.applicantDocument().value());
    assertEquals(HousingType.HOUSE, updated.housingType());
    assertEquals(true, updated.hasOtherPets());
    assertEquals("Tenho espaço e experiência com adoção responsável.", updated.reason().value());
    assertEquals(AdoptionRequestStatus.PENDING, updated.status());
  }

  @Test
  void shouldRejectNullId() {
    UpdateAdoptionRequestCommandHandler handler = new UpdateAdoptionRequestCommandHandler(new InMemoryAdoptionRequestGateway(sampleRequest(1L, AdoptionRequestStatus.PENDING)));

    InvalidUserInputException exception = assertThrows(
        InvalidUserInputException.class,
        () -> handler.execute(null, 1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900", "CASA", false, "Motivo suficientemente longo.")
    );

    assertEquals("Id da solicitação é obrigatório.", exception.getMessage());
  }

  @Test
  void shouldRejectWhenRequestDoesNotExist() {
    UpdateAdoptionRequestCommandHandler handler = new UpdateAdoptionRequestCommandHandler(new InMemoryAdoptionRequestGateway(null));

    EntityNotFoundException exception = assertThrows(
        EntityNotFoundException.class,
        () -> handler.execute(1L, 1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900", "CASA", false, "Motivo suficientemente longo.")
    );

    assertEquals("Solicitação de adoção não encontrada (id=1).", exception.getMessage());
  }

  @Test
  void shouldRejectUpdateForApprovedRequest() {
    UpdateAdoptionRequestCommandHandler handler = new UpdateAdoptionRequestCommandHandler(
        new InMemoryAdoptionRequestGateway(sampleRequest(1L, AdoptionRequestStatus.APPROVED))
    );

    InvalidStateTransitionException exception = assertThrows(
        InvalidStateTransitionException.class,
        () -> handler.execute(1L, 1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900", "CASA", false, "Motivo suficientemente longo.")
    );

    assertEquals("Somente solicitações pendentes ou em análise podem ser editadas.", exception.getMessage());
  }

  private static AdoptionRequest sampleRequest(Long id, AdoptionRequestStatus status) {
    LocalDateTime createdAt = LocalDateTime.now().minusMinutes(1);
    return new AdoptionRequest(
        id,
        1L,
        10L,
        new ApplicantName("João Silva"),
        new ApplicantEmail("joao@email.com"),
        new ApplicantPhone("31999999999"),
        new ApplicantDocument("98765432100"),
        HousingType.APARTMENT,
        false,
        new ReasonText("Quero adotar um pet e tenho tempo disponível."),
        status,
        EligibilityAnalysis.NOT_REQUESTED,
        createdAt,
        createdAt
    );
  }

  private static final class InMemoryAdoptionRequestGateway implements AdoptionRequestGateway {

    private AdoptionRequest stored;

    private InMemoryAdoptionRequestGateway(AdoptionRequest stored) {
      this.stored = stored;
    }

    @Override
    public java.util.List<AdoptionRequest> listAdoptionRequests(String status, Long petId, Long shelterId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Optional<AdoptionRequest> findById(Long id) {
      return stored != null && stored.id().equals(id) ? Optional.of(stored) : Optional.empty();
    }

    @Override
    public AdoptionRequest registerAdoptionRequest(AdoptionRequest adoptionRequest) {
      throw new UnsupportedOperationException();
    }

    @Override
    public AdoptionRequest updateAdoptionRequest(Long id, AdoptionRequest updated) {
      stored = updated;
      return updated;
    }

    @Override
    public void deleteAdoptionRequest(Long id) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean existsActiveRequestForPetAndDocument(Long petId, String applicantDocument) {
      return false;
    }

    @Override
    public void clear() {
      stored = null;
    }
  }
}
