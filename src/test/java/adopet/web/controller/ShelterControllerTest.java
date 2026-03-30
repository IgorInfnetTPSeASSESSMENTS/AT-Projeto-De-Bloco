package adopet.web.controller;

import adopet.application.shelterandpet.DeleteShelterCommandHandler;
import adopet.application.shelterandpet.ListSheltersQuery;
import adopet.application.shelterandpet.RegisterShelterCommandHandler;
import adopet.application.shelterandpet.UpdateShelterCommandHandler;
import adopet.domain.shelterandpet.Email;
import adopet.domain.shelterandpet.PhoneNumber;
import adopet.domain.shelterandpet.Shelter;
import adopet.exception.EntityNotFoundException;
import adopet.fakes.AbstractFakeShelterGateway;
import adopet.web.exception.WebExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class ShelterControllerTest {

  private FakeShelterGateway shelterGateway;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    shelterGateway = new FakeShelterGateway();

    ShelterController controller = new ShelterController(
        shelterGateway,
        new ListSheltersQuery(shelterGateway),
        new RegisterShelterCommandHandler(shelterGateway),
        new UpdateShelterCommandHandler(shelterGateway),
        new DeleteShelterCommandHandler(shelterGateway)
    );

    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new WebExceptionHandler())
        .build();
  }

  @Test
  void shouldListShelters() throws Exception {
    shelterGateway.seed(new Shelter(1L, "Abrigo 1", new PhoneNumber("31999999999"), new Email("abrigo1@email.com")));
    shelterGateway.seed(new Shelter(2L, "Abrigo 2", new PhoneNumber("31888888888"), new Email("abrigo2@email.com")));

    mockMvc.perform(get("/shelters"))
        .andExpect(status().isOk())
        .andExpect(view().name("shelters/list"))
        .andExpect(model().attributeExists("shelters"));
  }

  @Test
  void shouldShowCreateForm() throws Exception {
    mockMvc.perform(get("/shelters/new"))
        .andExpect(status().isOk())
        .andExpect(view().name("shelters/create"))
        .andExpect(model().attributeExists("shelterForm"));
  }

  @Test
  void shouldCreateShelterSuccessfully() throws Exception {
    mockMvc.perform(post("/shelters")
            .param("name", "Abrigo Central")
            .param("phoneNumber", "31999999999")
            .param("email", "abrigo@email.com"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/shelters"))
        .andExpect(flash().attribute("successMessage", "Abrigo cadastrado com sucesso."));

    assertEquals("Abrigo Central", shelterGateway.lastRegisteredShelter.name());
  }

  @Test
  void shouldReturnCreateFormWhenCreateShelterValidationFails() throws Exception {
    mockMvc.perform(post("/shelters")
            .param("name", "")
            .param("phoneNumber", "")
            .param("email", "email-invalido"))
        .andExpect(status().isOk())
        .andExpect(view().name("shelters/create"))
        .andExpect(model().attributeHasFieldErrors("shelterForm", "name", "phoneNumber", "email"));
  }

  @Test
  void shouldShowEditForm() throws Exception {
    shelterGateway.seed(sampleShelter());

    mockMvc.perform(get("/shelters/1/edit"))
        .andExpect(status().isOk())
        .andExpect(view().name("shelters/edit"))
        .andExpect(model().attributeExists("shelterId"))
        .andExpect(model().attributeExists("shelterForm"))
        .andExpect(model().attribute("shelterId", 1L));
  }

  @Test
  void shouldReturnErrorViewWhenEditFormShelterNotFound() throws Exception {
    mockMvc.perform(get("/shelters/1/edit"))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attributeExists("errorMessage"));
  }

  @Test
  void shouldUpdateShelterSuccessfully() throws Exception {
    shelterGateway.seed(sampleShelter());

    mockMvc.perform(post("/shelters/1")
            .param("name", "Abrigo Atualizado")
            .param("phoneNumber", "31888888888")
            .param("email", "novo@email.com"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/shelters"))
        .andExpect(flash().attribute("successMessage", "Abrigo atualizado com sucesso."));

    assertEquals("Abrigo Atualizado", shelterGateway.findById(1L).orElseThrow().name());
  }

  @Test
  void shouldReturnEditFormWhenUpdateShelterValidationFails() throws Exception {
    mockMvc.perform(post("/shelters/1")
            .param("name", "")
            .param("phoneNumber", "")
            .param("email", "email-invalido"))
        .andExpect(status().isOk())
        .andExpect(view().name("shelters/edit"))
        .andExpect(model().attribute("shelterId", 1L))
        .andExpect(model().attributeHasFieldErrors("shelterForm", "name", "phoneNumber", "email"));
  }

  @Test
  void shouldDeleteShelterSuccessfully() throws Exception {
    shelterGateway.seed(sampleShelter());

    mockMvc.perform(post("/shelters/1/delete"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/shelters"))
        .andExpect(flash().attribute("successMessage", "Abrigo excluído com sucesso."));

    assertEquals(Optional.empty(), shelterGateway.findById(1L));
  }

  @Test
  void shouldRenderErrorViewWhenDeleteShelterThrowsBusinessException() throws Exception {
    shelterGateway.seed(sampleShelter());
    shelterGateway.failDeleteWith = new EntityNotFoundException("Abrigo não encontrado (id=1).");

    mockMvc.perform(post("/shelters/1/delete"))
        .andExpect(status().isOk())
        .andExpect(view().name("error"))
        .andExpect(model().attribute("errorMessage", "Abrigo não encontrado (id=1)."));
  }

  private Shelter sampleShelter() {
    return new Shelter(
        1L,
        "Abrigo Central",
        new PhoneNumber("31999999999"),
        new Email("abrigo@email.com")
    );
  }

  private static final class FakeShelterGateway extends AbstractFakeShelterGateway {

    private final Map<Long, Shelter> shelters = new HashMap<>();
    private long nextId = 100L;
    private RuntimeException failDeleteWith;
    private Shelter lastRegisteredShelter;

    void seed(Shelter shelter) {
      shelters.put(shelter.id(), shelter);
      nextId = Math.max(nextId, shelter.id() + 1);
    }

    @Override
    public java.util.List<Shelter> listShelters() {
      return java.util.List.copyOf(shelters.values());
    }

    @Override
    public Optional<Shelter> findById(Long id) {
      return Optional.ofNullable(shelters.get(id));
    }

    @Override
    public Optional<Shelter> findByName(String name) {
      return shelters.values().stream().filter(shelter -> shelter.name().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public Shelter registerShelter(Shelter shelter) {
      Shelter registered = new Shelter(nextId++, shelter.name(), shelter.phoneNumber(), shelter.email());
      shelters.put(registered.id(), registered);
      lastRegisteredShelter = registered;
      return registered;
    }

    @Override
    public Shelter updateShelter(Long id, String name, PhoneNumber phoneNumber, Email email) {
      Shelter updated = new Shelter(id, name, phoneNumber, email);
      shelters.put(id, updated);
      return updated;
    }

    @Override
    public void deleteShelter(Long id) {
      if (failDeleteWith != null) {
        throw failDeleteWith;
      }
      shelters.remove(id);
    }
  }
}
