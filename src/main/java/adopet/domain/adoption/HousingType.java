package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;

public enum HousingType {
    HOUSE,
    APARTMENT,
    FARM,
    OTHER;

    public static HousingType from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidUserInputException("Tipo de moradia é obrigatório.");
        }

        return switch (raw.trim().toUpperCase()) {
            case "HOUSE", "CASA" -> HOUSE;
            case "APARTMENT", "APARTAMENTO" -> APARTMENT;
            case "FARM", "SITIO", "CHACARA" -> FARM;
            case "OTHER", "OUTRO" -> OTHER;
            default -> throw new InvalidUserInputException("Tipo de moradia inválido: " + raw);
        };
    }
}
