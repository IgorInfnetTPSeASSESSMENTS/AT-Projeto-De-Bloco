package adopet.application.adoption;

import adopet.domain.adoption.*;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.infrastructure.memory.InMemoryAdoptionRequestGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetAdoptionRequestDetailsQueryTest {

    private InMemoryAdoptionRequestGateway gateway;
    private GetAdoptionRequestDetailsQuery query;
    private AdoptionRequest created;

    @BeforeEach
    void setUp() {
        gateway = new InMemoryAdoptionRequestGateway();
        query = new GetAdoptionRequestDetailsQuery(gateway);

        created = gateway.registerAdoptionRequest(
                AdoptionRequest.newRequest(
                        1L,
                        10L,
                        new ApplicantName("Maria"),
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
    void shouldReturnRequestDetails() {
        AdoptionRequest result = query.execute(created.id());
        assertEquals(created.id(), result.id());
    }

    @Test
    void shouldFailWhenIdIsNull() {
        assertThrows(InvalidUserInputException.class, () -> query.execute(null));
    }

    @Test
    void shouldFailWhenRequestDoesNotExist() {
        assertThrows(EntityNotFoundException.class, () -> query.execute(999L));
    }
}