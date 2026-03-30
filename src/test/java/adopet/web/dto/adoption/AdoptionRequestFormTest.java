package adopet.web.dto.adoption;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdoptionRequestFormTest {

    @Test
    void shouldCreateEmptyForm() {
        AdoptionRequestForm form = new AdoptionRequestForm();

        assertNull(form.getPetId());
        assertNull(form.getShelterId());
        assertNull(form.getApplicantName());
        assertNull(form.getApplicantEmail());
        assertNull(form.getApplicantPhone());
        assertNull(form.getApplicantDocument());
        assertNull(form.getHousingType());
        assertFalse(form.isHasOtherPets());
        assertNull(form.getReason());
    }

    @Test
    void shouldCreateFormWithAllFields() {
        AdoptionRequestForm form = new AdoptionRequestForm(
                1L,
                10L,
                "Maria da Silva",
                "maria@email.com",
                "31999999999",
                "12345678900",
                "HOUSE",
                true,
                "Quero adotar com responsabilidade."
        );

        assertEquals(1L, form.getPetId());
        assertEquals(10L, form.getShelterId());
        assertEquals("Maria da Silva", form.getApplicantName());
        assertEquals("maria@email.com", form.getApplicantEmail());
        assertEquals("31999999999", form.getApplicantPhone());
        assertEquals("12345678900", form.getApplicantDocument());
        assertEquals("HOUSE", form.getHousingType());
        assertTrue(form.isHasOtherPets());
        assertEquals("Quero adotar com responsabilidade.", form.getReason());
    }

    @Test
    void shouldSetAndGetAllFields() {
        AdoptionRequestForm form = new AdoptionRequestForm();

        form.setPetId(2L);
        form.setShelterId(20L);
        form.setApplicantName("João");
        form.setApplicantEmail("joao@email.com");
        form.setApplicantPhone("31888888888");
        form.setApplicantDocument("99999999999");
        form.setHousingType("APARTMENT");
        form.setHasOtherPets(true);
        form.setReason("Quero adotar com muito cuidado e carinho.");

        assertEquals(2L, form.getPetId());
        assertEquals(20L, form.getShelterId());
        assertEquals("João", form.getApplicantName());
        assertEquals("joao@email.com", form.getApplicantEmail());
        assertEquals("31888888888", form.getApplicantPhone());
        assertEquals("99999999999", form.getApplicantDocument());
        assertEquals("APARTMENT", form.getHousingType());
        assertTrue(form.isHasOtherPets());
        assertEquals("Quero adotar com muito cuidado e carinho.", form.getReason());
    }
}