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
import adopet.domain.shelterandpet.AgeYears;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetName;
import adopet.domain.shelterandpet.PetStatus;
import adopet.domain.shelterandpet.PetType;
import adopet.domain.shelterandpet.WeightKg;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.AdoptionRequestGateway;
import adopet.gateway.PetGateway;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeleteAdoptionRequestCommandHandlerTest {

  @Test
  void shouldDeletePendingRequestWithoutTouchingPet() {
    FakeAdoptionRequestGateway gateway = new FakeAdoptionRequestGateway(pendingRequest());
    SpyPetGateway petGateway = new SpyPetGateway();

    DeleteAdoptionRequestCommandHandler handler =
        new DeleteAdoptionRequestCommandHandler(gateway, petGateway);

    handler.execute(1L);

    assertEquals(1L, gateway.deletedId);
    assertEquals(0, petGateway.updateCalls);
  }

  @Test
  void shouldRestorePetAvailabilityWhenDeletingApprovedRequest() {
    FakeAdoptionRequestGateway gateway = new FakeAdoptionRequestGateway(approvedRequest());
    SpyPetGateway petGateway = new SpyPetGateway();

    DeleteAdoptionRequestCommandHandler handler =
        new DeleteAdoptionRequestCommandHandler(gateway, petGateway);

    handler.execute(1L);

    assertEquals(1L, gateway.deletedId);
    assertEquals(1, petGateway.updateCalls);
    assertEquals(PetStatus.AVAILABLE, petGateway.lastStatus);
  }

  @Test
  void shouldFailWhenApprovedRequestPetDoesNotExist() {
    FakeAdoptionRequestGateway gateway = new FakeAdoptionRequestGateway(approvedRequest());
    SpyPetGateway petGateway = new SpyPetGateway();
    petGateway.findPet = false;

    DeleteAdoptionRequestCommandHandler handler =
        new DeleteAdoptionRequestCommandHandler(gateway, petGateway);

    assertThrows(EntityNotFoundException.class, () -> handler.execute(1L));
    assertTrue(gateway.deletedId == null);
  }

  @Test
  void shouldFailWhenIdIsNull() {
    DeleteAdoptionRequestCommandHandler handler =
        new DeleteAdoptionRequestCommandHandler(new FakeAdoptionRequestGateway(pendingRequest()), new SpyPetGateway());

    assertThrows(InvalidUserInputException.class, () -> handler.execute(null));
  }

  @Test
  void shouldFailWhenRequestDoesNotExist() {
    DeleteAdoptionRequestCommandHandler handler =
        new DeleteAdoptionRequestCommandHandler(new FakeAdoptionRequestGateway(null), new SpyPetGateway());

    assertThrows(EntityNotFoundException.class, () -> handler.execute(99L));
  }

  private static AdoptionRequest pendingRequest() {
    return baseRequest(AdoptionRequestStatus.PENDING);
  }

  private static AdoptionRequest approvedRequest() {
    return baseRequest(AdoptionRequestStatus.APPROVED);
  }

  private static AdoptionRequest baseRequest(AdoptionRequestStatus status) {
    return new AdoptionRequest(
        1L,
        10L,
        20L,
        new ApplicantName("Maria"),
        new ApplicantEmail("maria@email.com"),
        new ApplicantPhone("31999999999"),
        new ApplicantDocument("12345678900"),
        HousingType.HOUSE,
        true,
        new ReasonText("Quero adotar com responsabilidade e carinho."),
        status,
        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
        LocalDateTime.now(),
        LocalDateTime.now());
  }

  private static final class FakeAdoptionRequestGateway implements AdoptionRequestGateway {
    private final AdoptionRequest stored;
    private Long deletedId;

    private FakeAdoptionRequestGateway(AdoptionRequest stored) {
      this.stored = stored;
    }

    @Override
    public List<AdoptionRequest> listAdoptionRequests(String status, Long petId, Long shelterId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Optional<AdoptionRequest> findById(Long id) {
      return stored != null && stored.id().equals(id) ? Optional.of(stored) : Optional.empty();
    }

    @Override
    public AdoptionRequest registerAdoptionRequest(AdoptionRequest request) {
      throw new UnsupportedOperationException();
    }

    @Override
    public AdoptionRequest updateAdoptionRequest(Long id, AdoptionRequest request) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAdoptionRequest(Long id) {
      this.deletedId = id;
    }

    @Override
    public boolean existsActiveRequestForPetAndDocument(Long petId, String applicantDocument) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }
  }

  private static final class SpyPetGateway implements PetGateway {
    private boolean findPet = true;
    private int updateCalls;
    private PetStatus lastStatus;

    @Override
    public List<Pet> listPets(String shelterIdOrName) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Pet> findById(Long petId) {
      if (!findPet) {
        return Optional.empty();
      }
      return Optional.of(
          new Pet(
              petId,
              PetType.CACHORRO,
              new PetName("Rex"),
              "SRD",
              new AgeYears(2),
              "Caramelo",
              new WeightKg(10.0),
              PetStatus.ADOPTED));
    }

    @Override
    public Optional<Long> findShelterIdByPetId(Long petId) {
      return Optional.of(20L);
    }

    @Override
    public Pet registerPet(String shelterIdOrName, Pet pet) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Pet updatePet(Long petId, Pet updated) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Pet updatePetStatus(Long petId, PetStatus status) {
      updateCalls++;
      lastStatus = status;
      return new Pet(
          petId,
          PetType.CACHORRO,
          new PetName("Rex"),
          "SRD",
          new AgeYears(2),
          "Caramelo",
          new WeightKg(10.0),
          status);
    }

    @Override
    public void deletePet(Long petId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int importPets(String shelterIdOrName, java.io.InputStream csvInputStream) {
      throw new UnsupportedOperationException();
    }

    @Override
    public byte[] exportPets(String shelterIdOrName) {
      throw new UnsupportedOperationException();
    }
  }
}
