package adopet.application.shelterandpet;

import adopet.exception.InvalidUserInputException;
import adopet.fakes.AbstractFakePetGateway;
import adopet.gateway.PetGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeletePetCommandHandlerTest {

    @Test
    void shouldRejectNullPetId() {
        PetGateway gateway = new AbstractFakePetGateway() {
            @Override
            public void deletePet(Long petId) {
                fail("deletePet não deve ser chamado quando petId é null");
            }
        };

        DeletePetCommandHandler handler = new DeletePetCommandHandler(gateway);

        InvalidUserInputException ex = assertThrows(
                InvalidUserInputException.class,
                () -> handler.execute(null)
        );

        assertEquals("Id do pet não pode ser vazio.", ex.getMessage());
    }

    @Test
    void shouldCallGatewayWhenPetIdIsProvided() {
        class SpyGateway extends AbstractFakePetGateway {
            Long deletedId;

            @Override
            public void deletePet(Long petId) {
                this.deletedId = petId;
            }
        }

        SpyGateway gateway = new SpyGateway();
        DeletePetCommandHandler handler = new DeletePetCommandHandler(gateway);

        assertDoesNotThrow(() -> handler.execute(10L));
        assertEquals(10L, gateway.deletedId);
    }
}