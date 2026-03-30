package adopet.application.adoption;

import adopet.domain.adoption.*;
import adopet.infrastructure.memory.InMemoryAdoptionRequestGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListAdoptionRequestsQueryTest {

    private InMemoryAdoptionRequestGateway gateway;
    private ListAdoptionRequestsQuery query;

    @BeforeEach
    void setUp() {
        gateway = new InMemoryAdoptionRequestGateway();
        query = new ListAdoptionRequestsQuery(gateway);

        gateway.registerAdoptionRequest(
                AdoptionRequest.newRequest(
                        1L, 10L,
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

        gateway.registerAdoptionRequest(
                AdoptionRequest.newRequest(
                        2L, 20L,
                        new ApplicantName("João"),
                        new ApplicantEmail("joao@email.com"),
                        new ApplicantPhone("31888888888"),
                        new ApplicantDocument("98765432100"),
                        HousingType.APARTMENT,
                        false,
                        new ReasonText("Quero adotar de forma consciente e responsável."),
                        EligibilityAnalysis.ELIGIBLE
                )
        );
    }

    @Test
    void shouldListAllRequests() {
        List<AdoptionRequest> requests = query.execute(null, null, null);
        assertEquals(2, requests.size());
    }

    @Test
    void shouldFilterByStatus() {
        List<AdoptionRequest> requests = query.execute("UNDER_REVIEW", null, null);
        assertEquals(1, requests.size());
        assertEquals(AdoptionRequestStatus.UNDER_REVIEW, requests.getFirst().status());
    }

    @Test
    void shouldFilterByPetId() {
        List<AdoptionRequest> requests = query.execute(null, 1L, null);
        assertEquals(1, requests.size());
        assertEquals(1L, requests.getFirst().petId());
    }

    @Test
    void shouldFilterByShelterId() {
        List<AdoptionRequest> requests = query.execute(null, null, 20L);
        assertEquals(1, requests.size());
        assertEquals(20L, requests.getFirst().shelterId());
    }
}