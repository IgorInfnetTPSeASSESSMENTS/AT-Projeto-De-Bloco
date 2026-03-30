package adopet.domain.shelterandpet;

/*
 * PetType: enumera os tipos de pet suportados e fornece um parser estrito a partir de entrada bruta.
 * O parser remove espaços e converte para maiúsculas, lançando IllegalArgumentException para valores inválidos.
 */
public enum PetType {
  CACHORRO,
  GATO;

  public static PetType from(String raw) {
    if (raw == null) {
      throw new IllegalArgumentException("Tipo de pet não pode ser nulo");
    }

    return switch (raw.trim().toUpperCase()) {
      case "CACHORRO" -> CACHORRO;
      case "GATO" -> GATO;
      default -> throw new IllegalArgumentException("Tipo de pet inválido: " + raw);
    };
  }
}
