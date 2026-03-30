package adopet.infrastructure.memory;

import adopet.domain.shelterandpet.AgeYears;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetName;
import adopet.domain.shelterandpet.PetStatus;
import adopet.domain.shelterandpet.PetType;
import adopet.domain.shelterandpet.Shelter;
import adopet.domain.shelterandpet.WeightKg;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import adopet.gateway.PetGateway;
import adopet.gateway.ShelterGateway;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryPetGateway implements PetGateway {
  private static final String CSV_HEADER = "tipo,nome,raca,idade,cor,peso,status";

  private final ShelterGateway shelterGateway;
  private final AtomicLong petSequence = new AtomicLong(1);

  private final Map<Long, List<Pet>> petsByShelterId = new HashMap<>();
  private final Map<Long, Long> shelterIdByPetId = new HashMap<>();
  private final Map<Long, Pet> byId = new LinkedHashMap<>();

  public InMemoryPetGateway(ShelterGateway shelterGateway) {
    this.shelterGateway = shelterGateway;
  }

  @Override
  public List<Pet> listPets(String shelterIdOrName) {
    long shelterId = resolveShelterId(shelterIdOrName);
    return List.copyOf(petsByShelterId.getOrDefault(shelterId, List.of()));
  }

  @Override
  public Optional<Pet> findById(Long petId) {
    if (petId == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(byId.get(petId));
  }

  @Override
  public Optional<Long> findShelterIdByPetId(Long petId) {
    if (petId == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(shelterIdByPetId.get(petId));
  }

  @Override
  public Pet registerPet(String shelterIdOrName, Pet pet) {
    long shelterId = resolveShelterId(shelterIdOrName);

    long id = petSequence.getAndIncrement();
    Pet created = new Pet(
        id,
        pet.type(),
        pet.name(),
        pet.breed(),
        pet.age(),
        pet.color(),
        pet.weight(),
        pet.status());

    byId.put(id, created);
    shelterIdByPetId.put(id, shelterId);
    petsByShelterId.computeIfAbsent(shelterId, k -> new ArrayList<>()).add(created);

    return created;
  }

  @Override
  public Pet updatePet(Long petId, Pet updated) {
    Pet existing = byId.get(petId);
    if (existing == null) {
      throw new EntityNotFoundException("Pet não encontrado (id=" + petId + ").");
    }

    Pet newPet = new Pet(
        existing.id(),
        updated.type(),
        updated.name(),
        updated.breed(),
        updated.age(),
        updated.color(),
        updated.weight(),
        updated.status());

    byId.put(existing.id(), newPet);

    Long shelterId = shelterIdByPetId.get(existing.id());
    if (shelterId != null) {
      List<Pet> list = petsByShelterId.getOrDefault(shelterId, new ArrayList<>());
      for (int i = 0; i < list.size(); i++) {
        if (Objects.equals(list.get(i).id(), existing.id())) {
          list.set(i, newPet);
          break;
        }
      }
    }

    return newPet;
  }

  @Override
  public Pet updatePetStatus(Long petId, PetStatus status) {
    Pet existing = byId.get(petId);
    if (existing == null) {
      throw new EntityNotFoundException("Pet não encontrado (id=" + petId + ").");
    }

    Pet updated = existing.withStatus(status);
    byId.put(existing.id(), updated);

    Long shelterId = shelterIdByPetId.get(existing.id());
    if (shelterId != null) {
      List<Pet> list = petsByShelterId.getOrDefault(shelterId, new ArrayList<>());
      for (int i = 0; i < list.size(); i++) {
        if (Objects.equals(list.get(i).id(), existing.id())) {
          list.set(i, updated);
          break;
        }
      }
    }

    return updated;
  }

  @Override
  public void deletePet(Long petId) {
    Pet existing = byId.remove(petId);
    if (existing == null) {
      throw new EntityNotFoundException("Pet não encontrado (id=" + petId + ").");
    }

    Long shelterId = shelterIdByPetId.remove(petId);
    if (shelterId != null) {
      List<Pet> list = petsByShelterId.getOrDefault(shelterId, new ArrayList<>());
      list.removeIf(p -> Objects.equals(p.id(), petId));
    }
  }

  @Override
  public int importPets(String shelterIdOrName, InputStream csvInputStream) {
    long shelterId = resolveShelterId(shelterIdOrName);

    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(csvInputStream, StandardCharsets.UTF_8))) {
      int created = 0;
      String line;

      while ((line = reader.readLine()) != null) {
        if (line.isBlank() || isCsvHeader(line)) {
          continue;
        }

        String[] parts = line.split(",");
        if (parts.length < 6) {
          throw new InvalidUserInputException("CSV inválido: esperado 6 colunas.");
        }

        String type = parts[0].trim();
        String name = parts[1].trim();
        String breed = parts[2].trim();
        int age = Integer.parseInt(parts[3].trim());
        String color = parts[4].trim();
        double weight = Double.parseDouble(parts[5].trim());

        Pet pet = new Pet(
            null,
            PetType.from(type),
            new PetName(name),
            breed,
            new AgeYears(age),
            color,
            new WeightKg(weight),
            PetStatus.AVAILABLE);

        long id = petSequence.getAndIncrement();
        Pet createdPet = new Pet(
            id,
            pet.type(),
            pet.name(),
            pet.breed(),
            pet.age(),
            pet.color(),
            pet.weight(),
            pet.status());

        byId.put(id, createdPet);
        shelterIdByPetId.put(id, shelterId);
        petsByShelterId.computeIfAbsent(shelterId, k -> new ArrayList<>()).add(createdPet);

        created++;
      }

      return created;
    } catch (InvalidUserInputException e) {
      throw e;
    } catch (Exception e) {
      throw new InvalidUserInputException(
          "Falha ao importar CSV: "
              + (e.getMessage() == null ? "erro desconhecido" : e.getMessage()));
    }
  }

  private boolean isCsvHeader(String line) {
    return CSV_HEADER.equalsIgnoreCase(line.trim());
  }

  private long resolveShelterId(String shelterIdOrName) {
    if (shelterIdOrName == null || shelterIdOrName.isBlank()) {
      throw new InvalidUserInputException("Id ou nome do abrigo não pode ser vazio.");
    }

    String raw = shelterIdOrName.trim();

    try {
      long id = Long.parseLong(raw);
      if (shelterGateway.findById(id).isEmpty()) {
        throw new EntityNotFoundException("Abrigo não encontrado (id=" + id + ").");
      }
      return id;
    } catch (NumberFormatException ignored) {
      return shelterGateway.findByName(raw)
          .map(Shelter::id)
          .orElseThrow(
              () -> new EntityNotFoundException("Abrigo não encontrado (nome=" + raw + ")."));
    }
  }

  @Override
  public byte[] exportPets(String shelterIdOrName) {
    long shelterId = resolveShelterId(shelterIdOrName);

    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedWriter writer =
            new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
      List<Pet> pets = petsByShelterId.getOrDefault(shelterId, List.of());

      writer.write(CSV_HEADER);
      writer.newLine();

      for (Pet p : pets) {
        String line =
            String.format(
                Locale.US,
                "%s,%s,%s,%d,%s,%.2f,%s",
                p.type().name(),
                p.name().value(),
                p.breed(),
                p.age().value(),
                p.color(),
                p.weight().value(),
                p.status().name());
        writer.write(line);
        writer.newLine();
      }

      writer.flush();
      return outputStream.toByteArray();

    } catch (Exception e) {
      throw new InvalidUserInputException(
          "Falha ao exportar CSV: "
              + (e.getMessage() == null ? "erro desconhecido" : e.getMessage()));
    }
  }
}
