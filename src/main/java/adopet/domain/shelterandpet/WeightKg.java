package adopet.domain.shelterandpet;

import adopet.exception.InvalidUserInputException;

public record WeightKg(double value) {

  public WeightKg {
    if (value <= 0) {
      throw new InvalidUserInputException("Peso deve ser positivo.");
    }

    if (value > 150) {
      throw new InvalidUserInputException("Peso inválido: " + value);
    }
  }
}
