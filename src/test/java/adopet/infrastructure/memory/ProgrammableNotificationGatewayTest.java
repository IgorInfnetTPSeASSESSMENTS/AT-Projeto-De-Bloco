package adopet.infrastructure.memory;

import adopet.exception.ExternalServiceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProgrammableNotificationGatewayTest {

    @Test
    void shouldRegisterCreationEventWhenSuccessful() {
        ProgrammableNotificationGateway gateway = new ProgrammableNotificationGateway();
        gateway.setScenarioMode(ScenarioMode.SUCCESS);

        gateway.notifyCreation(1L, "maria@email.com");

        assertEquals(1, gateway.sentEvents().size());
        assertTrue(gateway.sentEvents().getFirst().contains("CREATED:1:maria@email.com"));
    }

    @Test
    void shouldRegisterApprovalEventWhenSuccessful() {
        ProgrammableNotificationGateway gateway = new ProgrammableNotificationGateway();
        gateway.setScenarioMode(ScenarioMode.SUCCESS);

        gateway.notifyApproval(2L, "joao@email.com");

        assertEquals(1, gateway.sentEvents().size());
        assertTrue(gateway.sentEvents().getFirst().contains("APPROVED:2:joao@email.com"));
    }

    @Test
    void shouldRegisterRejectionEventWhenSuccessful() {
        ProgrammableNotificationGateway gateway = new ProgrammableNotificationGateway();
        gateway.setScenarioMode(ScenarioMode.SUCCESS);

        gateway.notifyRejection(3L, "ana@email.com");

        assertEquals(1, gateway.sentEvents().size());
        assertTrue(gateway.sentEvents().getFirst().contains("REJECTED:3:ana@email.com"));
    }

    @Test
    void shouldRegisterCancellationEventWhenSuccessful() {
        ProgrammableNotificationGateway gateway = new ProgrammableNotificationGateway();
        gateway.setScenarioMode(ScenarioMode.SUCCESS);

        gateway.notifyCancellation(4L, "carlos@email.com");

        assertEquals(1, gateway.sentEvents().size());
        assertTrue(gateway.sentEvents().getFirst().contains("CANCELLED:4:carlos@email.com"));
    }

    @Test
    void shouldThrowTimeoutException() {
        ProgrammableNotificationGateway gateway = new ProgrammableNotificationGateway();
        gateway.setScenarioMode(ScenarioMode.TIMEOUT);

        assertThrows(ExternalServiceException.class, () ->
                gateway.notifyCreation(1L, "maria@email.com")
        );
    }

    @Test
    void shouldThrowNetworkErrorException() {
        ProgrammableNotificationGateway gateway = new ProgrammableNotificationGateway();
        gateway.setScenarioMode(ScenarioMode.NETWORK_ERROR);

        assertThrows(ExternalServiceException.class, () ->
                gateway.notifyApproval(1L, "maria@email.com")
        );
    }

    @Test
    void shouldThrowServiceUnavailableException() {
        ProgrammableNotificationGateway gateway = new ProgrammableNotificationGateway();
        gateway.setScenarioMode(ScenarioMode.SERVICE_UNAVAILABLE);

        assertThrows(ExternalServiceException.class, () ->
                gateway.notifyRejection(1L, "maria@email.com")
        );
    }

    @Test
    void shouldThrowInvalidResponseException() {
        ProgrammableNotificationGateway gateway = new ProgrammableNotificationGateway();
        gateway.setScenarioMode(ScenarioMode.INVALID_RESPONSE);

        assertThrows(ExternalServiceException.class, () ->
                gateway.notifyCancellation(1L, "maria@email.com")
        );
    }

    @Test
    void shouldThrowUnexpectedFailureException() {
        ProgrammableNotificationGateway gateway = new ProgrammableNotificationGateway();
        gateway.setScenarioMode(ScenarioMode.UNEXPECTED_FAILURE);

        assertThrows(RuntimeException.class, () ->
                gateway.notifyCreation(1L, "maria@email.com")
        );
    }

    @Test
    void shouldReturnCurrentScenarioMode() {
        ProgrammableNotificationGateway gateway = new ProgrammableNotificationGateway();

        gateway.setScenarioMode(ScenarioMode.TIMEOUT);

        assertEquals(ScenarioMode.TIMEOUT, gateway.getScenarioMode());
    }
}