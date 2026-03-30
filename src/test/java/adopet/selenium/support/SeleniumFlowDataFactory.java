package adopet.selenium.support;

public final class SeleniumFlowDataFactory {

    private static final int DEFAULT_TEXT_LIMIT = 20;

    private SeleniumFlowDataFactory() {
    }

    public static FlowUniqueData create(String scenarioName) {
        String normalized = normalizeScenarioName(scenarioName);
        String shortSuffix = shortSuffix();
        String shortLabel = compactLabel(normalized);

        String shelterName = truncate("Abr" + shortSuffix + shortLabel, DEFAULT_TEXT_LIMIT);
        String shelterEmail = "a" + shortSuffix + "@a.com";
        String shelterPhone = validBrazilMobile(shortSuffix, "31");

        String petName = truncate("Pet" + shortSuffix + shortLabel, DEFAULT_TEXT_LIMIT);

        String applicantName = truncate("Cand" + shortSuffix + shortLabel, DEFAULT_TEXT_LIMIT);
        String applicantEmail = "u" + shortSuffix + "@u.com";
        String applicantPhone = validBrazilMobile(new StringBuilder(shortSuffix).reverse().toString(), "21");
        String applicantDocument = shortSuffix + "12345";

        return new FlowUniqueData(
                shelterName,
                shelterEmail,
                shelterPhone,
                petName,
                applicantName,
                applicantEmail,
                applicantPhone,
                applicantDocument
        );
    }

    private static String shortSuffix() {
        String digits = String.valueOf(Math.abs(System.nanoTime()));
        return digits.substring(Math.max(0, digits.length() - 6));
    }

    private static String normalizeScenarioName(String scenarioName) {
        return scenarioName
                .toLowerCase()
                .replace('_', '-')
                .replace(' ', '-')
                .replaceAll("[^a-z0-9-]", "");
    }

    private static String compactLabel(String normalized) {
        String[] parts = normalized.split("-");
        StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            sb.append(part, 0, Math.min(part.length(), 2));
        }

        String result = sb.toString();
        return result.isBlank() ? "ts" : result;
    }

    private static String truncate(String value, int maxLength) {
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    private static String validBrazilMobile(String seed, String ddd) {
        String last8 = leftPadDigits(seed, 8, '1');
        return ddd + "9" + last8;
    }

    private static String leftPadDigits(String value, int size, char fill) {
        String digitsOnly = value.replaceAll("\\D", "");

        if (digitsOnly.length() >= size) {
            return digitsOnly.substring(digitsOnly.length() - size);
        }

        StringBuilder sb = new StringBuilder();
        while (sb.length() + digitsOnly.length() < size) {
            sb.append(fill);
        }
        sb.append(digitsOnly);
        return sb.toString();
    }

    public record FlowUniqueData(
            String shelterName,
            String shelterEmail,
            String shelterPhone,
            String petName,
            String applicantName,
            String applicantEmail,
            String applicantPhone,
            String applicantDocument
    ) {
    }
}