package adopet.application.shelterandpet;

import adopet.exception.InvalidUserInputException;
import adopet.fakes.AbstractFakePetGateway;
import adopet.gateway.PetGateway;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class ImportShelterPetsCommandHandlerTest {

    @Test
    void shouldRejectBlankShelterIdOrName() {
        PetGateway gateway = new AbstractFakePetGateway() {
            @Override
            public int importPets(String shelterIdOrName, InputStream csvInputStream) {
                fail("não deve chamar gateway se abrigo inválido");
                return 0;
            }
        };

        ImportShelterPetsCommandHandler handler = new ImportShelterPetsCommandHandler(gateway);
        InputStream stream = new ByteArrayInputStream("x".getBytes(StandardCharsets.UTF_8));

        InvalidUserInputException ex1 =
                assertThrows(InvalidUserInputException.class, () -> handler.execute(null, "pets.csv", stream));
        assertEquals("Id ou nome do abrigo não pode ser vazio.", ex1.getMessage());

        InvalidUserInputException ex2 =
                assertThrows(InvalidUserInputException.class, () -> handler.execute("   ", "pets.csv", stream));
        assertEquals("Id ou nome do abrigo não pode ser vazio.", ex2.getMessage());
    }

    @Test
    void shouldRejectBlankCsvFileName() {
        PetGateway gateway = new AbstractFakePetGateway() {
            @Override
            public int importPets(String shelterIdOrName, InputStream csvInputStream) {
                fail("não deve chamar gateway se csv inválido");
                return 0;
            }
        };

        ImportShelterPetsCommandHandler handler = new ImportShelterPetsCommandHandler(gateway);
        InputStream stream = new ByteArrayInputStream("x".getBytes(StandardCharsets.UTF_8));

        InvalidUserInputException ex1 =
                assertThrows(InvalidUserInputException.class, () -> handler.execute("1", null, stream));
        assertEquals("O arquivo CSV é obrigatório.", ex1.getMessage());

        InvalidUserInputException ex2 =
                assertThrows(InvalidUserInputException.class, () -> handler.execute("1", "   ", stream));
        assertEquals("O arquivo CSV é obrigatório.", ex2.getMessage());
    }

    @Test
    void shouldRejectNonCsvFileName() {
        ImportShelterPetsCommandHandler handler = new ImportShelterPetsCommandHandler(new AbstractFakePetGateway() { });

        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class,
                () -> handler.execute("1", "pets.txt", new ByteArrayInputStream("x".getBytes(StandardCharsets.UTF_8))));

        assertEquals("O arquivo enviado deve possuir extensão .csv.", ex.getMessage());
    }

    @Test
    void shouldTrimAndReturnGatewayCount() {
        class SpyGateway extends AbstractFakePetGateway {
            String lastShelter;
            InputStream lastStream;

            @Override
            public int importPets(String shelterIdOrName, InputStream csvInputStream) {
                this.lastShelter = shelterIdOrName;
                this.lastStream = csvInputStream;
                return 3;
            }
        }

        SpyGateway gateway = new SpyGateway();
        ImportShelterPetsCommandHandler handler = new ImportShelterPetsCommandHandler(gateway);
        ByteArrayInputStream stream = new ByteArrayInputStream("csv".getBytes(StandardCharsets.UTF_8));

        int count = handler.execute("  shelter1  ", " pets.csv ", stream);

        assertEquals(3, count);
        assertEquals("shelter1", gateway.lastShelter);
        assertEquals(stream, gateway.lastStream);
    }
}
