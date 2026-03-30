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
import adopet.gateway.PetGateway;
import adopet.infrastructure.memory.InMemoryAdoptionRequestGateway;
import adopet.infrastructure.memory.ProgrammableEligibilityAnalysisGateway;
import adopet.infrastructure.memory.ScenarioMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RetryEligibilityAnalysisCommandHandlerTest {

  private InMemoryAdoptionRequestGateway gateway;
  private ProgrammableEligibilityAnalysisGateway eligibilityGateway;
  private SpyPetGateway petGateway;
  private RetryEligibilityAnalysisCommandHandler handler;
  private AdoptionRequest created;

  @BeforeEach
  void setUp() {
    gateway = new InMemoryAdoptionRequestGateway();
    eligibilityGateway = new ProgrammableEligibilityAnalysisGateway();
    petGateway = new SpyPetGateway();

    handler = new RetryEligibilityAnalysisCommandHandler(gateway, eligibilityGateway, petGateway);

    created =
        gateway.registerAdoptionRequest(
            AdoptionRequest.newRequest(
                1L,
                10L,
                new ApplicantName("Maria da Silva"),
                new ApplicantEmail("maria@email.com"),
                new ApplicantPhone("31999999999"),
                new ApplicantDocument("12345678900"),
                HousingType.HOUSE,
                true,
                new ReasonText("Quero adotar com responsabilidade e carinho."),
                EligibilityAnalysis.UNAVAILABLE));
  }

  @Test
  void shouldRetryAnalysisSuccessfully() {
    eligibilityGateway.setScenarioMode(ScenarioMode.SUCCESS);
    eligibilityGateway.setSuccessResult(EligibilityAnalysis.ELIGIBLE);

    AdoptionRequest result = handler.execute(created.id());

    assertEquals(EligibilityAnalysis.ELIGIBLE, result.eligibilityAnalysis());
    assertEquals(AdoptionRequestStatus.UNDER_REVIEW, result.status());
    assertEquals(1, petGateway.updateCalls);
    assertEquals(PetStatus.AVAILABLE, petGateway.lastStatus);
  }

  @Test
  void shouldFallbackToUnavailableWhenTimeoutOccurs() {
    eligibilityGateway.setScenarioMode(ScenarioMode.TIMEOUT);

    AdoptionRequest result = handler.execute(created.id());

    assertEquals(EligibilityAnalysis.UNAVAILABLE, result.eligibilityAnalysis());
    assertEquals(AdoptionRequestStatus.PENDING, result.status());
    assertEquals(1, petGateway.updateCalls);
  }

  @Test
  void shouldFallbackToUnavailableWhenNetworkErrorOccurs() {
    eligibilityGateway.setScenarioMode(ScenarioMode.NETWORK_ERROR);

    AdoptionRequest result = handler.execute(created.id());

    assertEquals(EligibilityAnalysis.UNAVAILABLE, result.eligibilityAnalysis());
    assertEquals(AdoptionRequestStatus.PENDING, result.status());
    assertEquals(1, petGateway.updateCalls);
  }

  @Test
  void shouldFallbackToUnavailableWhenServiceIsUnavailable() {
    eligibilityGateway.setScenarioMode(ScenarioMode.SERVICE_UNAVAILABLE);

    AdoptionRequest result = handler.execute(created.id());

    assertEquals(EligibilityAnalysis.UNAVAILABLE, result.eligibilityAnalysis());
    assertEquals(AdoptionRequestStatus.PENDING, result.status());
  }

  @Test
  void shouldFailWhenPetDoesNotExist() {
    petGateway.findPet = false;

    assertThrows(EntityNotFoundException.class, () -> handler.execute(created.id()));
  }

  @Test
  void shouldFailWhenIdIsNull() {
    assertThrows(InvalidUserInputException.class, () -> handler.execute(null));
  }

  @Test
  void shouldFailWhenRequestDoesNotExist() {
    assertThrows(EntityNotFoundException.class, () -> handler.execute(999L));
  }

  private static final class SpyPetGateway implements PetGateway {
    private boolean findPet = true;
    private int updateCalls;
    private PetStatus lastStatus;

    @Override
    public java.util.List<Pet> listPets(String shelterIdOrName) {
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
              PetStatus.AVAILABLE));
    }

    @Override
    public Optional<Long> findShelterIdByPetId(Long petId) {
      return Optional.of(10L);
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
