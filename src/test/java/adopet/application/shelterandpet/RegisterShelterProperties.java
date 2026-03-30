package adopet.application.shelterandpet;

import adopet.exception.InvalidUserInputException;
import adopet.fakes.FakeShelterGateway;
import net.jqwik.api.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterShelterProperties {

    @Property
    void validNamesEmailsAndPhonesShouldNotThrow(
            @ForAll("validNames") String name,
            @ForAll("validEmails") String email,
            @ForAll("validPhones") String phone
    ) {
        FakeShelterGateway gateway = new FakeShelterGateway();
        RegisterShelterCommandHandler handler = new RegisterShelterCommandHandler(gateway);

        assertDoesNotThrow(() -> handler.execute(name, phone, email));
    }

    @Property
    void blankNamesShouldThrow(@ForAll("blankStrings") String blank) {
        FakeShelterGateway gateway = new FakeShelterGateway();
        RegisterShelterCommandHandler handler = new RegisterShelterCommandHandler(gateway);

        assertThrows(InvalidUserInputException.class,
                () -> handler.execute(blank, "31999999999", "a@b.com"));
    }

    @Provide
    Arbitrary<String> validNames() {
        return Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ")
                .ofMinLength(1)
                .ofMaxLength(60)
                .map(String::trim)
                .filter(s -> !s.isBlank());
    }

    @Provide
    Arbitrary<String> validEmails() {
        Arbitrary<String> user = Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyz0123456789")
                .ofMinLength(1).ofMaxLength(12);

        Arbitrary<String> domain = Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyz0123456789")
                .ofMinLength(1).ofMaxLength(10);

        return Combinators.combine(user, domain).as((u, d) -> u + "@" + d + ".com");
    }

    @Provide
    Arbitrary<String> validPhones() {
        return Arbitraries.strings()
                .numeric()
                .ofMinLength(8)
                .ofMaxLength(13);
    }

    @Provide
    Arbitrary<String> blankStrings() {
        return Arbitraries.of("", " ", "   ", "\t", "\n");
    }
}