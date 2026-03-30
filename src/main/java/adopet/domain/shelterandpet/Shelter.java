package adopet.domain.shelterandpet;

public record Shelter(
    Long id,
    String name,
    PhoneNumber phoneNumber,
    Email email
) {

  public Shelter {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Nome do abrigo não pode ser vazio.");
    }
  }
}
