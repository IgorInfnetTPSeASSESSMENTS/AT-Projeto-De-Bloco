package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;

public record AgeYears(int value) {

  public AgeYears {
    if (value < 0) {
      throw new InvalidUserInputException("Idade não pode ser negativa.");
    }

    if (value > 40) {
      throw new InvalidUserInputException("Idade inválida para pet: " + value);
    }
  }
}
