package adopet.web.dto.adoption;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FailureSimulationFormValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateValidForm() {
        FailureSimulationForm form = new FailureSimulationForm();
        form.setMode("TIMEOUT");

        Set<ConstraintViolation<FailureSimulationForm>> violations = validator.validate(form);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldValidateBlankMode() {
        FailureSimulationForm form = new FailureSimulationForm();
        form.setMode("   ");

        Set<ConstraintViolation<FailureSimulationForm>> violations = validator.validate(form);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("modo de simulação é obrigatório")));
    }
}