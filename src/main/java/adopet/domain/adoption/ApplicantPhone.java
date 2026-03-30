package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;

import java.util.Objects;

public record ApplicantPhone(String value) {

    public ApplicantPhone(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserInputException("Telefone do solicitante não pode ser vazio.");
        }

        String normalized = value.trim();
        if (normalized.length() < 8) {
            throw new InvalidUserInputException("Telefone do solicitante inválido: " + value);
        }
        if (normalized.length() > 25) {
            throw new InvalidUserInputException("Telefone do solicitante muito longo.");
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
        if (!(o instanceof ApplicantPhone that)) return false;
        return Objects.equals(value, that.value);
    }

}