package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HousingTypeTest {

    @Test
    void shouldParseHouseInEnglish() {
        assertEquals(HousingType.HOUSE, HousingType.from("HOUSE"));
    }

    @Test
    void shouldParseHouseInPortuguese() {
        assertEquals(HousingType.HOUSE, HousingType.from("CASA"));
    }

    @Test
    void shouldParseApartmentInEnglish() {
        assertEquals(HousingType.APARTMENT, HousingType.from("APARTMENT"));
    }

    @Test
    void shouldParseApartmentInPortuguese() {
        assertEquals(HousingType.APARTMENT, HousingType.from("APARTAMENTO"));
    }

    @Test
    void shouldParseFarmInEnglish() {
        assertEquals(HousingType.FARM, HousingType.from("FARM"));
    }

    @Test
    void shouldParseFarmAsSitio() {
        assertEquals(HousingType.FARM, HousingType.from("SITIO"));
    }

    @Test
    void shouldParseFarmAsChacara() {
        assertEquals(HousingType.FARM, HousingType.from("CHACARA"));
    }

    @Test
    void shouldParseOtherInEnglish() {
        assertEquals(HousingType.OTHER, HousingType.from("OTHER"));
    }

    @Test
    void shouldParseOtherInPortuguese() {
        assertEquals(HousingType.OTHER, HousingType.from("OUTRO"));
    }

    @Test
    void shouldTrimAndUppercaseInput() {
        assertEquals(HousingType.HOUSE, HousingType.from("  house  "));
    }

    @Test
    void shouldFailWhenRawIsNull() {
        assertThrows(InvalidUserInputException.class, () -> HousingType.from(null));
    }

    @Test
    void shouldFailWhenRawIsBlank() {
        assertThrows(InvalidUserInputException.class, () -> HousingType.from("   "));
    }

    @Test
    void shouldFailWhenRawIsInvalid() {
        assertThrows(InvalidUserInputException.class, () -> HousingType.from("CASTELO"));
    }
}