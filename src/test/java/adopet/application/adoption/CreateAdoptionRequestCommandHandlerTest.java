package adopet.application.adoption;

import adopet.domain.adoption.EligibilityAnalysis;
import adopet.domain.shelterandpet.AgeYears;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetName;
import adopet.domain.shelterandpet.PetStatus;
import adopet.domain.shelterandpet.PetType;
import adopet.domain.shelterandpet.WeightKg;
import adopet.exception.DuplicateEntityException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.PetGateway;
import adopet.infrastructure.memory.InMemoryAdoptionRequestGateway;
import adopet.infrastructure.memory.ProgrammableEligibilityAnalysisGateway;
import adopet.infrastructure.memory.ProgrammableNotificationGateway;
import adopet.infrastructure.memory.ScenarioMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CreateAdoptionRequestCommandHandlerTest {

    private InMemoryAdoptionRequestGateway gateway;
    private ProgrammableEligibilityAnalysisGateway eligibilityGateway;
    private ProgrammableNotificationGateway notificationGateway;
    private CreateAdoptionRequestCommandHandler handler;
    private PetGateway petGateway;

    @BeforeEach
    void setUp() {
        gateway = new InMemoryAdoptionRequestGateway();
        eligibilityGateway = new ProgrammableEligibilityAnalysisGateway();
        notificationGateway = new ProgrammableNotificationGateway();
        petGateway = new StubPetGateway();

        handler = new CreateAdoptionRequestCommandHandler(
                gateway,
                eligibilityGateway,
                notificationGateway,
                petGateway
        );
    }

    @Test
    void shouldCreateRequestSuccessfully() {
        eligibilityGateway.setScenarioMode(ScenarioMode.SUCCESS);
        eligibilityGateway.setSuccessResult(EligibilityAnalysis.ELIGIBLE);

        AdoptionRequestOperationResult result = handler.execute(
                1L, 2L,
                "Maria da Silva",
                "maria@email.com",
                "31999999999",
                "12345678900",
                "HOUSE",
                true,
                "Quero adotar de forma responsável e segura."
        );

        assertNotNull(result.adoptionRequest().id());
        assertEquals(EligibilityAnalysis.ELIGIBLE, result.analysisResult());
        assertEquals(AnalysisExecutionStatus.SUCCESS, result.analysisExecutionStatus());
        assertTrue(result.notificationSent());
    }

    @Test
    void shouldFallbackGracefullyWhenEligibilityFails() {
        eligibilityGateway.setScenarioMode(ScenarioMode.TIMEOUT);

        AdoptionRequestOperationResult result = handler.execute(
                1L, 2L,
                "Maria da Silva",
                "maria@email.com",
                "31999999999",
                "12345678900",
                "HOUSE",
                true,
                "Quero adotar de forma responsável e segura."
        );

        assertEquals(EligibilityAnalysis.UNAVAILABLE, result.analysisResult());
        assertEquals(AnalysisExecutionStatus.FAILED, result.analysisExecutionStatus());
    }

    @Test
    void shouldKeepConsistentStateWhenNotificationFails() {
        notificationGateway.setScenarioMode(ScenarioMode.NETWORK_ERROR);

        AdoptionRequestOperationResult result = handler.execute(
                1L, 2L,
                "Maria da Silva",
                "maria@email.com",
                "31999999999",
                "12345678900",
                "HOUSE",
                true,
                "Quero adotar de forma responsável e segura."
        );

        assertNotNull(result.adoptionRequest().id());
        assertFalse(result.notificationSent());
        assertTrue(gateway.findById(result.adoptionRequest().id()).isPresent());
    }

    @Test
    void shouldRejectDuplicateActiveRequestForSamePetAndDocument() {
        handler.execute(
                1L, 2L,
                "Maria da Silva",
                "maria@email.com",
                "31999999999",
                "12345678900",
                "HOUSE",
                true,
                "Quero adotar de forma responsável e segura."
        );

        assertThrows(DuplicateEntityException.class, () ->
                handler.execute(
                        1L, 2L,
                        "Maria da Silva",
                        "maria2@email.com",
                        "31999999998",
                        "12345678900",
                        "HOUSE",
                        true,
                        "Quero adotar de forma responsável e segura."
                )
        );
    }

    @Test
    void shouldFailWhenPetIdIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        null, 2L,
                        "Maria", "maria@email.com", "31999999999", "12345678900",
                        "HOUSE", true, "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenPetIdIsInvalid() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        0L, 2L,
                        "Maria", "maria@email.com", "31999999999", "12345678900",
                        "HOUSE", true, "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenShelterIdIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, null,
                        "Maria", "maria@email.com", "31999999999", "12345678900",
                        "HOUSE", true, "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenShelterIdIsInvalid() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, 0L,
                        "Maria", "maria@email.com", "31999999999", "12345678900",
                        "HOUSE", true, "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenApplicantNameIsBlank() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, 2L,
                        "   ", "maria@email.com", "31999999999", "12345678900",
                        "HOUSE", true, "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenApplicantEmailIsBlank() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, 2L,
                        "Maria", "   ", "31999999999", "12345678900",
                        "HOUSE", true, "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenApplicantPhoneIsBlank() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, 2L,
                        "Maria", "maria@email.com", "   ", "12345678900",
                        "HOUSE", true, "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenApplicantDocumentIsBlank() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, 2L,
                        "Maria", "maria@email.com", "31999999999", "   ",
                        "HOUSE", true, "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenHousingTypeIsBlank() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, 2L,
                        "Maria", "maria@email.com", "31999999999", "12345678900",
                        "   ", true, "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenReasonIsBlank() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, 2L,
                        "Maria", "maria@email.com", "31999999999", "12345678900",
                        "HOUSE", true, "   "
                )
        );
    }

    @Test
    void shouldFailWhenApplicantNameIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, 2L,
                        null,
                        "maria@email.com",
                        "31999999999",
                        "12345678900",
                        "HOUSE",
                        true,
                        "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenApplicantEmailIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, 2L,
                        "Maria",
                        null,
                        "31999999999",
                        "12345678900",
                        "HOUSE",
                        true,
                        "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenApplicantPhoneIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, 2L,
                        "Maria",
                        "maria@email.com",
                        null,
                        "12345678900",
                        "HOUSE",
                        true,
                        "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenApplicantDocumentIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, 2L,
                        "Maria",
                        "maria@email.com",
                        "31999999999",
                        null,
                        "HOUSE",
                        true,
                        "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenHousingTypeIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, 2L,
                        "Maria",
                        "maria@email.com",
                        "31999999999",
                        "12345678900",
                        null,
                        true,
                        "Quero adotar com responsabilidade."
                )
        );
    }

    @Test
    void shouldFailWhenReasonIsNull() {
        assertThrows(InvalidUserInputException.class, () ->
                handler.execute(
                        1L, 2L,
                        "Maria",
                        "maria@email.com",
                        "31999999999",
                        "12345678900",
                        "HOUSE",
                        true,
                        null
                )
        );
    }

    private static class StubPetGateway implements PetGateway {

        @Override
        public List<Pet> listPets(String shelterIdOrName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Pet> findById(Long petId) {
            if (petId == null || petId <= 0) {
                return Optional.empty();
            }

            return Optional.of(new Pet(
                    petId,
                    PetType.CACHORRO,
                    new PetName("Rex"),
                    "Vira-lata",
                    new AgeYears(2),
                    "Caramelo",
                    new WeightKg(10.5),
                    PetStatus.AVAILABLE
            ));
        }

        @Override
        public Optional<Long> findShelterIdByPetId(Long petId) {
            if (petId == null || petId <= 0) {
                return Optional.empty();
            }
            return Optional.of(2L);
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

        @Override
        public Pet updatePetStatus(Long petId, PetStatus status) {
            return new Pet(
                    petId,
                    PetType.CACHORRO,
                    new PetName("Rex"),
                    "Vira-lata",
                    new AgeYears(2),
                    "Caramelo",
                    new WeightKg(10.5),
                    status
            );
        }
    }
}
