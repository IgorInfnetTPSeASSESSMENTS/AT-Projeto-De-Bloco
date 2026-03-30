package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;

public record ReasonText(String value) {

  public ReasonText(String value) {
    if (value == null || value.isBlank()) {
      throw new InvalidUserInputException("Motivo da adoção não pode ser vazio.");
    }

    String normalized = value.trim();
    if (normalized.length() < 10) {
      throw new InvalidUserInputException("Motivo da adoção muito curto.");
    }
    if (normalized.length() > 1000) {
      throw new InvalidUserInputException("Motivo da adoção muito longo.");
    }

    this.value = normalized;
  }

  @Override
  public String toString() {
    return value;
  }
}
