package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;

import java.util.Objects;

public record Email(String value) {

  public Email(String value) {
    if (value == null || value.isBlank()) {
      throw new InvalidUserInputException("Email não pode ser vazio.");
    }

    if (!value.contains("@")) {
      throw new InvalidUserInputException("Email inválido: " + value);
    }

    this.value = value.trim().toLowerCase();
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Email email)) {
      return false;
    }
    return Objects.equals(value, email.value);
  }
}
