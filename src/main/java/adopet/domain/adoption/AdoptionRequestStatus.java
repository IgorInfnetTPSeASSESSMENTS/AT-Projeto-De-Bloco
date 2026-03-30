package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;

public enum AdoptionRequestStatus {
    PENDING,
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
    CANCELLED;

    public static AdoptionRequestStatus from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidUserInputException("Status é obrigatório.");
        }

        return switch (raw.trim().toUpperCase()) {
            case "PENDING", "PENDENTE" -> PENDING;
            case "UNDER_REVIEW", "EM_ANALISE", "EM ANÁLISE" -> UNDER_REVIEW;
            case "APPROVED", "APROVADA", "APROVADO" -> APPROVED;
            case "REJECTED", "REJEITADA", "REJEITADO" -> REJECTED;
            case "CANCELLED", "CANCELADA", "CANCELADO" -> CANCELLED;
            default -> throw new InvalidUserInputException("Status inválido: " + raw);
        };
    }
}