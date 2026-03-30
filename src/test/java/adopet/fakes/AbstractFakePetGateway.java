package adopet.fakes;

import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetStatus;
import adopet.gateway.PetGateway;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public abstract class AbstractFakePetGateway implements PetGateway {
    @Override public List<Pet> listPets(String shelterIdOrName) { throw new UnsupportedOperationException(); }
    @Override public Optional<Pet> findById(Long petId) { throw new UnsupportedOperationException(); }
    @Override public Pet registerPet(String shelterIdOrName, Pet pet) { throw new UnsupportedOperationException(); }
    @Override public Pet updatePet(Long petId, Pet updated) { throw new UnsupportedOperationException(); }
    @Override public void deletePet(Long petId) { throw new UnsupportedOperationException(); }
    @Override public int importPets(String shelterIdOrName, InputStream csvInputStream) { throw new UnsupportedOperationException(); }
    @Override public byte[] exportPets(String shelterIdOrName) { throw new UnsupportedOperationException(); }
    @Override public Optional<Long> findShelterIdByPetId(Long petId) { throw new UnsupportedOperationException(); }
    @Override public Pet updatePetStatus(Long petId, PetStatus status) { throw new UnsupportedOperationException(); }

}
