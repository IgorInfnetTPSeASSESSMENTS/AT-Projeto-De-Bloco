package adopet.web.dto.adoption;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdoptionRequestFilterFormTest {

    @Test
    void shouldSetAndGetAllFields() {
        AdoptionRequestFilterForm form = new AdoptionRequestFilterForm();

        form.setStatus("PENDING");
        form.setPetId(1L);
        form.setShelterId(10L);

        assertEquals("PENDING", form.getStatus());
        assertEquals(1L, form.getPetId());
        assertEquals(10L, form.getShelterId());
    }

    @Test
    void shouldStartWithNullFields() {
        AdoptionRequestFilterForm form = new AdoptionRequestFilterForm();

        assertNull(form.getStatus());
        assertNull(form.getPetId());
        assertNull(form.getShelterId());
    }
}