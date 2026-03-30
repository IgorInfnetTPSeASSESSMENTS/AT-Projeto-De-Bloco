package adopet.web.dto.shelterandpet;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class PetImportFormValidationTest {

  @Test
  void shouldCreateEmptyFormByDefault() {
    PetImportForm form = new PetImportForm();

    assertNull(form.getCsvFile());
  }

  @Test
  void shouldStoreMultipartFile() {
    MockMultipartFile csvFile = new MockMultipartFile("csvFile", "pets.csv", "text/csv", "tipo,nome".getBytes());
    PetImportForm form = new PetImportForm(csvFile);

    assertSame(csvFile, form.getCsvFile());
  }

  @Test
  void shouldAllowReplacingMultipartFile() {
    PetImportForm form = new PetImportForm();
    MockMultipartFile csvFile = new MockMultipartFile("csvFile", "pets.csv", "text/csv", "tipo,nome".getBytes());

    form.setCsvFile(csvFile);

    assertEquals("pets.csv", form.getCsvFile().getOriginalFilename());
  }
}
