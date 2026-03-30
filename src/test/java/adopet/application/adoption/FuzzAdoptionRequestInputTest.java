package adopet.application.adoption;

import adopet.domain.shelterandpet.AgeYears;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetName;
import adopet.domain.shelterandpet.PetStatus;
import adopet.domain.shelterandpet.PetType;
import adopet.domain.shelterandpet.WeightKg;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.PetGateway;
import adopet.infrastructure.memory.InMemoryAdoptionRequestGateway;
import adopet.infrastructure.memory.ProgrammableEligibilityAnalysisGateway;
import adopet.infrastructure.memory.ProgrammableNotificationGateway;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FuzzAdoptionRequestInputTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "<script>alert('x')</script>",
            "' OR '1'='1",
            "𓀀𓀁𓀂",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    })
    void shouldRejectOrSafelyHandleMalformedApplicantName(String raw) {
        CreateAdoptionRequestCommandHandler handler = new CreateAdoptionRequestCommandHandler(
                new InMemoryAdoptionRequestGateway(),
                new ProgrammableEligibilityAnalysisGateway(),
                new ProgrammableNotificationGateway(),
                new StubPetGateway()
        );

        if (raw.isBlank() || raw.length() > 100) {
            assertThrows(InvalidUserInputException.class, () -> handler.execute(
                    1L,
                    2L,
                    raw,
                    "maria@email.com",
                    "31999999999",
                    "12345678900",
                    "HOUSE",
                    false,
                    "Quero adotar com responsabilidade e carinho."
            ));
        } else {
            assertDoesNotThrow(() -> {
                try {
                    handler.execute(
                            1L,
                            2L,
                            raw,
                            "maria@email.com",
                            "31999999999",
                            "12345678900",
                            "HOUSE",
                            false,
                            "Quero adotar com responsabilidade e carinho."
                    );
                } catch (InvalidUserInputException ignored) {
                }
            });
        }
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
