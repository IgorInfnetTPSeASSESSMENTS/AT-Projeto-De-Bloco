package adopet.infrastructure.memory;

import adopet.domain.shelterandpet.Email;
import adopet.domain.shelterandpet.PhoneNumber;
import adopet.domain.shelterandpet.Shelter;
import adopet.exception.DuplicateEntityException;
import adopet.exception.EntityNotFoundException;
import adopet.gateway.ShelterGateway;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/*
 * InMemoryShelterGateway: implementação em memória simples de ShelterGateway para testes e demonstrações.
 * Notas de design:
 * - Mantém a ordem de inserção usando LinkedHashMap para obtenção previsível em testes.
 * - Usa um mapa name->id (lower-case) para detectar duplicidades e permitir lookup por nome.
 * - Lança exceções de domínio específicas para cenários de duplicidade e não-encontrado.
 */
public class InMemoryShelterGateway implements ShelterGateway {

    private final Map<Long, Shelter> byId = new LinkedHashMap<>();
    private final Map<String, Long> idByNameLower = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public List<Shelter> listShelters() {
        return List.copyOf(byId.values());
    }

    @Override
    public Optional<Shelter> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<Shelter> findByName(String name) {
        if (name == null) return Optional.empty();
        Long id = idByNameLower.get(name.trim().toLowerCase());
        if (id == null) return Optional.empty();
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Shelter registerShelter(Shelter shelter) {
        String key = shelter.name().trim().toLowerCase();
        if (idByNameLower.containsKey(key)) {
            throw new DuplicateEntityException("Já existe um abrigo com esse nome.");
        }

        long id = sequence.getAndIncrement();
        Shelter created = new Shelter(id, shelter.name().trim(), shelter.phoneNumber(), shelter.email());

        byId.put(id, created);
        idByNameLower.put(key, id);

        return created;
    }

    @Override
    public Shelter updateShelter(Long id, String name, PhoneNumber phoneNumber, Email email) {
        Shelter existing = byId.get(id);
        if (existing == null) {
            throw new EntityNotFoundException("Abrigo não encontrado (id=" + id + ").");
        }

        String newName = name.trim();
        String newKey = newName.toLowerCase();

        // Se mudou nome, checa duplicidade
        String oldKey = existing.name().trim().toLowerCase();
        if (!oldKey.equals(newKey) && idByNameLower.containsKey(newKey)) {
            throw new DuplicateEntityException("Já existe um abrigo com esse nome.");
        }

        Shelter updated = new Shelter(existing.id(), newName, phoneNumber, email);

        byId.put(existing.id(), updated);
        idByNameLower.remove(oldKey);
        idByNameLower.put(newKey, existing.id());

        return updated;
    }

    @Override
    public void deleteShelter(Long id) {
        Shelter existing = byId.remove(id);
        if (existing == null) {
            throw new EntityNotFoundException("Abrigo não encontrado (id=" + id + ").");
        }
        idByNameLower.remove(existing.name().trim().toLowerCase());
    }
}
