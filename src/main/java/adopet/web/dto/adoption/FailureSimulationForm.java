package adopet.web.dto.adoption;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FailureSimulationForm {

    @NotBlank(message = "O modo de simulação é obrigatório.")
    @Size(max = 30, message = "O modo de simulação deve ter no máximo 30 caracteres.")
    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
