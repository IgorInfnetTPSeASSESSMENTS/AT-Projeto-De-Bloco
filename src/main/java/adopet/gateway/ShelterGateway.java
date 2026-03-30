package adopet.gateway;

import adopet.domain.shelterandpet.Email;
import adopet.domain.shelterandpet.PhoneNumber;
import adopet.domain.shelterandpet.Shelter;

import java.util.List;
import java.util.Optional;

public interface ShelterGateway {

    // READ
    List<Shelter> listShelters();
    Optional<Shelter> findById(Long id);
    Optional<Shelter> findByName(String name);

    // CREATE
    Shelter registerShelter(Shelter shelter);

    // UPDATE
    Shelter updateShelter(Long id, String name, PhoneNumber phoneNumber, Email email);

    // DELETE
    void deleteShelter(Long id);
}