package adopet.application.shelterandpet;

import adopet.domain.shelterandpet.*;
import adopet.exception.InvalidUserInputException;
import adopet.fakes.AbstractFakePetGateway;
import adopet.gateway.PetGateway;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ListShelterPetsQueryTest {

    @Test
    void shouldRejectBlankShelterIdOrName() {
        PetGateway gateway = new AbstractFakePetGateway() {
            @Override public List<Pet> listPets(String shelterIdOrName) { return List.of(); }
            @Override public Optional<Pet> findById(Long petId) { return Optional.empty(); }
        };

        ListShelterPetsQuery query = new ListShelterPetsQuery(gateway);

        assertThrows(InvalidUserInputException.class, () -> query.execute("   "));
        assertThrows(InvalidUserInputException.class, () -> query.execute(null));
    }

    @Test
    void shouldTrimInputBeforeSendingToGateway() {
        PetGateway gateway = new AbstractFakePetGateway() {
            @Override public List<Pet> listPets(String shelterIdOrName) {
                assertEquals("1", shelterIdOrName); // cobre branch do trim
                return List.of(new Pet(
                        1L, PetType.GATO, new PetName("Mimi"), "SRD", new AgeYears(2), "Branco", new WeightKg(3.2), PetStatus.AVAILABLE
                ));
            }
            @Override public Optional<Pet> findById(Long petId) { return Optional.empty(); }
        };

        ListShelterPetsQuery query = new ListShelterPetsQuery(gateway);

        List<Pet> pets = query.execute(" 1 ");
        assertEquals(1, pets.size());
        assertEquals("Mimi", pets.get(0).name().value());
    }

    @Test
    void shouldPropagateGatewayException() {
        RuntimeException boom = new RuntimeException("boom");

        PetGateway gateway = new AbstractFakePetGateway() {
            @Override public List<Pet> listPets(String shelterIdOrName) { throw boom; }
            @Override public Optional<Pet> findById(Long petId) { return Optional.empty(); }
        };

        ListShelterPetsQuery query = new ListShelterPetsQuery(gateway);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> query.execute("1"));
        assertSame(boom, ex);
    }
}