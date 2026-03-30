package adopet.web.dto.adoption;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AdoptionRequestFormValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateValidForm() {
        AdoptionRequestForm form = new AdoptionRequestForm(
                1L,
                10L,
                "Maria da Silva",
                "maria@email.com",
                "31999999999",
                "12345678900",
                "HOUSE",
                true,
                "Quero adotar com responsabilidade e carinho."
        );

        Set<ConstraintViolation<AdoptionRequestForm>> violations = validator.validate(form);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldValidateRequiredFields() {
        AdoptionRequestForm form = new AdoptionRequestForm();

        Set<ConstraintViolation<AdoptionRequestForm>> violations = validator.validate(form);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldValidateInvalidEmail() {
        AdoptionRequestForm form = new AdoptionRequestForm(
                1L,
                10L,
                "Maria da Silva",
                "email-invalido",
                "31999999999",
                "12345678900",
                "HOUSE",
                true,
                "Quero adotar com responsabilidade e carinho."
        );

        Set<ConstraintViolation<AdoptionRequestForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("inválido")));
    }

    @Test
    void shouldValidateReasonSize() {
        AdoptionRequestForm form = new AdoptionRequestForm(
                1L,
                10L,
                "Maria da Silva",
                "maria@email.com",
                "31999999999",
                "12345678900",
                "HOUSE",
                true,
                "curto"
        );

        Set<ConstraintViolation<AdoptionRequestForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("entre 10 e 1000 caracteres")));
    }

    @Test
    void shouldValidatePositiveIds() {
        AdoptionRequestForm form = new AdoptionRequestForm(
                0L,
                -1L,
                "Maria da Silva",
                "maria@email.com",
                "31999999999",
                "12345678900",
                "HOUSE",
                true,
                "Quero adotar com responsabilidade e carinho."
        );

        Set<ConstraintViolation<AdoptionRequestForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("id do pet deve ser positivo")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("id do abrigo deve ser positivo")));
    }

    @Test
    void shouldValidateApplicantNameMaxSize() {
        AdoptionRequestForm form = new AdoptionRequestForm(
                1L,
                10L,
                "M".repeat(121),
                "maria@email.com",
                "31999999999",
                "12345678900",
                "HOUSE",
                true,
                "Quero adotar com responsabilidade e carinho."
        );

        Set<ConstraintViolation<AdoptionRequestForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("nome do solicitante deve ter no máximo 120 caracteres")));
    }

    @Test
    void shouldValidateApplicantEmailMaxSize() {
        AdoptionRequestForm form = new AdoptionRequestForm(
                1L,
                10L,
                "Maria da Silva",
                "a".repeat(245) + "@email.com",
                "31999999999",
                "12345678900",
                "HOUSE",
                true,
                "Quero adotar com responsabilidade e carinho."
        );

        Set<ConstraintViolation<AdoptionRequestForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("email do solicitante deve ter no máximo 254 caracteres")));
    }
}
