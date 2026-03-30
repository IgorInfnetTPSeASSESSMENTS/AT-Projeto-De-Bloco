package adopet.application.adoption;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisExecutionStatusTest {

    @Test
    void shouldContainSuccessAndFailed() {
        assertEquals("SUCCESS", AnalysisExecutionStatus.SUCCESS.name());
        assertEquals("FAILED", AnalysisExecutionStatus.FAILED.name());
    }
}