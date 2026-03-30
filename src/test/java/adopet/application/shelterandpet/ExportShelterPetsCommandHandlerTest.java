package adopet.application.shelterandpet;

import adopet.exception.InvalidUserInputException;
import adopet.fakes.AbstractFakePetGateway;
import adopet.gateway.PetGateway;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExportShelterPetsCommandHandlerTest {

    @Test
    void shouldRejectBlankShelterIdOrName() {
        PetGateway gw = new AbstractFakePetGateway() { };

        ExportShelterPetsCommandHandler handler = new ExportShelterPetsCommandHandler(gw);

        assertThrows(InvalidUserInputException.class, () -> handler.execute(null));
        assertThrows(InvalidUserInputException.class, () -> handler.execute("   "));
    }

    @Test
    void shouldTrimInputsAndReturnCsvBytes() {
        byte[] expected = "tipo,nome\nGATO,Mimi\n".getBytes(StandardCharsets.UTF_8);
        PetGateway gw = new AbstractFakePetGateway() {
            @Override
            public byte[] exportPets(String shelterIdOrName) {
                assertEquals("1", shelterIdOrName);
                return expected;
            }
        };

        ExportShelterPetsCommandHandler handler = new ExportShelterPetsCommandHandler(gw);

        byte[] exported = handler.execute(" 1 ");
        assertArrayEquals(expected, exported);
    }
}
