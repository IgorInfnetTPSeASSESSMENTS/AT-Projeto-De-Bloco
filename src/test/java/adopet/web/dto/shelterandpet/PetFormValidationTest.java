package adopet.web.dto.shelterandpet;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PetFormValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateBlankType() {
        PetForm form = new PetForm("", "Rex", "Vira-lata", 2, "Caramelo", 10.5);

        Set<ConstraintViolation<PetForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O tipo do pet é obrigatório.")));
    }

    @Test
    void shouldValidateBlankName() {
        PetForm form = new PetForm("CACHORRO", "", "Vira-lata", 2, "Caramelo", 10.5);

        Set<ConstraintViolation<PetForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O nome do pet é obrigatório.")));
    }

    @Test
    void shouldValidateBlankBreed() {
        PetForm form = new PetForm("CACHORRO", "Rex", "", 2, "Caramelo", 10.5);

        Set<ConstraintViolation<PetForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("A raça do pet é obrigatória.")));
    }

    @Test
    void shouldValidateNegativeAge() {
        PetForm form = new PetForm("CACHORRO", "Rex", "Vira-lata", -1, "Caramelo", 10.5);

        Set<ConstraintViolation<PetForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("A idade do pet não pode ser negativa.")));
    }

    @Test
    void shouldValidateBlankColor() {
        PetForm form = new PetForm("CACHORRO", "Rex", "Vira-lata", 2, "", 10.5);

        Set<ConstraintViolation<PetForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("A cor do pet é obrigatória.")));
    }

    @Test
    void shouldValidateInvalidWeight() {
        PetForm form = new PetForm("CACHORRO", "Rex", "Vira-lata", 2, "Caramelo", 0.0);

        Set<ConstraintViolation<PetForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O peso do pet deve ser maior que zero.")));
    }

    @Test
    void shouldValidateNameMaxSize() {
        PetForm form = new PetForm("CACHORRO", "R".repeat(81), "Vira-lata", 2, "Caramelo", 10.5);

        Set<ConstraintViolation<PetForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O nome do pet deve ter no máximo 80 caracteres.")));
    }

    @Test
    void shouldValidateAgeMaxValue() {
        PetForm form = new PetForm("CACHORRO", "Rex", "Vira-lata", 51, "Caramelo", 10.5);

        Set<ConstraintViolation<PetForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("A idade do pet deve ser no máximo 50 anos.")));
    }

    @Test
    void shouldValidateWeightMaxValue() {
        PetForm form = new PetForm("CACHORRO", "Rex", "Vira-lata", 2, "Caramelo", 201.0);

        Set<ConstraintViolation<PetForm>> violations = validator.validate(form);

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O peso do pet deve ser no máximo 200 kg.")));
    }
}
