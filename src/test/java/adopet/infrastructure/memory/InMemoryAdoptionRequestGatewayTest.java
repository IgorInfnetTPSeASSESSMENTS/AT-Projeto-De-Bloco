package adopet.infrastructure.memory;

import adopet.domain.adoption.*;
import adopet.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAdoptionRequestGatewayTest {

    private InMemoryAdoptionRequestGateway gateway;

    @BeforeEach
    void setUp() {
        gateway = new InMemoryAdoptionRequestGateway();
    }

    @Test
    void shouldRegisterAndFindRequestById() {
        AdoptionRequest created = gateway.registerAdoptionRequest(newRequest(
                1L,
                10L,
                "Maria",
                "maria@email.com",
                "31999999999",
                "12345678900"
        ));

        assertNotNull(created.id());
        assertTrue(gateway.findById(created.id()).isPresent());
    }

    @Test
    void shouldReturnEmptyWhenFindByIdReceivesNull() {
        assertTrue(gateway.findById(null).isEmpty());
    }

    @Test
    void shouldListAllRequestsWhenFiltersAreNull() {
        gateway.registerAdoptionRequest(newRequest(1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900"));
        gateway.registerAdoptionRequest(newRequest(2L, 20L, "João", "joao@email.com", "31888888888", "98765432100"));

        List<AdoptionRequest> requests = gateway.listAdoptionRequests(null, null, null);

        assertEquals(2, requests.size());
    }

    @Test
    void shouldFilterByPetId() {
        gateway.registerAdoptionRequest(newRequest(1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900"));
        gateway.registerAdoptionRequest(newRequest(2L, 20L, "João", "joao@email.com", "31888888888", "98765432100"));

        List<AdoptionRequest> requests = gateway.listAdoptionRequests(null, 2L, null);

        assertEquals(1, requests.size());
        assertEquals(2L, requests.getFirst().petId());
    }

    @Test
    void shouldFilterByShelterId() {
        gateway.registerAdoptionRequest(newRequest(1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900"));
        gateway.registerAdoptionRequest(newRequest(2L, 20L, "João", "joao@email.com", "31888888888", "98765432100"));

        List<AdoptionRequest> requests = gateway.listAdoptionRequests(null, null, 10L);

        assertEquals(1, requests.size());
        assertEquals(10L, requests.getFirst().shelterId());
    }

    @Test
    void shouldFilterByStatus() {
        AdoptionRequest created = gateway.registerAdoptionRequest(newRequest(
                1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900"
        ));

        gateway.registerAdoptionRequest(newRequest(
                2L, 20L, "João", "joao@email.com", "31888888888", "98765432100"
        ));

        AdoptionRequest approved = created.approve();
        gateway.updateAdoptionRequest(created.id(), approved);

        List<AdoptionRequest> approvedRequests = gateway.listAdoptionRequests("APPROVED", null, null);

        assertEquals(1, approvedRequests.size());
        assertEquals(AdoptionRequestStatus.APPROVED, approvedRequests.getFirst().status());
    }

    @Test
    void shouldUpdateRequest() {
        AdoptionRequest created = gateway.registerAdoptionRequest(newRequest(
                1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900"
        ));

        AdoptionRequest updated = created.updateDraft(
                5L,
                50L,
                new ApplicantName("Maria Editada"),
                new ApplicantEmail("maria.editada@email.com"),
                new ApplicantPhone("31777777777"),
                new ApplicantDocument("11122233344"),
                HousingType.APARTMENT,
                false,
                new ReasonText("Agora tenho uma nova justificativa válida.")
        );

        AdoptionRequest result = gateway.updateAdoptionRequest(created.id(), updated);

        assertEquals("Maria Editada", result.applicantName().value());
        assertEquals(5L, result.petId());
        assertEquals(50L, result.shelterId());
        assertEquals(created.createdAt(), result.createdAt());
    }

    @Test
    void shouldFailWhenUpdatingNonExistingRequest() {
        AdoptionRequest request = newRequest(
                1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900"
        );

        assertThrows(EntityNotFoundException.class, () ->
                gateway.updateAdoptionRequest(999L, request)
        );
    }

    @Test
    void shouldDeleteRequest() {
        AdoptionRequest created = gateway.registerAdoptionRequest(newRequest(
                1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900"
        ));

        gateway.deleteAdoptionRequest(created.id());

        assertTrue(gateway.findById(created.id()).isEmpty());
    }

    @Test
    void shouldFailWhenDeletingNonExistingRequest() {
        assertThrows(EntityNotFoundException.class, () ->
                gateway.deleteAdoptionRequest(999L)
        );
    }

    @Test
    void shouldReturnTrueWhenActiveRequestExistsForPetAndDocument() {
        gateway.registerAdoptionRequest(newRequest(
                1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900"
        ));

        boolean exists = gateway.existsActiveRequestForPetAndDocument(1L, "12345678900");

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenOnlyCancelledRequestExistsForPetAndDocument() {
        AdoptionRequest created = gateway.registerAdoptionRequest(newRequest(
                1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900"
        ));

        gateway.updateAdoptionRequest(created.id(), created.cancel());

        boolean exists = gateway.existsActiveRequestForPetAndDocument(1L, "12345678900");

        assertFalse(exists);
    }

    @Test
    void shouldClearAllData() {
        gateway.registerAdoptionRequest(newRequest(
                1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900"
        ));

        gateway.clear();

        assertTrue(gateway.listAdoptionRequests(null, null, null).isEmpty());

        AdoptionRequest createdAfterClear = gateway.registerAdoptionRequest(newRequest(
                2L, 20L, "João", "joao@email.com", "31888888888", "98765432100"
        ));

        assertEquals(1L, createdAfterClear.id());
    }

    @Test
    void shouldListRequestsWhenStatusFilterIsBlank() {
        gateway.registerAdoptionRequest(newRequest(
                1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900"
        ));
        gateway.registerAdoptionRequest(newRequest(
                2L, 20L, "João", "joao@email.com", "31888888888", "98765432100"
        ));

        List<AdoptionRequest> requests = gateway.listAdoptionRequests("   ", null, null);

        assertEquals(2, requests.size());
    }

    @Test
    void shouldListRequestsWhenStatusMatchesUnderReview() {
        AdoptionRequest created = gateway.registerAdoptionRequest(
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
                        EligibilityAnalysis.ELIGIBLE
                )
        );

        List<AdoptionRequest> requests = gateway.listAdoptionRequests("UNDER_REVIEW", null, null);

        assertEquals(1, requests.size());
        assertEquals(AdoptionRequestStatus.UNDER_REVIEW, requests.getFirst().status());
        assertEquals(created.id(), requests.getFirst().id());
    }

    @Test
    void shouldReturnTrueWhenUnderReviewRequestExistsForPetAndDocument() {
        gateway.registerAdoptionRequest(
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
                        EligibilityAnalysis.ELIGIBLE
                )
        );

        boolean exists = gateway.existsActiveRequestForPetAndDocument(1L, "12345678900");

        assertTrue(exists);
    }

    @Test
    void shouldReturnTrueWhenApprovedRequestExistsForPetAndDocument() {
        AdoptionRequest created = gateway.registerAdoptionRequest(newRequest(
                1L, 10L, "Maria", "maria@email.com", "31999999999", "12345678900"
        ));

        gateway.updateAdoptionRequest(created.id(), created.approve());

        boolean exists = gateway.existsActiveRequestForPetAndDocument(1L, "12345678900");

        assertTrue(exists);
    }

    private AdoptionRequest newRequest(
            Long petId,
            Long shelterId,
            String applicantName,
            String applicantEmail,
            String applicantPhone,
            String applicantDocument
    ) {
        return AdoptionRequest.newRequest(
                petId,
                shelterId,
                new ApplicantName(applicantName),
                new ApplicantEmail(applicantEmail),
                new ApplicantPhone(applicantPhone),
                new ApplicantDocument(applicantDocument),
                HousingType.HOUSE,
                true,
                new ReasonText("Quero adotar com responsabilidade e carinho."),
                EligibilityAnalysis.REQUIRES_MANUAL_REVIEW
        );
    }
}