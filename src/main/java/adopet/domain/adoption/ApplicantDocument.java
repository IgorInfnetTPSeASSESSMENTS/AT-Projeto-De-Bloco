package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;

import java.util.Objects;

public record ApplicantDocument(String value) {

    public ApplicantDocument(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserInputException("Documento do solicitante não pode ser vazio.");
        }

        String normalized = value.trim();
        if (normalized.length() < 5) {
            throw new InvalidUserInputException("Documento do solicitante inválido.");
        }
        if (normalized.length() > 30) {
            throw new InvalidUserInputException("Documento do solicitante muito longo.");
        }

        this.value = normalized;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicantDocument that)) return false;
        return Objects.equals(value, that.value);
    }

}