package adopet.fakes;

import adopet.domain.shelterandpet.Email;
import adopet.domain.shelterandpet.PhoneNumber;
import adopet.domain.shelterandpet.Shelter;
import adopet.gateway.ShelterGateway;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FakeShelterGateway implements ShelterGateway {

    private final List<Shelter> storage = new ArrayList<>();

    public void seed(Shelter shelter) {
        storage.add(shelter);
    }

    @Override
    public List<Shelter> listShelters() {
        return List.copyOf(storage);
    }

    @Override
    public Optional<Shelter> findById(Long id) {
        return storage.stream()
                .filter(s -> Objects.equals(s.id(), id))
                .findFirst();
    }

    @Override
    public Optional<Shelter> findByName(String name) {
        if (name == null) return Optional.empty();
        String trimmed = name.trim();
        return storage.stream()
                .filter(s -> s.name().equalsIgnoreCase(trimmed))
                .findFirst();
    }

    @Override
    public Shelter registerShelter(Shelter shelter) {
        storage.add(shelter);

        long newId = storage.size();
        return new Shelter(newId, shelter.name(), shelter.phoneNumber(), shelter.email());
    }

    @Override
    public Shelter updateShelter(Long id, String name, PhoneNumber phoneNumber, Email email) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteShelter(Long id) {
        throw new UnsupportedOperationException();
    }
}