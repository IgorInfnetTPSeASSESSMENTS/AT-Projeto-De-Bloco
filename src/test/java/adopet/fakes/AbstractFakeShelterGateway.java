package adopet.fakes;

import adopet.domain.shelterandpet.Email;
import adopet.domain.shelterandpet.PhoneNumber;
import adopet.domain.shelterandpet.Shelter;
import adopet.gateway.ShelterGateway;

import java.util.List;
import java.util.Optional;

public abstract class AbstractFakeShelterGateway implements ShelterGateway {
    @Override public List<Shelter> listShelters() { throw new UnsupportedOperationException(); }
    @Override public Optional<Shelter> findById(Long id) { throw new UnsupportedOperationException(); }
    @Override public Optional<Shelter> findByName(String name) { throw new UnsupportedOperationException(); }
    @Override public Shelter registerShelter(Shelter shelter) { throw new UnsupportedOperationException(); }
    @Override public Shelter updateShelter(Long id, String name, PhoneNumber phoneNumber, Email email) { throw new UnsupportedOperationException(); }
    @Override public void deleteShelter(Long id) { throw new UnsupportedOperationException(); }
}
