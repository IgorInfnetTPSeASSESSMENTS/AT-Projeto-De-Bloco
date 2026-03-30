package adopet.web.controller;

import adopet.application.shelterandpet.DeletePetCommandHandler;
import adopet.application.shelterandpet.ExportShelterPetsCommandHandler;
import adopet.application.shelterandpet.ImportShelterPetsCommandHandler;
import adopet.application.shelterandpet.ListShelterPetsQuery;
import adopet.application.shelterandpet.RegisterPetCommandHandler;
import adopet.application.shelterandpet.UpdatePetCommandHandler;
import adopet.domain.shelterandpet.AgeYears;
import adopet.domain.shelterandpet.Email;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetName;
import adopet.domain.shelterandpet.PetStatus;
import adopet.domain.shelterandpet.PetType;
import adopet.domain.shelterandpet.PhoneNumber;
import adopet.domain.shelterandpet.Shelter;
import adopet.domain.shelterandpet.WeightKg;
import adopet.exception.EntityNotFoundException;
import adopet.fakes.AbstractFakePetGateway;
import adopet.fakes.AbstractFakeShelterGateway;
import adopet.web.dto.shelterandpet.PetImportForm;
import adopet.web.exception.WebExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class PetControllerTest {

  private FakeShelterGateway shelterGateway;
  private FakePetGateway petGateway;
  private PetController controller;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    shelterGateway = new FakeShelterGateway();
    petGateway = new FakePetGateway();

    controller = new PetController(
        shelterGateway,
        petGateway,
        new ListShelterPetsQuery(petGateway),
        new RegisterPetCommandHandler(petGateway),
        new UpdatePetCommandHandler(petGateway),
        new DeletePetCommandHandler(petGateway),
        new ImportShelterPetsCommandHandler(petGateway),
        new ExportShelterPetsCommandHandler(petGateway)
    );

    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new WebExceptionHandler())
        .build();
  }

  @Test
  void shouldListPets() throws Exception {
    shelterGateway.seed(sampleShelter());
    petGateway.seed("1", samplePet(1L, "Rex"));
    petGateway.seed("1", sampleCat(2L, "Mimi"));

    mockMvc.perform(get("/shelters/1/pets"))
        .andExpect(status().isOk())
        .andExpect(view().name("pets/list"))
        .andExpect(model().attributeExists("shelter"))
        .andExpect(model().attributeExists("pets"))
        .andExpect(model().attributeExists("petImportForm"));
  }

  @Test
  void shouldShowCreatePetForm() throws Exception {
    shelterGateway.seed(sampleShelter());

    mockMvc.perform(get("/shelters/1/pets/new"))
        .andExpect(status().isOk())
        .andExpect(view().name("pets/create"))
        .andExpect(model().attributeExists("shelter"))
        .andExpect(model().attributeExists("petForm"));
  }

  @Test
  void shouldRenderErrorViewWhenShowCreateFormShelterNotFound() throws Exception {
    mockMvc.perform(get("/shelters/1/pets/new"))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attribute("errorMessage", "Abrigo não encontrado (id=1)."));
  }

  @Test
  void shouldCreatePetSuccessfully() throws Exception {
    shelterGateway.seed(sampleShelter());

    mockMvc.perform(post("/shelters/1/pets")
            .param("type", "CACHORRO")
            .param("name", "Rex")
            .param("breed", "Vira-lata")
            .param("age", "2")
            .param("color", "Caramelo")
            .param("weight", "10.5"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/shelters/1/pets"))
        .andExpect(flash().attribute("successMessage", "Pet cadastrado com sucesso."));

    assertEquals("1", petGateway.lastShelterReference);
    assertEquals("Rex", petGateway.lastRegisteredPet.name().value());
  }

  @Test
  void shouldRenderErrorViewWhenCreatePetShelterNotFound() throws Exception {
    mockMvc.perform(post("/shelters/1/pets")
            .param("type", "CACHORRO")
            .param("name", "Rex")
            .param("breed", "Vira-lata")
            .param("age", "2")
            .param("color", "Caramelo")
            .param("weight", "10.5"))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attribute("errorMessage", "Abrigo não encontrado (id=1)."));
  }

  @Test
  void shouldReturnCreatePetFormWhenValidationFails() throws Exception {
    shelterGateway.seed(sampleShelter());

    mockMvc.perform(post("/shelters/1/pets")
            .param("type", "")
            .param("name", "")
            .param("breed", "")
            .param("age", "-1")
            .param("color", "")
            .param("weight", "0"))
        .andExpect(status().isOk())
        .andExpect(view().name("pets/create"))
        .andExpect(model().attributeExists("shelter"))
        .andExpect(model().attributeHasFieldErrors(
            "petForm",
            "type",
            "name",
            "breed",
            "age",
            "color",
            "weight"
        ));
  }

  @Test
  void shouldShowEditPetForm() throws Exception {
    petGateway.seed("10", samplePet(1L, "Rex"));

    mockMvc.perform(get("/pets/1/edit"))
        .andExpect(status().isOk())
        .andExpect(view().name("pets/edit"))
        .andExpect(model().attributeExists("petId"))
        .andExpect(model().attributeExists("shelterId"))
        .andExpect(model().attributeExists("petForm"))
        .andExpect(model().attribute("petId", 1L))
        .andExpect(model().attribute("shelterId", 10L));
  }

  @Test
  void shouldRenderErrorViewWhenShowEditPetPetNotFound() throws Exception {
    mockMvc.perform(get("/pets/1/edit"))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attribute("errorMessage", "Pet não encontrado (id=1)."));
  }

  @Test
  void shouldRenderErrorViewWhenShowEditPetShelterNotFound() throws Exception {
    petGateway.pets.put(1L, samplePet(1L, "Rex"));

    mockMvc.perform(get("/pets/1/edit"))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attribute("errorMessage", "Abrigo do pet não encontrado."));
  }

  @Test
  void shouldUpdatePetSuccessfully() throws Exception {
    petGateway.seed("10", samplePet(1L, "Rex"));

    mockMvc.perform(post("/pets/1")
            .param("type", "GATO")
            .param("name", "Mimi")
            .param("breed", "Siamês")
            .param("age", "3")
            .param("color", "Branco")
            .param("weight", "4.2"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/shelters/10/pets"))
        .andExpect(flash().attribute("successMessage", "Pet atualizado com sucesso."));

    assertEquals("Mimi", petGateway.findById(1L).orElseThrow().name().value());
  }

  @Test
  void shouldRenderErrorViewWhenUpdatePetNotFound() throws Exception {
    mockMvc.perform(post("/pets/1")
            .param("type", "GATO")
            .param("name", "Mimi")
            .param("breed", "Siamês")
            .param("age", "3")
            .param("color", "Branco")
            .param("weight", "4.2"))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attribute("errorMessage", "Pet não encontrado (id=1)."));
  }

  @Test
  void shouldRenderErrorViewWhenUpdatePetShelterNotFound() throws Exception {
    petGateway.pets.put(1L, samplePet(1L, "Rex"));

    mockMvc.perform(post("/pets/1")
            .param("type", "GATO")
            .param("name", "Mimi")
            .param("breed", "Siamês")
            .param("age", "3")
            .param("color", "Branco")
            .param("weight", "4.2"))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attribute("errorMessage", "Abrigo do pet não encontrado."));
  }

  @Test
  void shouldReturnEditPetFormWhenUpdateValidationFails() throws Exception {
    petGateway.seed("10", samplePet(1L, "Rex"));

    mockMvc.perform(post("/pets/1")
            .param("type", "")
            .param("name", "")
            .param("breed", "")
            .param("age", "-1")
            .param("color", "")
            .param("weight", "0"))
        .andExpect(status().isOk())
        .andExpect(view().name("pets/edit"))
        .andExpect(model().attribute("petId", 1L))
        .andExpect(model().attribute("shelterId", 10L))
        .andExpect(model().attributeHasFieldErrors(
            "petForm",
            "type",
            "name",
            "breed",
            "age",
            "color",
            "weight"
        ));
  }

  @Test
  void shouldRenderErrorViewWhenDeletePetPetNotFound() throws Exception {
    mockMvc.perform(post("/pets/1/delete"))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attribute("errorMessage", "Pet não encontrado (id=1)."));
  }

  @Test
  void shouldRenderErrorViewWhenDeletePetShelterNotFound() throws Exception {
    petGateway.pets.put(1L, samplePet(1L, "Rex"));

    mockMvc.perform(post("/pets/1/delete"))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attribute("errorMessage", "Abrigo do pet não encontrado."));
  }

  @Test
  void shouldDeletePetSuccessfully() throws Exception {
    petGateway.seed("10", samplePet(1L, "Rex"));

    mockMvc.perform(post("/pets/1/delete"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/shelters/10/pets"))
        .andExpect(flash().attribute("successMessage", "Pet excluído com sucesso."));

    assertEquals(Optional.empty(), petGateway.findById(1L));
  }

  @Test
  void shouldRenderErrorViewWhenImportPetsShelterNotFound() throws Exception {
    MockMultipartFile csvFile = new MockMultipartFile(
        "csvFile",
        "pets.csv",
        "text/csv",
        "GATO,Mimi,SRD,2,Branco,3.2\n".getBytes(StandardCharsets.UTF_8)
    );

    mockMvc.perform(multipart("/shelters/1/pets/import").file(csvFile))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attribute("errorMessage", "Abrigo não encontrado (id=1)."));
  }

  @Test
  void shouldReturnPetListWhenImportFileIsEmpty() {
    shelterGateway.seed(sampleShelter());

    PetImportForm form = new PetImportForm(new EmptyMultipartFile("pets.csv"));
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "petImportForm");
    ConcurrentModel model = new ConcurrentModel();

    String view = controller.importPets(1L, form, bindingResult, model, new RedirectAttributesModelMap());

    assertEquals("pets/list", view);
    assertEquals(true, bindingResult.hasFieldErrors("csvFile"));
  }

  @Test
  void shouldReturnPetListWhenImportOriginalFilenameIsNull() {
    shelterGateway.seed(sampleShelter());

    PetImportForm form = new PetImportForm(new FilenameMultipartFile(null));
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "petImportForm");
    ConcurrentModel model = new ConcurrentModel();

    String view = controller.importPets(1L, form, bindingResult, model, new RedirectAttributesModelMap());

    assertEquals("pets/list", view);
    assertEquals(true, bindingResult.hasFieldErrors("csvFile"));
  }

  @Test
  void shouldReturnPetListWhenImportOriginalFilenameIsBlank() {
    shelterGateway.seed(sampleShelter());

    PetImportForm form = new PetImportForm(new FilenameMultipartFile("   "));
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "petImportForm");
    ConcurrentModel model = new ConcurrentModel();

    String view = controller.importPets(1L, form, bindingResult, model, new RedirectAttributesModelMap());

    assertEquals("pets/list", view);
    assertEquals(true, bindingResult.hasFieldErrors("csvFile"));
  }

  @Test
  void shouldImportPetsSuccessfully() throws Exception {
    shelterGateway.seed(sampleShelter());
    petGateway.importResult = 2;

    MockMultipartFile csvFile = new MockMultipartFile(
        "csvFile",
        "pets.csv",
        "text/csv",
        "GATO,Mimi,SRD,2,Branco,3.2\n".getBytes(StandardCharsets.UTF_8)
    );

    mockMvc.perform(multipart("/shelters/1/pets/import").file(csvFile))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/shelters/1/pets"))
        .andExpect(flash().attribute("successMessage", "2 pet(s) importado(s) com sucesso."));

    assertEquals("1", petGateway.lastImportedShelterReference);
  }

  @Test
  void shouldReturnPetListWhenImportValidationFails() throws Exception {
    shelterGateway.seed(sampleShelter());

    mockMvc.perform(multipart("/shelters/1/pets/import"))
        .andExpect(status().isOk())
        .andExpect(view().name("pets/list"))
        .andExpect(model().attributeExists("shelter"))
        .andExpect(model().attributeExists("pets"))
        .andExpect(model().attributeHasFieldErrors("petImportForm", "csvFile"));
  }

  @Test
  void shouldReturnPetListWhenImportFileExtensionIsInvalid() throws Exception {
    shelterGateway.seed(sampleShelter());

    MockMultipartFile csvFile = new MockMultipartFile(
        "csvFile",
        "pets.txt",
        "text/plain",
        "conteudo".getBytes(StandardCharsets.UTF_8)
    );

    mockMvc.perform(multipart("/shelters/1/pets/import").file(csvFile))
        .andExpect(status().isOk())
        .andExpect(view().name("pets/list"))
        .andExpect(model().attributeHasFieldErrors("petImportForm", "csvFile"));
  }

  @Test
  void shouldThrowIllegalStateWhenImportFileReadFails() {
    shelterGateway.seed(sampleShelter());

    PetImportForm form = new PetImportForm(new BrokenMultipartFile());
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "petImportForm");

    IllegalStateException exception = org.junit.jupiter.api.Assertions.assertThrows(
        IllegalStateException.class,
        () -> controller.importPets(
            1L,
            form,
            bindingResult,
            new ConcurrentModel(),
            new RedirectAttributesModelMap()
        )
    );

    assertEquals("Falha ao ler o arquivo CSV enviado.", exception.getMessage());
  }

  @Test
  void shouldReturnPetListWhenImportFileIsTooLarge() throws Exception {
    shelterGateway.seed(sampleShelter());

    MockMultipartFile csvFile = new MockMultipartFile(
        "csvFile",
        "pets.csv",
        "text/csv",
        new byte[1024 * 1024 + 1]
    );

    mockMvc.perform(multipart("/shelters/1/pets/import").file(csvFile))
        .andExpect(status().isOk())
        .andExpect(view().name("pets/list"))
        .andExpect(model().attributeHasFieldErrors("petImportForm", "csvFile"));
  }

  @Test
  void shouldRenderErrorViewWhenExportPetsShelterNotFound() throws Exception {
    mockMvc.perform(get("/shelters/1/pets/export"))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attribute("errorMessage", "Abrigo não encontrado (id=1)."));
  }

  @Test
  void shouldExportPetsUsingAbrigoFallbackWhenSlugIsBlank() throws Exception {
    shelterGateway.seed(new Shelter(
        2L,
        "!!!",
        new PhoneNumber("31999999999"),
        new Email("abrigo@email.com")
    ));
    petGateway.exportBytes = "tipo,nome\n".getBytes(StandardCharsets.UTF_8);

    mockMvc.perform(get("/shelters/2/pets/export"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Disposition", "attachment; filename=\"pets-abrigo.csv\""));
  }

  @Test
  void shouldExportPetsSuccessfully() throws Exception {
    shelterGateway.seed(sampleShelter());
    petGateway.exportBytes = "tipo,nome\nGATO,Mimi\n".getBytes(StandardCharsets.UTF_8);

    mockMvc.perform(get("/shelters/1/pets/export"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type", "text/csv"))
        .andExpect(header().string("Content-Disposition", "attachment; filename=\"pets-abrigo-central.csv\""))
        .andExpect(content().bytes("tipo,nome\nGATO,Mimi\n".getBytes(StandardCharsets.UTF_8)));

    assertEquals("1", petGateway.lastExportedShelterReference);
  }

  @Test
  void shouldRenderErrorViewWhenListPetsShelterNotFound() throws Exception {
    mockMvc.perform(get("/shelters/1/pets"))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attributeExists("errorMessage"));
  }

  @Test
  void shouldRenderErrorViewWhenDeletePetThrowsBusinessException() throws Exception {
    petGateway.seed("10", samplePet(1L, "Rex"));
    petGateway.failDeleteWith = new EntityNotFoundException("Pet não encontrado (id=1).");

    mockMvc.perform(post("/pets/1/delete"))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attributeExists("errorMessage"));
  }

  private Shelter sampleShelter() {
    return new Shelter(
        1L,
        "Abrigo Central",
        new PhoneNumber("31999999999"),
        new Email("abrigo@email.com")
    );
  }

  private Pet samplePet(Long id, String name) {
    return new Pet(
        id,
        PetType.CACHORRO,
        new PetName(name),
        "Vira-lata",
        new AgeYears(2),
        "Caramelo",
        new WeightKg(10.5),
        PetStatus.AVAILABLE
    );
  }

  private Pet sampleCat(Long id, String name) {
    return new Pet(
        id,
        PetType.GATO,
        new PetName(name),
        "Siamês",
        new AgeYears(3),
        "Branco",
        new WeightKg(4.2),
        PetStatus.AVAILABLE
    );
  }

  private static final class FakeShelterGateway extends AbstractFakeShelterGateway {

    private final Map<Long, Shelter> shelters = new HashMap<>();

    void seed(Shelter shelter) {
      shelters.put(shelter.id(), shelter);
    }

    @Override
    public Optional<Shelter> findById(Long id) {
      return Optional.ofNullable(shelters.get(id));
    }
  }

  private static final class FakePetGateway extends AbstractFakePetGateway {

    private final Map<Long, Pet> pets = new HashMap<>();
    private final Map<Long, Long> shelterIdsByPetId = new HashMap<>();
    private final Map<Long, List<Pet>> petsByShelterId = new HashMap<>();
    private long nextId = 100L;
    private RuntimeException failDeleteWith;
    private int importResult;
    private byte[] exportBytes = new byte[0];
    private String lastShelterReference;
    private String lastImportedShelterReference;
    private String lastExportedShelterReference;
    private Pet lastRegisteredPet;

    void seed(String shelterId, Pet pet) {
      Long parsedShelterId = Long.valueOf(shelterId);
      pets.put(pet.id(), pet);
      shelterIdsByPetId.put(pet.id(), parsedShelterId);
      petsByShelterId.computeIfAbsent(parsedShelterId, ignored -> new ArrayList<>()).add(pet);
      nextId = Math.max(nextId, pet.id() + 1);
    }

    @Override
    public List<Pet> listPets(String shelterIdOrName) {
      return List.copyOf(petsByShelterId.getOrDefault(Long.valueOf(shelterIdOrName), List.of()));
    }

    @Override
    public Optional<Pet> findById(Long petId) {
      return Optional.ofNullable(pets.get(petId));
    }

    @Override
    public Pet registerPet(String shelterIdOrName, Pet pet) {
      long petId = nextId++;
      Pet registered = new Pet(
          petId,
          pet.type(),
          pet.name(),
          pet.breed(),
          pet.age(),
          pet.color(),
          pet.weight(),
          pet.status()
      );
      lastShelterReference = shelterIdOrName;
      lastRegisteredPet = registered;
      seed(shelterIdOrName, registered);
      return registered;
    }

    @Override
    public Pet updatePet(Long petId, Pet updated) {
      Long shelterId = shelterIdsByPetId.get(petId);
      pets.put(petId, updated);
      List<Pet> petsInShelter = petsByShelterId.getOrDefault(shelterId, new ArrayList<>());
      for (int index = 0; index < petsInShelter.size(); index++) {
        if (petsInShelter.get(index).id().equals(petId)) {
          petsInShelter.set(index, updated);
          break;
        }
      }
      return updated;
    }

    @Override
    public void deletePet(Long petId) {
      if (failDeleteWith != null) {
        throw failDeleteWith;
      }
      Long shelterId = shelterIdsByPetId.remove(petId);
      pets.remove(petId);
      if (shelterId != null) {
        petsByShelterId.computeIfPresent(shelterId, (ignored, existing) -> {
          existing.removeIf(pet -> pet.id().equals(petId));
          return existing;
        });
      }
    }

    @Override
    public int importPets(String shelterIdOrName, InputStream csvInputStream) {
      lastImportedShelterReference = shelterIdOrName;
      return importResult;
    }

    @Override
    public byte[] exportPets(String shelterIdOrName) {
      lastExportedShelterReference = shelterIdOrName;
      return exportBytes;
    }

    @Override
    public Optional<Long> findShelterIdByPetId(Long petId) {
      return Optional.ofNullable(shelterIdsByPetId.get(petId));
    }
  }

  private static final class BrokenMultipartFile implements MultipartFile {

    @Override
    public String getName() {
      return "csvFile";
    }

    @Override
    public String getOriginalFilename() {
      return "pets.csv";
    }

    @Override
    public String getContentType() {
      return "text/csv";
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public long getSize() {
      return 10;
    }

    @Override
    public byte[] getBytes() throws IOException {
      throw new IOException("boom");
    }

    @Override
    public InputStream getInputStream() throws IOException {
      throw new IOException("boom");
    }

    @Override
    public ByteArrayResource getResource() {
      return new ByteArrayResource(new byte[0]);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
      throw new IOException("boom");
    }
  }

  private static class EmptyMultipartFile implements MultipartFile {

    private final String originalFilename;

    private EmptyMultipartFile(String originalFilename) {
      this.originalFilename = originalFilename;
    }

    @Override
    public String getName() {
      return "csvFile";
    }

    @Override
    public String getOriginalFilename() {
      return originalFilename;
    }

    @Override
    public String getContentType() {
      return "text/csv";
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public long getSize() {
      return 0;
    }

    @Override
    public byte[] getBytes() {
      return new byte[0];
    }

    @Override
    public InputStream getInputStream() {
      return InputStream.nullInputStream();
    }

    @Override
    public ByteArrayResource getResource() {
      return new ByteArrayResource(new byte[0]);
    }

    @Override
    public void transferTo(java.io.File dest) {
    }
  }

  private static final class FilenameMultipartFile extends EmptyMultipartFile {

    private FilenameMultipartFile(String originalFilename) {
      super(originalFilename);
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public long getSize() {
      return 1;
    }

    @Override
    public byte[] getBytes() {
      return "x".getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public InputStream getInputStream() {
      return InputStream.nullInputStream();
    }
  }
}
