package adopet.web.dto.shelterandpet;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShelterFormValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateBlankName() {
        ShelterForm form = new ShelterForm("", "31999999999", "abrigo@email.com");

        Set<ConstraintViolation<ShelterForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O nome do abrigo é obrigatório.")));
    }

    @Test
    void shouldValidateBlankPhoneNumber() {
        ShelterForm form = new ShelterForm("Abrigo", "", "abrigo@email.com");

        Set<ConstraintViolation<ShelterForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O telefone é obrigatório.")));
    }

    @Test
    void shouldValidateInvalidEmail() {
        ShelterForm form = new ShelterForm("Abrigo", "31999999999", "email-invalido");

        Set<ConstraintViolation<ShelterForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O email informado é inválido.")));
    }

    @Test
    void shouldValidateNameMaxSize() {
        ShelterForm form = new ShelterForm("A".repeat(121), "31999999999", "abrigo@email.com");

        Set<ConstraintViolation<ShelterForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O nome do abrigo deve ter no máximo 120 caracteres.")));
    }

    @Test
    void shouldValidatePhoneMaxSize() {
        ShelterForm form = new ShelterForm("Abrigo", "1".repeat(21), "abrigo@email.com");

        Set<ConstraintViolation<ShelterForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O telefone deve ter no máximo 20 caracteres.")));
    }
}
