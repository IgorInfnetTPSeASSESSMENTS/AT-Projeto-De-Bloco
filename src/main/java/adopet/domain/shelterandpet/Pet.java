package adopet.domain.shelterandpet;

/*
 * Pet: record imutável que representa um animal em um abrigo.
 * Invariantes aplicadas no construtor compacto:
 * - `breed` e `color` devem ser não-nulos e não-vazios.
 * - Outros value objects (por ex. `PetName`, `AgeYears`, `WeightKg`) realizam suas próprias validações.
 *
 * O campo `id` pode ser nulo quando o identificador for atribuído pelo gateway
 * (por exemplo ao registrar um novo pet na camada de persistência).
 */
public record Pet(
    Long id,
    PetType type,
    PetName name,
    String breed,
    AgeYears age,
    String color,
    WeightKg weight,
    PetStatus status
) {

  public Pet {
    if (breed == null || breed.isBlank()) {
      throw new IllegalArgumentException("Raça não pode ser vazia.");
    }

    if (color == null || color.isBlank()) {
      throw new IllegalArgumentException("Cor não pode ser vazia.");
    }

    if (status == null) {
      throw new IllegalArgumentException("Status do pet não pode ser vazio.");
    }
  }

  public Pet withStatus(PetStatus newStatus) {
    return new Pet(id, type, name, breed, age, color, weight, newStatus);
  }
}
