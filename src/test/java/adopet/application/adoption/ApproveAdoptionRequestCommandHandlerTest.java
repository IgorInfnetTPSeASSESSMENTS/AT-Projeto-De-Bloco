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
import adopet.infrastructure.memory.ProgrammableNotificationGateway;
import adopet.infrastructure.memory.ScenarioMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ApproveAdoptionRequestCommandHandlerTest {

    private InMemoryAdoptionRequestGateway gateway;
    private ProgrammableNotificationGateway notificationGateway;
    private SpyPetGateway petGateway;
    private ApproveAdoptionRequestCommandHandler handler;
    private AdoptionRequest created;

    @BeforeEach
    void setUp() {
        gateway = new InMemoryAdoptionRequestGateway();
        notificationGateway = new ProgrammableNotificationGateway();
        petGateway = new SpyPetGateway();
        handler = new ApproveAdoptionRequestCommandHandler(gateway, notificationGateway, petGateway);

        created = gateway.registerAdoptionRequest(
                AdoptionRequest.newRequest(
                        1L, 10L,
                        new ApplicantName("Maria da Silva"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade e carinho."),
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW
                )
        );
    }

    @Test
    void shouldApproveRequestAndSendNotification() {
        AdoptionRequestOperationResult result = handler.execute(created.id());

        assertEquals(AdoptionRequestStatus.APPROVED, result.adoptionRequest().status());
        assertTrue(result.notificationSent());
        assertEquals(AnalysisExecutionStatus.SUCCESS, result.analysisExecutionStatus());
        assertEquals(created.eligibilityAnalysis(), result.analysisResult());

        assertEquals(created.petId(), petGateway.updatedPetId);
        assertEquals(PetStatus.ADOPTED, petGateway.updatedStatus);
    }

    @Test
    void shouldApproveEvenWhenNotificationFails() {
        notificationGateway.setScenarioMode(ScenarioMode.TIMEOUT);

        AdoptionRequestOperationResult result = handler.execute(created.id());

        assertEquals(AdoptionRequestStatus.APPROVED, result.adoptionRequest().status());
        assertFalse(result.notificationSent());

        assertEquals(created.petId(), petGateway.updatedPetId);
        assertEquals(PetStatus.ADOPTED, petGateway.updatedStatus);
    }

    @Test
    void shouldFailWhenIdIsNull() {
        assertThrows(InvalidUserInputException.class, () -> handler.execute(null));
    }

    @Test
    void shouldFailWhenRequestDoesNotExist() {
        assertThrows(EntityNotFoundException.class, () -> handler.execute(999L));
    }

    private static class SpyPetGateway implements PetGateway {

        private Long updatedPetId;
        private PetStatus updatedStatus;

        @Override
        public List<Pet> listPets(String shelterIdOrName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Pet> findById(Long petId) {
            if (petId == null) {
                return Optional.empty();
            }

            return Optional.of(new Pet(
                    petId,
                    PetType.CACHORRO,
                    new PetName("Rex"),
                    "SRD",
                    new AgeYears(2),
                    "Caramelo",
                    new WeightKg(10.0),
                    PetStatus.AVAILABLE
            ));
        }

        @Override
        public Optional<Long> findShelterIdByPetId(Long petId) {
            throw new UnsupportedOperationException();
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
            this.updatedPetId = petId;
            this.updatedStatus = status;

            return new Pet(
                    petId,
                    PetType.CACHORRO,
                    new PetName("Rex"),
                    "SRD",
                    new AgeYears(2),
                    "Caramelo",
                    new WeightKg(10.0),
                    status
            );
        }

        @Override
        public void deletePet(Long petId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int importPets(String shelterIdOrName, InputStream csvInputStream) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] exportPets(String shelterIdOrName) {
            throw new UnsupportedOperationException();
        }
    }
}
