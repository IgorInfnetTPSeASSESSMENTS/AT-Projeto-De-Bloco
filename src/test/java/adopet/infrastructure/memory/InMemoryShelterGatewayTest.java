package adopet.infrastructure.memory;

import adopet.domain.shelterandpet.Email;
import adopet.domain.shelterandpet.PhoneNumber;
import adopet.domain.shelterandpet.Shelter;
import adopet.exception.DuplicateEntityException;
import adopet.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryShelterGatewayTest {

    @Test
    void registerShouldAssignIdAndStore() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();

        Shelter created = gw.registerShelter(new Shelter(
                null, "Abrigo X", new PhoneNumber("31999999999"), new Email("a@b.com")
        ));

        assertNotNull(created.id());
        assertEquals("Abrigo X", created.name());

        List<Shelter> all = gw.listShelters();
        assertEquals(1, all.size());
        assertEquals(created.id(), all.get(0).id());
    }

    @Test
    void registerShouldRejectDuplicateName_caseInsensitive() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();

        gw.registerShelter(new Shelter(null, "Abrigo X", new PhoneNumber("31999999999"), new Email("a@b.com")));

        assertThrows(DuplicateEntityException.class, () ->
                gw.registerShelter(new Shelter(null, "  abrigo x  ", new PhoneNumber("31988888888"), new Email("x@y.com")))
        );
    }

    @Test
    void findByIdShouldReturnEmptyWhenNullOrMissing() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();

        assertTrue(gw.findById(null).isEmpty());
        assertTrue(gw.findById(1L).isEmpty());
    }

    @Test
    void findByNameShouldReturnEmptyWhenNullOrMissing() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();

        assertTrue(gw.findByName(null).isEmpty());
        assertTrue(gw.findByName("X").isEmpty());
    }

    @Test
    void findByNameShouldFindIgnoringSpaces() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();

        Shelter created = gw.registerShelter(new Shelter(null, "Abrigo X", new PhoneNumber("31999999999"), new Email("a@b.com")));

        Shelter found = gw.findByName("  Abrigo X ").orElseThrow();
        assertEquals(created.id(), found.id());
    }

    @Test
    void updateShouldUpdateFields() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();
        Shelter created = gw.registerShelter(new Shelter(null, "A1", new PhoneNumber("31999999999"), new Email("a@b.com")));

        Shelter updated = gw.updateShelter(
                created.id(),
                "A2",
                new PhoneNumber("31988888888"),
                new Email("b@b.com")
        );

        assertEquals(created.id(), updated.id());
        assertEquals("A2", updated.name());
        assertEquals("31988888888", updated.phoneNumber().value());
        assertEquals("b@b.com", updated.email().value());

        Shelter fromStore = gw.findById(created.id()).orElseThrow();
        assertEquals("A2", fromStore.name());
    }

    @Test
    void updateShouldRejectDuplicateName() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();
        Shelter s1 = gw.registerShelter(new Shelter(null, "A1", new PhoneNumber("31999999999"), new Email("a@b.com")));
        Shelter s2 = gw.registerShelter(new Shelter(null, "A2", new PhoneNumber("31999999998"), new Email("c@d.com")));

        assertThrows(DuplicateEntityException.class, () ->
                gw.updateShelter(s2.id(), "A1", s2.phoneNumber(), s2.email())
        );

        // branch: update mantendo o mesmo nome não deve acusar duplicidade
        assertDoesNotThrow(() ->
                gw.updateShelter(s1.id(), "A1", s1.phoneNumber(), s1.email())
        );
    }

    @Test
    void updateShouldThrowWhenNotFound() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();

        assertThrows(EntityNotFoundException.class, () ->
                gw.updateShelter(999L, "X", new PhoneNumber("31999999999"), new Email("a@b.com"))
        );
    }

    @Test
    void deleteShouldRemove() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();
        Shelter created = gw.registerShelter(new Shelter(null, "A1", new PhoneNumber("31999999999"), new Email("a@b.com")));

        gw.deleteShelter(created.id());

        assertTrue(gw.listShelters().isEmpty());
        assertTrue(gw.findById(created.id()).isEmpty());
        assertTrue(gw.findByName("A1").isEmpty());
    }

    @Test
    void deleteShouldThrowWhenNotFound() {
        InMemoryShelterGateway gw = new InMemoryShelterGateway();

        assertThrows(EntityNotFoundException.class, () -> gw.deleteShelter(1L));
    }
}