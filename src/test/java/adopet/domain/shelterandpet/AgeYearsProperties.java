package adopet.domain.shelterandpet;


import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AgeYearsProperties {

    @Property
    void validRangeShouldAlwaysWork(@ForAll @IntRange(min = 0, max = 40) int age) {
        assertDoesNotThrow(() -> new AgeYears(age));
    }

    @Property
    void negativeShouldAlwaysFail(@ForAll @IntRange(min = -200, max = -1) int age) {
        assertThrows(RuntimeException.class, () -> new AgeYears(age));
    }

    @Property
    void aboveMaxShouldAlwaysFail(@ForAll @IntRange(min = 41, max = 200) int age) {
        assertThrows(RuntimeException.class, () -> new AgeYears(age));
    }
}