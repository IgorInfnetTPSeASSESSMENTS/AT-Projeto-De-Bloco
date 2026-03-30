package adopet.application.adoption;

import adopet.domain.adoption.*;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.infrastructure.memory.InMemoryAdoptionRequestGateway;
import adopet.infrastructure.memory.ProgrammableNotificationGateway;
import adopet.infrastructure.memory.ScenarioMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RejectAdoptionRequestCommandHandlerTest {

    private InMemoryAdoptionRequestGateway gateway;
    private ProgrammableNotificationGateway notificationGateway;
    private RejectAdoptionRequestCommandHandler handler;
    private AdoptionRequest created;

    @BeforeEach
    void setUp() {
        gateway = new InMemoryAdoptionRequestGateway();
        notificationGateway = new ProgrammableNotificationGateway();
        handler = new RejectAdoptionRequestCommandHandler(gateway, notificationGateway);

        created = gateway.registerAdoptionRequest(
                AdoptionRequest.newRequest(
                        1L, 10L,
                        new ApplicantName("Maria da Silva"),
                        new ApplicantEmail("maria@email.com"),
                        new ApplicantPhone("31999999999"),
                        new ApplicantDocument("12345678900"),
                        HousingType.HOUSE,
                        true,
                        new ReasonText("Quero adotar com responsabilidade e carinho."),
                        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW
                )
        );
    }

    @Test
    void shouldRejectRequestAndSendNotification() {
        AdoptionRequestOperationResult result = handler.execute(created.id());

        assertEquals(AdoptionRequestStatus.REJECTED, result.adoptionRequest().status());
        assertTrue(result.notificationSent());
        assertEquals(AnalysisExecutionStatus.SUCCESS, result.analysisExecutionStatus());
    }

    @Test
    void shouldRejectEvenWhenNotificationFails() {
        notificationGateway.setScenarioMode(ScenarioMode.UNEXPECTED_FAILURE);

        AdoptionRequestOperationResult result = handler.execute(created.id());

        assertEquals(AdoptionRequestStatus.REJECTED, result.adoptionRequest().status());
        assertFalse(result.notificationSent());
    }

    @Test
    void shouldFailWhenIdIsNull() {
        assertThrows(InvalidUserInputException.class, () -> handler.execute(null));
    }

    @Test
    void shouldFailWhenRequestDoesNotExist() {
        assertThrows(EntityNotFoundException.class, () -> handler.execute(999L));
    }
}