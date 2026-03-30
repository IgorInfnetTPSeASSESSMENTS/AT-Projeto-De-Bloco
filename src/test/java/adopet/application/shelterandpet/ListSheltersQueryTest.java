package adopet.application.shelterandpet;

import adopet.domain.shelterandpet.Email;
import adopet.domain.shelterandpet.PhoneNumber;
import adopet.domain.shelterandpet.Shelter;
import adopet.exception.RemoteServiceException;
import adopet.fakes.AbstractFakeShelterGateway;
import adopet.gateway.ShelterGateway;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListSheltersQueryTest {

    @Test
    void shouldReturnListFromGateway() {
        ShelterGateway gateway = new AbstractFakeShelterGateway() {
            @Override public List<Shelter> listShelters() {
                return List.of(new Shelter(1L, "A1", new PhoneNumber("31999999999"), new Email("a@b.com")));
            }
            @Override public Optional<Shelter> findById(Long id) { return Optional.empty(); }
            @Override public Optional<Shelter> findByName(String name) { return Optional.empty(); }
        };

        ListSheltersQuery query = new ListSheltersQuery(gateway);

        List<Shelter> shelters = query.execute();

        assertEquals(1, shelters.size());
        assertEquals("A1", shelters.get(0).name());
    }

    @Test
    void shouldPropagateGatewayException() {
        ShelterGateway gateway = new AbstractFakeShelterGateway() {
            @Override public List<Shelter> listShelters() {
                throw new RemoteServiceException("boom", 500, "err");
            }
            @Override public Optional<Shelter> findById(Long id) { return Optional.empty(); }
            @Override public Optional<Shelter> findByName(String name) { return Optional.empty(); }
        };

        ListSheltersQuery query = new ListSheltersQuery(gateway);

        assertThrows(RemoteServiceException.class, query::execute);
    }
}