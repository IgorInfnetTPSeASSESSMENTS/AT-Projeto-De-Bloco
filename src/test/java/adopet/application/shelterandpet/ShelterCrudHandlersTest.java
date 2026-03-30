package adopet.application.shelterandpet;

import adopet.domain.shelterandpet.Email;
import adopet.domain.shelterandpet.PhoneNumber;
import adopet.domain.shelterandpet.Shelter;
import adopet.exception.DuplicateEntityException;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.infrastructure.memory.InMemoryShelterGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShelterCrudHandlersTest {

    @Test
    void registerShouldReturnMessageWithId() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();
        RegisterShelterCommandHandler handler = new RegisterShelterCommandHandler(gw);

        Shelter msg = handler.execute("Abrigo X", "31999999999", "a@b.com");


        assertEquals(1, gw.listShelters().size());
    }

    @Test
    void registerShouldRejectBlankInputs() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();
        RegisterShelterCommandHandler handler = new RegisterShelterCommandHandler(gw);

        assertThrows(InvalidUserInputException.class, () -> handler.execute(" ", "31999999999", "a@b.com"));
        assertThrows(InvalidUserInputException.class, () -> handler.execute("Abrigo", " ", "a@b.com"));
        assertThrows(InvalidUserInputException.class, () -> handler.execute("Abrigo", "31999999999", " "));
    }

    @Test
    void updateShouldWork() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();
        Shelter created = gw.registerShelter(new Shelter(null, "A1", new PhoneNumber("31999999999"), new Email("a@b.com")));

        UpdateShelterCommandHandler handler = new UpdateShelterCommandHandler(gw);

        Shelter updated = handler.execute(created.id(), "A2", "31988888888", "b@b.com");

        assertEquals("A2", updated.name());
        assertEquals("A2", gw.findById(created.id()).orElseThrow().name());
    }

    @Test
    void updateShouldRejectInvalidInputAndNotFoundAndDuplicate() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();
        UpdateShelterCommandHandler handler = new UpdateShelterCommandHandler(gw);

        assertThrows(InvalidUserInputException.class, () -> handler.execute(null, "A", "1", "a@b.com"));

        assertThrows(EntityNotFoundException.class, () ->
                handler.execute(999L, "A", "31999999999", "a@b.com")
        );

        Shelter s1 = gw.registerShelter(new Shelter(null, "A1", new PhoneNumber("31999999999"), new Email("a@b.com")));
        Shelter s2 = gw.registerShelter(new Shelter(null, "A2", new PhoneNumber("31999999998"), new Email("c@d.com")));

        assertThrows(DuplicateEntityException.class, () ->
                handler.execute(s2.id(), "A1", "31999999998", "c@d.com")
        );
    }

    @Test
    void deleteShouldRemoveAndThrowWhenNotFound() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();
        Shelter s = gw.registerShelter(new Shelter(null, "A1", new PhoneNumber("31999999999"), new Email("a@b.com")));

        DeleteShelterCommandHandler handler = new DeleteShelterCommandHandler(gw);

        handler.execute(s.id());
        assertTrue(gw.listShelters().isEmpty());

        assertThrows(EntityNotFoundException.class, () -> handler.execute(s.id()));
    }
}