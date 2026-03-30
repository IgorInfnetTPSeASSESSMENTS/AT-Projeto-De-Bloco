package adopet.application.shelterandpet;

import adopet.domain.shelterandpet.Shelter;
import adopet.exception.InvalidUserInputException;
import adopet.fakes.AbstractFakeShelterGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterShelterCommandHandlerTest {

    @Test
    void shouldRejectBlankName() {
        RegisterShelterCommandHandler handler = new RegisterShelterCommandHandler(new NoopShelterGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(null, "31999999999", "a@b.com"));
        assertEquals("Nome do abrigo não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("   ", "31999999999", "a@b.com"));
        assertEquals("Nome do abrigo não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldRejectBlankPhone() {
        RegisterShelterCommandHandler handler = new RegisterShelterCommandHandler(new NoopShelterGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("Abrigo", null, "a@b.com"));
        assertEquals("Telefone do abrigo não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("Abrigo", "   ", "a@b.com"));
        assertEquals("Telefone do abrigo não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldRejectBlankEmail() {
        RegisterShelterCommandHandler handler = new RegisterShelterCommandHandler(new NoopShelterGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("Abrigo", "31999999999", null));
        assertEquals("Email do abrigo não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("Abrigo", "31999999999", "   "));
        assertEquals("Email do abrigo não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldRegisterShelterAndReturnPortugueseMessageWithId() {
        class SpyGateway extends NoopShelterGateway {
            Shelter received;

            @Override
            public Shelter registerShelter(Shelter shelter) {
                this.received = shelter;
                return new Shelter(
                        123L,
                        shelter.name(),
                        shelter.phoneNumber(),
                        shelter.email()
                );
            }
        }

        SpyGateway gateway = new SpyGateway();
        RegisterShelterCommandHandler handler = new RegisterShelterCommandHandler(gateway);

        Shelter result = handler.execute("Abrigo X", "31999999999", "a@b.com");

        assertNotNull(result);
        assertEquals(123L, result.id());

        assertNotNull(gateway.received);
        assertNull(gateway.received.id());
        assertEquals("Abrigo X", gateway.received.name());
        assertEquals("31999999999", gateway.received.phoneNumber().value());
        assertEquals("a@b.com", gateway.received.email().value());
    }

    private static class NoopShelterGateway extends AbstractFakeShelterGateway {
    }
}