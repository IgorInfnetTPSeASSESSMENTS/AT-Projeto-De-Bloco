package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;

import java.util.Objects;

public record PhoneNumber(String value) {

  public PhoneNumber(String value) {
    if (value == null || value.isBlank()) {
      throw new InvalidUserInputException("Telefone não pode ser vazio.");
    }

    if (value.length() < 8) {
      throw new InvalidUserInputException("Telefone inválido: " + value);
    }

    this.value = value.trim();
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
    if (!(o instanceof PhoneNumber that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }
}
