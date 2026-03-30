package adopet.web.dto.adoption;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FailureSimulationFormTest {

    @Test
    void shouldSetAndGetMode() {
        FailureSimulationForm form = new FailureSimulationForm();

        form.setMode("TIMEOUT");

        assertEquals("TIMEOUT", form.getMode());
    }

    @Test
    void shouldStartWithNullMode() {
        FailureSimulationForm form = new FailureSimulationForm();

        assertNull(form.getMode());
    }
}