package adopet.application.adoption;

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
import adopet.infrastructure.memory.ProgrammableNotificationGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.io.InputStream;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CreateAdoptionRequestFuzzTest {

    private static final String CHAR_POOL =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" +
                    " !@#$%¨&*()_+-=[]{}|;':,./<>?`~" +
                    "çÇáàãâéêíóôõúüÁÀÃÂÉÊÍÓÔÕÚÜ" +
                    "<script>alert('x')</script>";

    private final SecureRandom random = new SecureRandom();

    private CreateAdoptionRequestCommandHandler handler;
    private InMemoryAdoptionRequestGateway gateway;

    @BeforeEach
    void setUp() {
        gateway = new InMemoryAdoptionRequestGateway();
        handler = new CreateAdoptionRequestCommandHandler(
                gateway,
                new ProgrammableEligibilityAnalysisGateway(),
                new ProgrammableNotificationGateway(),
                new StubPetGateway()
        );
    }

    @RepeatedTest(300)
    void shouldHandleRandomInputsWithoutCrashingUnexpectedly() {
        String applicantName = randomString(0, 140);
        String applicantEmail = randomEmailLikeString();
        String applicantPhone = randomString(0, 40);
        String applicantDocument = randomString(0, 40);
        String housingType = randomHousingTypeLikeString();
        String reason = randomString(0, 1200);

        Long petId = randomNullablePositiveOrInvalidLong();
        Long shelterId = randomNullablePositiveOrInvalidLong();
        boolean hasOtherPets = random.nextBoolean();

        try {
            AdoptionRequestOperationResult result = handler.execute(
                    petId,
                    shelterId,
                    applicantName,
                    applicantEmail,
                    applicantPhone,
                    applicantDocument,
                    housingType,
                    hasOtherPets,
                    reason
            );

            assertNotNull(result);
            assertNotNull(result.adoptionRequest());
            assertNotNull(result.analysisExecutionStatus());
            assertNotNull(result.analysisResult());
            assertTrue(result.adoptionRequest().id() != null && result.adoptionRequest().id() > 0);

        } catch (
                InvalidUserInputException
                | adopet.exception.DuplicateEntityException
                | EntityNotFoundException expected
        ) {
            assertNotNull(expected.getMessage());
            assertFalse(expected.getMessage().isBlank());
        } catch (RuntimeException unexpected) {
            fail("O fuzz test encontrou falha inesperada: " + unexpected.getClass().getName()
                    + " - " + unexpected.getMessage());
        }
    }

    private String randomString(int minLength, int maxLength) {
        int length = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            builder.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }

        return builder.toString();
    }

    private String randomEmailLikeString() {
        return switch (random.nextInt(6)) {
            case 0 -> "";
            case 1 -> randomString(1, 20);
            case 2 -> randomString(1, 10) + "@";
            case 3 -> "@" + randomString(1, 10);
            case 4 -> randomString(1, 10) + "@" + randomString(1, 8) + ".com";
            default -> randomString(1, 120);
        };
    }

    private String randomHousingTypeLikeString() {
        return switch (random.nextInt(8)) {
            case 0 -> "HOUSE";
            case 1 -> "APARTMENT";
            case 2 -> "FARM";
            case 3 -> "OTHER";
            case 4 -> "";
            case 5 -> " ";
            default -> randomString(1, 20);
        };
    }

    private Long randomNullablePositiveOrInvalidLong() {
        return switch (random.nextInt(6)) {
            case 0 -> null;
            case 1 -> 0L;
            case 2 -> -1L;
            case 3 -> 1L;
            case 4 -> 10L;
            default -> (long) random.nextInt(1000) + 1;
        };
    }

    private static class StubPetGateway implements PetGateway {

        @Override
        public List<Pet> listPets(String shelterIdOrName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Pet> findById(Long petId) {
            if (petId == null || petId != 1L) {
                return Optional.empty();
            }

            return Optional.of(new Pet(
                    1L,
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
            return petId != null && petId == 1L ? Optional.of(2L) : Optional.empty();
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
            throw new UnsupportedOperationException();
        }
    }
}
