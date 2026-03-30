package adopet.web.dto.adoption;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class AdoptionRequestFilterForm {

    @Size(max = 30, message = "O status deve ter no máximo 30 caracteres.")
    private String status;

    @Positive(message = "O id do pet deve ser positivo.")
    private Long petId;

    @Positive(message = "O id do abrigo deve ser positivo.")
    private Long shelterId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getShelterId() {
        return shelterId;
    }

    public void setShelterId(Long shelterId) {
        this.shelterId = shelterId;
    }
}
