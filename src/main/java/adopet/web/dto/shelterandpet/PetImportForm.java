package adopet.web.dto.shelterandpet;

import org.springframework.web.multipart.MultipartFile;

public class PetImportForm {

    private MultipartFile csvFile;

    public PetImportForm() {
    }

    public PetImportForm(MultipartFile csvFile) {
        this.csvFile = csvFile;
    }

    public MultipartFile getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(MultipartFile csvFile) {
        this.csvFile = csvFile;
    }
}
