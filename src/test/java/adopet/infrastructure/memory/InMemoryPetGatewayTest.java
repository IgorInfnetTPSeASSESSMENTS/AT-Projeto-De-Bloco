package adopet.infrastructure.memory;

import adopet.domain.shelterandpet.*;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.ShelterGateway;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryPetGatewayTest {

    private static Shelter createShelter(String name) {
        return new Shelter(null, name, new PhoneNumber("31999999999"), new Email("a@b.com"));
    }

    private static Pet createPet(String name) {
        return new Pet(
                null,
                PetType.GATO,
                new PetName(name),
                "SRD",
                new AgeYears(2),
                "Branco",
                new WeightKg(3.2),
                PetStatus.AVAILABLE
        );
    }

    @Test
    void listPetsShouldRejectBlankShelterInput() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        assertThrows(InvalidUserInputException.class, () -> pets.listPets("   "));
        assertThrows(InvalidUserInputException.class, () -> pets.listPets(null));
    }

    @Test
    void registerPetShouldFailWhenShelterDoesNotExist_byId() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        assertThrows(EntityNotFoundException.class, () ->
                pets.registerPet("1", createPet("Mimi"))
        );
    }

    @Test
    void registerPetShouldFailWhenShelterDoesNotExist_byName() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        assertThrows(EntityNotFoundException.class, () ->
                pets.registerPet("Abrigo Inexistente", createPet("Mimi"))
        );
    }

    @Test
    void registerAndListPetsByShelterId() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        Pet created = pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));
        assertNotNull(created.id());

        List<Pet> list = pets.listPets(String.valueOf(s.id()));
        assertEquals(1, list.size());
        assertEquals("Mimi", list.get(0).name().value());
    }

    @Test
    void listPetsByShelterNameShouldWork() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        List<Pet> list = pets.listPets(" Abrigo X ");
        assertEquals(1, list.size());
    }

    @Test
    void findByIdShouldReturnEmptyWhenNullOrMissing() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        assertTrue(pets.findById(null).isEmpty());
        assertTrue(pets.findById(1L).isEmpty());
    }

    @Test
    void findShelterIdByPetIdShouldReturnEmptyWhenNullOrMissing() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        assertTrue(pets.findShelterIdByPetId(null).isEmpty());
        assertTrue(pets.findShelterIdByPetId(1L).isEmpty());
    }

    @Test
    void findShelterIdByPetIdShouldReturnShelterWhenExists() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        Pet created = pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        assertEquals(s.id(), pets.findShelterIdByPetId(created.id()).orElseThrow());
    }

    // ✅ NOVO: cobre branch findById quando existe
    @Test
    void findByIdShouldReturnPetWhenExists() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        Pet created = pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        assertTrue(pets.findById(created.id()).isPresent());
        assertEquals("Mimi", pets.findById(created.id()).get().name().value());
    }

    @Test
    void updatePetShouldReplaceInIndexAndReturnUpdated() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        Pet created = pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        Pet updated = new Pet(
                created.id(),
                PetType.CACHORRO,
                new PetName("Rex"),
                "SRD",
                new AgeYears(3),
                "Preto",
                new WeightKg(10.0),
                PetStatus.AVAILABLE
        );

        Pet result = pets.updatePet(created.id(), updated);

        assertEquals(created.id(), result.id());
        assertEquals(PetType.CACHORRO, result.type());
        assertEquals("Rex", result.name().value());

        // força o caminho do loop + if true + set + break
        List<Pet> list = pets.listPets(String.valueOf(s.id()));
        assertEquals(1, list.size());
        assertEquals(created.id(), list.get(0).id());
        assertEquals("Rex", list.get(0).name().value());
    }

    @Test
    void updatePetShouldThrowWhenNotFound() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        Pet updated = createPet("X");
        assertThrows(EntityNotFoundException.class, () -> pets.updatePet(999L, updated));
    }

    // ✅ NOVO: cobre branch "if (shelterId != null)" = FALSE (mapping removido)
    @Test
    void updatePetShouldWorkEvenWhenShelterMappingIsMissing_branchShelterIdNull() throws Exception {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        Pet created = pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        removeShelterMappingForPet(pets, created.id()); // força shelterId == null

        Pet updated = new Pet(
                created.id(),
                PetType.CACHORRO,
                new PetName("Rex"),
                "SRD",
                new AgeYears(3),
                "Preto",
                new WeightKg(10.0),
                PetStatus.AVAILABLE
        );

        Pet result = pets.updatePet(created.id(), updated);
        assertEquals("Rex", result.name().value());
        // não dá pra validar lista do abrigo aqui pq o mapping foi removido
    }

    // ✅ NOVO: cobre branch shelterId != null mas lista do abrigo ausente -> getOrDefault(... new ArrayList<>())
    @Test
    void updatePetShouldNotCrashWhenShelterListIsMissing_branchListDefault() throws Exception {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        Pet created = pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        removeShelterPetsList(pets, s.id()); // remove petsByShelterId entry, mas mantém mapping

        Pet updated = new Pet(
                created.id(),
                PetType.CACHORRO,
                new PetName("Rex"),
                "SRD",
                new AgeYears(3),
                "Preto",
                new WeightKg(10.0),
                PetStatus.AVAILABLE
        );

        Pet result = pets.updatePet(created.id(), updated);
        assertEquals("Rex", result.name().value());

        // byId foi atualizado sempre
        assertEquals("Rex", pets.findById(created.id()).orElseThrow().name().value());
    }

    @Test
    void updatePetStatusShouldUpdatePetAndShelterList() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        Pet created = pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        Pet updated = pets.updatePetStatus(created.id(), PetStatus.ADOPTED);

        assertEquals(PetStatus.ADOPTED, updated.status());
        assertEquals(PetStatus.ADOPTED, pets.findById(created.id()).orElseThrow().status());
        assertEquals(PetStatus.ADOPTED, pets.listPets(String.valueOf(s.id())).get(0).status());
    }

    @Test
    void updatePetStatusShouldThrowWhenPetDoesNotExist() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        assertThrows(EntityNotFoundException.class, () -> pets.updatePetStatus(999L, PetStatus.ADOPTED));
    }

    @Test
    void updatePetStatusShouldWorkWhenShelterMappingIsMissing() throws Exception {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        Pet created = pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        removeShelterMappingForPet(pets, created.id());

        Pet updated = pets.updatePetStatus(created.id(), PetStatus.ADOPTED);
        assertEquals(PetStatus.ADOPTED, updated.status());
    }

    @Test
    void updatePetStatusShouldWorkWhenShelterListIsMissing() throws Exception {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        Pet created = pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        removeShelterPetsList(pets, s.id());

        Pet updated = pets.updatePetStatus(created.id(), PetStatus.ADOPTED);
        assertEquals(PetStatus.ADOPTED, updated.status());
        assertEquals(PetStatus.ADOPTED, pets.findById(created.id()).orElseThrow().status());
    }

    @Test
    void updatePetStatusShouldNotChangeShelterListWhenPetIsNotInShelterList() throws Exception {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        Pet created = pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        replaceShelterPetsListWithDifferentPet(pets, s.id());

        Pet updated = pets.updatePetStatus(created.id(), PetStatus.ADOPTED);

        assertEquals(PetStatus.ADOPTED, updated.status());
        assertEquals(PetStatus.ADOPTED, pets.findById(created.id()).orElseThrow().status());
        List<Pet> list = pets.listPets(String.valueOf(s.id()));
        assertEquals(1, list.size());
        assertNotEquals(created.id(), list.get(0).id());
    }

    @Test
    void deletePetShouldRemoveFromBothStores() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        Pet created = pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        pets.deletePet(created.id());

        assertTrue(pets.findById(created.id()).isEmpty());
        assertTrue(pets.listPets(String.valueOf(s.id())).isEmpty());
    }

    @Test
    void deletePetShouldThrowWhenNotFound() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        assertThrows(EntityNotFoundException.class, () -> pets.deletePet(1L));
    }

    // ✅ NOVO: cobre branch shelterId == null no delete (não entra no if e não tenta remover lista)
    @Test
    void deletePetShouldWorkEvenWhenShelterMappingIsMissing_branchShelterIdNull() throws Exception {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        Pet created = pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        removeShelterMappingForPet(pets, created.id()); // força shelterId == null

        pets.deletePet(created.id());
        assertTrue(pets.findById(created.id()).isEmpty());
    }

    @Test
    void importPetsShouldCreatePetsAndSkipBlankLines() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        int created = pets.importPets(String.valueOf(s.id()), new ByteArrayInputStream("""
                GATO,Mimi,SRD,2,Branco,3.2

                CACHORRO,Rex,SRD,3,Preto,10.0
                """.getBytes(StandardCharsets.UTF_8)));
        assertEquals(2, created);

        assertEquals(2, pets.listPets(String.valueOf(s.id())).size());
    }

    @Test
    void importPetsShouldReturnZeroWhenCsvIsEmpty() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        int created = pets.importPets(String.valueOf(s.id()), new ByteArrayInputStream(new byte[0]));

        assertEquals(0, created);
    }

    @Test
    void importPetsShouldIgnoreExportedCsvHeader() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        int created = pets.importPets(String.valueOf(s.id()), new ByteArrayInputStream("""
                tipo,nome,raca,idade,cor,peso,status
                GATO,Mimi,SRD,2,Branco,3.2,AVAILABLE
                CACHORRO,Rex,SRD,3,Preto,10.0,AVAILABLE
                """.getBytes(StandardCharsets.UTF_8)));

        assertEquals(2, created);
        assertEquals(2, pets.listPets(String.valueOf(s.id())).size());
    }

    @Test
    void importPetsShouldRejectWhenLessThan6Columns() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        assertThrows(InvalidUserInputException.class, () ->
                pets.importPets(String.valueOf(s.id()), new ByteArrayInputStream("GATO,Mimi,SRD,2,Branco\n".getBytes(StandardCharsets.UTF_8)))
        );
    }

    @Test
    void importPetsShouldWrapUnexpectedErrorsWhenStreamFails() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class, () ->
                pets.importPets(String.valueOf(s.id()), new java.io.InputStream() {
                    @Override
                    public int read() {
                        throw new RuntimeException("boom");
                    }
                })
        );

        assertTrue(ex.getMessage().contains("Falha ao importar CSV"));
    }

    @Test
    void importPetsShouldUseUnknownErrorMessageWhenExceptionMessageIsNull() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class, () ->
                pets.importPets(String.valueOf(s.id()), new java.io.InputStream() {
                    @Override
                    public int read() throws java.io.IOException {
                        throw new java.io.IOException();
                    }
                })
        );

        assertTrue(ex.getMessage().contains("erro desconhecido"));
    }

    @Test
    void importPetsShouldWrapUnexpectedErrors_invalidNumber() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class, () ->
                pets.importPets(String.valueOf(s.id()), new ByteArrayInputStream("GATO,Mimi,SRD,idade_invalida,Branco,3.2\n".getBytes(StandardCharsets.UTF_8)))
        );

        assertTrue(ex.getMessage().contains("Falha ao importar CSV"));
    }

    @Test
    void importPetsShouldThrowWhenShelterDoesNotExist() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        assertThrows(EntityNotFoundException.class, () ->
                pets.importPets("1", new ByteArrayInputStream("x".getBytes(StandardCharsets.UTF_8)))
        );
    }

    // ----------------- helpers (reflection) -----------------

    @SuppressWarnings("unchecked")
    private static void removeShelterMappingForPet(InMemoryPetGateway gateway, Long petId) throws Exception {
        Field f = InMemoryPetGateway.class.getDeclaredField("shelterIdByPetId");
        f.setAccessible(true);
        Map<Long, Long> map = (Map<Long, Long>) f.get(gateway);
        map.remove(petId);
    }

    @SuppressWarnings("unchecked")
    private static void removeShelterPetsList(InMemoryPetGateway gateway, Long shelterId) throws Exception {
        Field f = InMemoryPetGateway.class.getDeclaredField("petsByShelterId");
        f.setAccessible(true);
        Map<Long, List<Pet>> map = (Map<Long, List<Pet>>) f.get(gateway);
        map.remove(shelterId);
    }

    @Test
    void updatePetShouldNotChangeShelterListWhenPetIsNotInShelterList_branchInnerIfFalse() throws Exception {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        Pet created = pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        // força: a lista do abrigo NÃO contém o pet com o id "created.id()"
        // (mantém mapping shelterIdByPetId, mas troca a lista)
        replaceShelterPetsListWithDifferentPet(pets, s.id());

        Pet updated = new Pet(
                created.id(),
                PetType.CACHORRO,
                new PetName("Rex"),
                "SRD",
                new AgeYears(3),
                "Preto",
                new WeightKg(10.0),
                PetStatus.AVAILABLE
        );

        Pet result = pets.updatePet(created.id(), updated);

        // byId sempre atualiza
        assertEquals("Rex", pets.findById(created.id()).orElseThrow().name().value());

        // mas a lista do abrigo não foi alterada (porque não encontrou o id no loop)
        List<Pet> list = pets.listPets(String.valueOf(s.id()));
        assertEquals(1, list.size());
        assertNotEquals(created.id(), list.get(0).id()); // garante que não foi substituído
    }

    @SuppressWarnings("unchecked")
    private static void replaceShelterPetsListWithDifferentPet(InMemoryPetGateway gateway, Long shelterId) throws Exception {
        Field f = InMemoryPetGateway.class.getDeclaredField("petsByShelterId");
        f.setAccessible(true);
        Map<Long, List<Pet>> map = (Map<Long, List<Pet>>) f.get(gateway);

        Pet other = new Pet(
                9999L,
                PetType.GATO,
                new PetName("Outro"),
                "SRD",
                new AgeYears(1),
                "Branco",
                new WeightKg(1.0),
                PetStatus.AVAILABLE
        );

        map.put(shelterId, new java.util.ArrayList<>(List.of(other)));
    }

    @Test
    void exportPetsShouldCreateCsvWithHeaderAndLines() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));

        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));
        pets.registerPet(String.valueOf(s.id()), new Pet(
                null,
                PetType.CACHORRO,
                new PetName("Rex"),
                "SRD",
                new AgeYears(3),
                "Preto",
                new WeightKg(10.0),
                PetStatus.AVAILABLE
        ));

        String csv = new String(pets.exportPets(String.valueOf(s.id())), StandardCharsets.UTF_8);
        assertTrue(csv.contains("tipo,nome,raca,idade,cor,peso"));
        assertTrue(csv.contains("GATO,Mimi,SRD,2,Branco"));
        assertTrue(csv.contains("CACHORRO,Rex,SRD,3,Preto"));
    }

    @Test
    void exportPetsShouldReturnOnlyHeaderWhenThereAreNoPets() {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);

        String csv = new String(pets.exportPets(String.valueOf(s.id())), StandardCharsets.UTF_8);

        assertEquals("tipo,nome,raca,idade,cor,peso,status\n", csv.replace("\r\n", "\n"));
    }

    @Test
    void exportPetsShouldWrapUnexpectedErrorsWhenStoredPetIsCorrupted() throws Exception {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        corruptShelterPetsListWithNullPet(pets, s.id());

        InvalidUserInputException ex = assertThrows(
                InvalidUserInputException.class,
                () -> pets.exportPets(String.valueOf(s.id()))
        );

        assertTrue(ex.getMessage().contains("Falha ao exportar CSV"));
    }

    @Test
    void exportPetsShouldUseUnknownErrorMessageWhenExceptionMessageIsNull() throws Exception {
        ShelterGateway shelters = new InMemoryShelterGateway();
        Shelter s = shelters.registerShelter(createShelter("Abrigo X"));
        InMemoryPetGateway pets = new InMemoryPetGateway(shelters);
        pets.registerPet(String.valueOf(s.id()), createPet("Mimi"));

        corruptShelterPetsListWithExplodingIterator(pets, s.id());

        InvalidUserInputException ex = assertThrows(
                InvalidUserInputException.class,
                () -> pets.exportPets(String.valueOf(s.id()))
        );

        assertTrue(ex.getMessage().contains("erro desconhecido"));
    }

    @SuppressWarnings("unchecked")
    private static void corruptShelterPetsListWithNullPet(InMemoryPetGateway gateway, Long shelterId) throws Exception {
        Field f = InMemoryPetGateway.class.getDeclaredField("petsByShelterId");
        f.setAccessible(true);
        Map<Long, List<Pet>> map = (Map<Long, List<Pet>>) f.get(gateway);
        List<Pet> corrupted = new java.util.ArrayList<>();
        corrupted.add(null);
        map.put(shelterId, corrupted);
    }

    @SuppressWarnings("unchecked")
    private static void corruptShelterPetsListWithExplodingIterator(InMemoryPetGateway gateway, Long shelterId) throws Exception {
        Field f = InMemoryPetGateway.class.getDeclaredField("petsByShelterId");
        f.setAccessible(true);
        Map<Long, List<Pet>> map = (Map<Long, List<Pet>>) f.get(gateway);
        map.put(shelterId, new java.util.AbstractList<>() {
            @Override
            public Pet get(int index) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int size() {
                return 1;
            }

            @Override
            public java.util.Iterator<Pet> iterator() {
                throw new RuntimeException();
            }
        });
    }
}
