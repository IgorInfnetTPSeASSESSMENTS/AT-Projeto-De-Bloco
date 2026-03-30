package adopet.application.shelterandpet;

import adopet.exception.InvalidUserInputException;
import adopet.fakes.AbstractFakeShelterGateway;
import adopet.gateway.ShelterGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeleteShelterCommandHandlerTest {

    @Test
    void shouldRejectNullId() {
        ShelterGateway gateway = new AbstractFakeShelterGateway() {
            @Override public void deleteShelter(Long id) { fail("deleteShelter não deve ser chamado quando id é null"); }
        };

        DeleteShelterCommandHandler handler = new DeleteShelterCommandHandler(gateway);

        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class, () -> handler.execute(null));
        assertEquals("Id do abrigo não pode ser vazio.", ex.getMessage());
    }

    @Test
    void shouldCallGatewayWhenValidId() {
        class SpyGateway extends AbstractFakeShelterGateway {
            Long deletedId;


            @Override
            public void deleteShelter(Long id) {
                this.deletedId = id;
            }
        }

        SpyGateway gateway = new SpyGateway();
        DeleteShelterCommandHandler handler = new DeleteShelterCommandHandler(gateway);

        assertDoesNotThrow(() -> handler.execute(7L));
        assertEquals(7L, gateway.deletedId);
    }
}