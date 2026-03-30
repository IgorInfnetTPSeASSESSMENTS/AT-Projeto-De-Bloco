package adopet.domain.adoption;

import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicantPhoneTest {

    @Test
    void shouldCreateValidPhone() {
        ApplicantPhone phone = new ApplicantPhone("31999999999");

        assertEquals("31999999999", phone.value());
    }

    @Test
    void shouldTrimPhone() {
        ApplicantPhone phone = new ApplicantPhone("  31999999999  ");

        assertEquals("31999999999", phone.value());
    }

    @Test
    void shouldFailWhenPhoneIsNull() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantPhone(null));
    }

    @Test
    void shouldFailWhenPhoneIsBlank() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantPhone("   "));
    }

    @Test
    void shouldFailWhenPhoneIsTooShort() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantPhone("1234567"));
    }

    @Test
    void shouldFailWhenPhoneIsTooLong() {
        assertThrows(InvalidUserInputException.class, () -> new ApplicantPhone("1".repeat(26)));
    }

    @Test
    void shouldImplementToString() {
        ApplicantPhone phone = new ApplicantPhone("31999999999");

        assertEquals("31999999999", phone.toString());
    }

    @Test
    void shouldImplementEqualsAndHashCode() {
        ApplicantPhone a = new ApplicantPhone("31999999999");
        ApplicantPhone b = new ApplicantPhone("31999999999");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotBeEqualToDifferentPhone() {
        ApplicantPhone a = new ApplicantPhone("31999999999");
        ApplicantPhone b = new ApplicantPhone("31888888888");

        assertNotEquals(a, b);
    }

    @Test
    void shouldReturnTrueWhenComparingSameInstance() {
        ApplicantPhone phone = new ApplicantPhone("31999999999");

        assertEquals(phone, phone);
    }

    @Test
    void shouldReturnFalseWhenComparingWithDifferentType() {
        ApplicantPhone phone = new ApplicantPhone("31999999999");

        assertNotEquals(phone, "string");
    }

    @Test
    void shouldReturnFalseWhenComparingWithNull() {
        ApplicantPhone phone = new ApplicantPhone("31999999999");

        assertNotEquals(phone, null);
    }
}