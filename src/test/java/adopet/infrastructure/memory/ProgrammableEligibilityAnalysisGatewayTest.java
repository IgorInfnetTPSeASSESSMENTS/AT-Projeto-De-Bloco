package adopet.infrastructure.memory;

import adopet.domain.adoption.EligibilityAnalysis;
import adopet.exception.ExternalServiceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProgrammableEligibilityAnalysisGatewayTest {

    @Test
    void shouldReturnConfiguredSuccessResult() {
        ProgrammableEligibilityAnalysisGateway gateway = new ProgrammableEligibilityAnalysisGateway();
        gateway.setScenarioMode(ScenarioMode.SUCCESS);
        gateway.setSuccessResult(EligibilityAnalysis.ELIGIBLE);

        EligibilityAnalysis result = gateway.analyze(
                1L,
                10L,
                "12345678900",
                true,
                "Quero adotar com responsabilidade."
        );

        assertEquals(EligibilityAnalysis.ELIGIBLE, result);
    }

    @Test
    void shouldThrowTimeoutException() {
        ProgrammableEligibilityAnalysisGateway gateway = new ProgrammableEligibilityAnalysisGateway();
        gateway.setScenarioMode(ScenarioMode.TIMEOUT);

        assertThrows(ExternalServiceException.class, () ->
                gateway.analyze(1L, 10L, "12345678900", true, "Motivo válido.")
        );
    }

    @Test
    void shouldThrowNetworkErrorException() {
        ProgrammableEligibilityAnalysisGateway gateway = new ProgrammableEligibilityAnalysisGateway();
        gateway.setScenarioMode(ScenarioMode.NETWORK_ERROR);

        assertThrows(ExternalServiceException.class, () ->
                gateway.analyze(1L, 10L, "12345678900", true, "Motivo válido.")
        );
    }

    @Test
    void shouldThrowServiceUnavailableException() {
        ProgrammableEligibilityAnalysisGateway gateway = new ProgrammableEligibilityAnalysisGateway();
        gateway.setScenarioMode(ScenarioMode.SERVICE_UNAVAILABLE);

        assertThrows(ExternalServiceException.class, () ->
                gateway.analyze(1L, 10L, "12345678900", true, "Motivo válido.")
        );
    }

    @Test
    void shouldThrowInvalidResponseException() {
        ProgrammableEligibilityAnalysisGateway gateway = new ProgrammableEligibilityAnalysisGateway();
        gateway.setScenarioMode(ScenarioMode.INVALID_RESPONSE);

        assertThrows(ExternalServiceException.class, () ->
                gateway.analyze(1L, 10L, "12345678900", true, "Motivo válido.")
        );
    }

    @Test
    void shouldThrowUnexpectedFailureException() {
        ProgrammableEligibilityAnalysisGateway gateway = new ProgrammableEligibilityAnalysisGateway();
        gateway.setScenarioMode(ScenarioMode.UNEXPECTED_FAILURE);

        assertThrows(RuntimeException.class, () ->
                gateway.analyze(1L, 10L, "12345678900", true, "Motivo válido.")
        );
    }

    @Test
    void shouldReturnCurrentScenarioMode() {
        ProgrammableEligibilityAnalysisGateway gateway = new ProgrammableEligibilityAnalysisGateway();

        gateway.setScenarioMode(ScenarioMode.NETWORK_ERROR);

        assertEquals(ScenarioMode.NETWORK_ERROR, gateway.getScenarioMode());
    }
}