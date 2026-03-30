package adopet.gateway;

import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetStatus;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/*
 * PetGateway: abstração para persistência/serviço remoto de pets.
 * Implementações devem fornecer CRUD básico e capacidades de importação/exportação em CSV.
 * Notas de contrato:
 * - listPets: aceita id ou nome do abrigo (string) e retorna os pets desse abrigo.
 * - findById: retorna Optional.empty() quando não encontrado.
 * - register/update/delete: devem lançar exceções de domínio quando aplicável (ex.: EntityNotFoundException).
 * - importPets/exportPets: devem tratar o formato CSV e propagar exceções significativas em caso de falha.
 */
public interface PetGateway {

    // READ
    List<Pet> listPets(String shelterIdOrName);
    Optional<Pet> findById(Long petId);
    Optional<Long> findShelterIdByPetId(Long petId);

    // CREATE
    Pet registerPet(String shelterIdOrName, Pet pet);

    // UPDATE
    Pet updatePet(Long petId, Pet updated);
    Pet updatePetStatus(Long petId, PetStatus status);

    // DELETE
    void deletePet(Long petId);

    // IMPORT (CREATE em lote)
    int importPets(String shelterIdOrName, InputStream csvInputStream);

    // EXPORT (READ em lote)
    byte[] exportPets(String shelterIdOrName);
}
