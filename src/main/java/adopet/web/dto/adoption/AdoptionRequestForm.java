package adopet.web.dto.adoption;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class AdoptionRequestForm {

    @NotNull(message = "O id do pet é obrigatório.")
    @Positive(message = "O id do pet deve ser positivo.")
    private Long petId;

    @NotNull(message = "O id do abrigo é obrigatório.")
    @Positive(message = "O id do abrigo deve ser positivo.")
    private Long shelterId;

    @NotBlank(message = "O nome do solicitante é obrigatório.")
    @Size(max = 120, message = "O nome do solicitante deve ter no máximo 120 caracteres.")
    private String applicantName;

    @NotBlank(message = "O email do solicitante é obrigatório.")
    @Email(message = "O email do solicitante é inválido.")
    @Size(max = 254, message = "O email do solicitante deve ter no máximo 254 caracteres.")
    private String applicantEmail;

    @NotBlank(message = "O telefone do solicitante é obrigatório.")
    @Size(max = 20, message = "O telefone do solicitante deve ter no máximo 20 caracteres.")
    private String applicantPhone;

    @NotBlank(message = "O documento do solicitante é obrigatório.")
    @Size(max = 20, message = "O documento do solicitante deve ter no máximo 20 caracteres.")
    private String applicantDocument;

    @NotBlank(message = "O tipo de moradia é obrigatório.")
    @Size(max = 30, message = "O tipo de moradia deve ter no máximo 30 caracteres.")
    private String housingType;

    private boolean hasOtherPets;

    @NotBlank(message = "O motivo da adoção é obrigatório.")
    @Size(min = 10, max = 1000, message = "O motivo da adoção deve ter entre 10 e 1000 caracteres.")
    private String reason;

    public AdoptionRequestForm() {
    }

    public AdoptionRequestForm(Long petId, Long shelterId, String applicantName, String applicantEmail, String applicantPhone,
                               String applicantDocument, String housingType, boolean hasOtherPets, String reason) {
        this.petId = petId;
        this.shelterId = shelterId;
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.applicantPhone = applicantPhone;
        this.applicantDocument = applicantDocument;
        this.housingType = housingType;
        this.hasOtherPets = hasOtherPets;
        this.reason = reason;
    }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
    public Long getShelterId() { return shelterId; }
    public void setShelterId(Long shelterId) { this.shelterId = shelterId; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public String getApplicantEmail() { return applicantEmail; }
    public void setApplicantEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }
    public String getApplicantPhone() { return applicantPhone; }
    public void setApplicantPhone(String applicantPhone) { this.applicantPhone = applicantPhone; }
    public String getApplicantDocument() { return applicantDocument; }
    public void setApplicantDocument(String applicantDocument) { this.applicantDocument = applicantDocument; }
    public String getHousingType() { return housingType; }
    public void setHousingType(String housingType) { this.housingType = housingType; }
    public boolean isHasOtherPets() { return hasOtherPets; }
    public void setHasOtherPets(boolean hasOtherPets) { this.hasOtherPets = hasOtherPets; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
