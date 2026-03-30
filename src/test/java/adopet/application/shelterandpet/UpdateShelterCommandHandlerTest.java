package adopet.application.shelterandpet;

import adopet.domain.shelterandpet.Email;
import adopet.domain.shelterandpet.PhoneNumber;
import adopet.domain.shelterandpet.Shelter;
import adopet.exception.InvalidUserInputException;
import adopet.fakes.AbstractFakeShelterGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateShelterCommandHandlerTest {

    @Test
    void shouldRejectNullId() {
        UpdateShelterCommandHandler handler = new UpdateShelterCommandHandler(new NoopShelterGateway());

        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(null, "Abrigo", "31999999999", "a@b.com"));

        assertEquals("Id do abrigo não pode ser vazio.", ex.getMessage());
    }

    @Test
    void shouldRejectBlankName() {
        UpdateShelterCommandHandler handler = new UpdateShelterCommandHandler(new NoopShelterGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, null, "31999999999", "a@b.com"));
        assertEquals("Nome do abrigo não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, "   ", "31999999999", "a@b.com"));
        assertEquals("Nome do abrigo não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldRejectBlankPhone() {
        UpdateShelterCommandHandler handler = new UpdateShelterCommandHandler(new NoopShelterGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, "Abrigo", null, "a@b.com"));
        assertEquals("Telefone do abrigo não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, "Abrigo", "   ", "a@b.com"));
        assertEquals("Telefone do abrigo não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldRejectBlankEmail() {
        UpdateShelterCommandHandler handler = new UpdateShelterCommandHandler(new NoopShelterGateway());

        InvalidUserInputException ex1 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, "Abrigo", "31999999999", null));
        assertEquals("Email do abrigo não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 = assertThrows(InvalidUserInputException.class,
                () -> handler.execute(1L, "Abrigo", "31999999999", "   "));
        assertEquals("Email do abrigo não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldTrimAndCallGatewayUpdate() {
        class SpyGateway extends NoopShelterGateway {
            Long lastId;
            String lastName;
            PhoneNumber lastPhone;
            Email lastEmail;

            @Override
            public Shelter updateShelter(Long id, String name, PhoneNumber phoneNumber, Email email) {
                this.lastId = id;
                this.lastName = name;
                this.lastPhone = phoneNumber;
                this.lastEmail = email;

                return new Shelter(id, name, phoneNumber, email);
            }
        }

        SpyGateway gateway = new SpyGateway();
        UpdateShelterCommandHandler handler = new UpdateShelterCommandHandler(gateway);

        Shelter updated = handler.execute(5L, "  Abrigo X  ", " 31999999999 ", " a@b.com ");

        assertEquals(5L, updated.id());
        assertEquals("Abrigo X", updated.name());
        assertEquals("31999999999", updated.phoneNumber().value());
        assertEquals("a@b.com", updated.email().value());

        assertEquals(5L, gateway.lastId);
        assertEquals("Abrigo X", gateway.lastName);
        assertEquals("31999999999", gateway.lastPhone.value());
        assertEquals("a@b.com", gateway.lastEmail.value());
    }

    private static class NoopShelterGateway extends AbstractFakeShelterGateway {
    }
}