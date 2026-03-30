package adopet.web.controller;

import adopet.application.adoption.AdoptionRequestOperationResult;
import adopet.application.adoption.AnalysisExecutionStatus;
import adopet.application.adoption.ApproveAdoptionRequestCommandHandler;
import adopet.application.adoption.CancelAdoptionRequestCommandHandler;
import adopet.application.adoption.CreateAdoptionRequestCommandHandler;
import adopet.application.adoption.DeleteAdoptionRequestCommandHandler;
import adopet.application.adoption.GetAdoptionRequestDetailsQuery;
import adopet.application.adoption.ListAdoptionRequestsQuery;
import adopet.application.adoption.RejectAdoptionRequestCommandHandler;
import adopet.application.adoption.RetryEligibilityAnalysisCommandHandler;
import adopet.application.adoption.UpdateAdoptionRequestCommandHandler;
import adopet.domain.adoption.AdoptionRequest;
import adopet.domain.adoption.AdoptionRequestStatus;
import adopet.domain.adoption.ApplicantDocument;
import adopet.domain.adoption.ApplicantEmail;
import adopet.domain.adoption.ApplicantName;
import adopet.domain.adoption.ApplicantPhone;
import adopet.domain.adoption.EligibilityAnalysis;
import adopet.domain.adoption.HousingType;
import adopet.domain.adoption.ReasonText;
import adopet.domain.shelterandpet.AgeYears;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetName;
import adopet.domain.shelterandpet.PetStatus;
import adopet.domain.shelterandpet.PetType;
import adopet.domain.shelterandpet.WeightKg;
import adopet.fakes.AbstractFakePetGateway;
import adopet.exception.EntityNotFoundException;
import adopet.gateway.AdoptionRequestGateway;
import adopet.infrastructure.memory.ProgrammableEligibilityAnalysisGateway;
import adopet.infrastructure.memory.ProgrammableNotificationGateway;
import adopet.infrastructure.memory.ScenarioMode;
import adopet.web.dto.adoption.AdoptionRequestForm;
import adopet.web.exception.WebExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

class AdoptionRequestControllerTest {

  private InMemoryAdoptionRequestGateway adoptionRequestGateway;
  private InMemoryPetGateway petGateway;
  private ProgrammableEligibilityAnalysisGateway programmableEligibilityAnalysisGateway;
  private ProgrammableNotificationGateway programmableNotificationGateway;
  private AdoptionRequestController controller;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    adoptionRequestGateway = new InMemoryAdoptionRequestGateway();
    petGateway = new InMemoryPetGateway();
    programmableEligibilityAnalysisGateway = new ProgrammableEligibilityAnalysisGateway();
    programmableNotificationGateway = new ProgrammableNotificationGateway();

    petGateway.seed(10L, samplePet());

    controller = new AdoptionRequestController(
        adoptionRequestGateway,
        petGateway,
        new ListAdoptionRequestsQuery(adoptionRequestGateway),
        new GetAdoptionRequestDetailsQuery(adoptionRequestGateway),
        new CreateAdoptionRequestCommandHandler(
            adoptionRequestGateway,
            programmableEligibilityAnalysisGateway,
            programmableNotificationGateway,
            petGateway
        ),
        new UpdateAdoptionRequestCommandHandler(adoptionRequestGateway),
        new ApproveAdoptionRequestCommandHandler(
            adoptionRequestGateway,
            programmableNotificationGateway,
            petGateway
        ),
        new RejectAdoptionRequestCommandHandler(adoptionRequestGateway, programmableNotificationGateway),
        new CancelAdoptionRequestCommandHandler(adoptionRequestGateway, programmableNotificationGateway),
        new RetryEligibilityAnalysisCommandHandler(
            adoptionRequestGateway,
            programmableEligibilityAnalysisGateway,
            petGateway
        ),
        programmableEligibilityAnalysisGateway,
        programmableNotificationGateway,
        new DeleteAdoptionRequestCommandHandler(adoptionRequestGateway, petGateway)
    );

    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new WebExceptionHandler())
        .build();
  }

  @Test
  void shouldListRequests() throws Exception {
    adoptionRequestGateway.registerAdoptionRequest(sampleRequest());

    mockMvc.perform(get("/pets/1/adoption-requests"))
        .andExpect(status().isOk())
        .andExpect(view().name("adoption-requests/list"))
        .andExpect(model().attributeExists("requests"))
        .andExpect(model().attributeExists("analysisMode"))
        .andExpect(model().attributeExists("notificationMode"))
        .andExpect(model().attribute("contextLocked", true))
        .andExpect(model().attribute("contextPetId", 1L))
        .andExpect(model().attribute("contextShelterId", 10L));
  }

  @Test
  void shouldShowCreateForm() throws Exception {
    mockMvc.perform(get("/pets/1/adoption-requests/new"))
        .andExpect(status().isOk())
        .andExpect(view().name("adoption-requests/create"))
        .andExpect(model().attributeExists("adoptionRequestForm"))
        .andExpect(model().attribute("contextLocked", true))
        .andExpect(model().attribute("contextPetId", 1L))
        .andExpect(model().attribute("contextShelterId", 10L));
  }

  @Test
  void shouldReturnCreateViewWhenCreateFormHasErrors() throws Exception {
    mockMvc.perform(post("/adoption-requests")
            .param("petId", "1")
            .param("shelterId", "10")
            .param("applicantName", "")
            .param("applicantEmail", "email-invalido")
            .param("applicantPhone", "")
            .param("applicantDocument", "")
            .param("housingType", "")
            .param("reason", "curto"))
        .andExpect(status().isOk())
        .andExpect(view().name("adoption-requests/create"))
        .andExpect(model().attribute("contextLocked", true))
        .andExpect(model().attribute("contextPetId", 1L))
        .andExpect(model().attribute("contextShelterId", 10L));
  }

  @Test
  void shouldThrowWhenCreateRequestPetIdIsMissing() {
    AdoptionRequestForm form = new AdoptionRequestForm();
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(form, "adoptionRequestForm");

    EntityNotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(
        EntityNotFoundException.class,
        () -> controller.createRequest(
            form,
            bindingResult,
            new RedirectAttributesModelMap(),
            new ConcurrentModel()
        )
    );

    assertEquals("Pet não informado.", exception.getMessage());
  }

  @Test
  void shouldCreateRequestWithEligibleAnalysisMessageAndNotificationSent() throws Exception {
    programmableEligibilityAnalysisGateway.setSuccessResult(EligibilityAnalysis.ELIGIBLE);

    mockMvc.perform(validCreatePost())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("ELIGIBLE")
        ))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("Notificação enviada ao solicitante.")
        ));
  }

  @Test
  void shouldCreateRequestWithManualReviewMessage() throws Exception {
    programmableEligibilityAnalysisGateway.setSuccessResult(EligibilityAnalysis.REQUIRES_MANUAL_REVIEW);

    mockMvc.perform(validCreatePost())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("REQUIRES_MANUAL_REVIEW")
        ));
  }

  @Test
  void shouldCreateRequestWithNotEligibleMessage() throws Exception {
    programmableEligibilityAnalysisGateway.setSuccessResult(EligibilityAnalysis.NOT_ELIGIBLE);

    mockMvc.perform(validCreatePost())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("NOT_ELIGIBLE")
        ));
  }

  @Test
  void shouldCreateRequestWithNotRequestedMessage() throws Exception {
    programmableEligibilityAnalysisGateway.setSuccessResult(EligibilityAnalysis.NOT_REQUESTED);

    mockMvc.perform(validCreatePost())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("Análise automática não solicitada.")
        ));
  }

  @Test
  void shouldCreateRequestWithUnavailableMessage() throws Exception {
    programmableEligibilityAnalysisGateway.setSuccessResult(EligibilityAnalysis.UNAVAILABLE);

    mockMvc.perform(validCreatePost())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("UNAVAILABLE")
        ));
  }

  @Test
  void shouldCreateRequestWhenAnalysisFailsAndNotificationFails() throws Exception {
    programmableEligibilityAnalysisGateway.setScenarioMode(ScenarioMode.TIMEOUT);
    programmableNotificationGateway.setScenarioMode(ScenarioMode.NETWORK_ERROR);

    mockMvc.perform(validCreatePost())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("A análise automática falhou")
        ))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("A notificação não pôde ser enviada no momento.")
        ));
  }

  @Test
  void shouldShowDetails() throws Exception {
    AdoptionRequest stored = adoptionRequestGateway.registerAdoptionRequest(sampleRequest());

    mockMvc.perform(get("/adoption-requests/" + stored.id()))
        .andExpect(status().isOk())
        .andExpect(view().name("adoption-requests/details"))
        .andExpect(model().attributeExists("request"));
  }

  @Test
  void shouldShowEditForm() throws Exception {
    AdoptionRequest stored = adoptionRequestGateway.registerAdoptionRequest(sampleRequest());

    mockMvc.perform(get("/adoption-requests/" + stored.id() + "/edit"))
        .andExpect(status().isOk())
        .andExpect(view().name("adoption-requests/edit"))
        .andExpect(model().attributeExists("requestId"))
        .andExpect(model().attributeExists("adoptionRequestForm"));
  }

  @Test
  void shouldReturnEditViewWhenUpdateFormHasErrors() throws Exception {
    AdoptionRequest stored = adoptionRequestGateway.registerAdoptionRequest(sampleRequest());

    mockMvc.perform(post("/adoption-requests/" + stored.id())
            .param("petId", "1")
            .param("shelterId", "10")
            .param("applicantName", "")
            .param("applicantEmail", "email-invalido")
            .param("applicantPhone", "")
            .param("applicantDocument", "")
            .param("housingType", "")
            .param("reason", "curto"))
        .andExpect(status().isOk())
        .andExpect(view().name("adoption-requests/edit"))
        .andExpect(model().attribute("requestId", stored.id()));
  }

  @Test
  void shouldUpdateRequest() throws Exception {
    AdoptionRequest stored = adoptionRequestGateway.registerAdoptionRequest(sampleRequest());

    mockMvc.perform(post("/adoption-requests/" + stored.id())
            .param("petId", "1")
            .param("shelterId", "10")
            .param("applicantName", "Maria da Silva")
            .param("applicantEmail", "maria@email.com")
            .param("applicantPhone", "31999999999")
            .param("applicantDocument", "12345678900")
            .param("housingType", "HOUSE")
            .param("hasOtherPets", "true")
            .param("reason", "Quero adotar com responsabilidade e carinho."))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute("successMessage", "Solicitação atualizada com sucesso."));
  }

  @Test
  void shouldApproveWithNotificationSent() throws Exception {
    AdoptionRequest stored = adoptionRequestGateway.registerAdoptionRequest(sampleRequest());

    mockMvc.perform(post("/adoption-requests/" + stored.id() + "/approve"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("Notificação enviada ao solicitante.")
        ));

    assertEquals(PetStatus.ADOPTED, petGateway.findById(1L).orElseThrow().status());
  }

  @Test
  void shouldApproveWithNotificationFailure() throws Exception {
    programmableNotificationGateway.setScenarioMode(ScenarioMode.NETWORK_ERROR);
    AdoptionRequest stored = adoptionRequestGateway.registerAdoptionRequest(sampleRequest());

    mockMvc.perform(post("/adoption-requests/" + stored.id() + "/approve"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("A notificação não pôde ser enviada no momento.")
        ));
  }

  @Test
  void shouldRejectWithNotificationSent() throws Exception {
    AdoptionRequest stored = adoptionRequestGateway.registerAdoptionRequest(sampleRequest());

    mockMvc.perform(post("/adoption-requests/" + stored.id() + "/reject"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("Notificação enviada ao solicitante.")
        ));
  }

  @Test
  void shouldRejectWithNotificationFailure() throws Exception {
    programmableNotificationGateway.setScenarioMode(ScenarioMode.NETWORK_ERROR);
    AdoptionRequest stored = adoptionRequestGateway.registerAdoptionRequest(sampleRequest());

    mockMvc.perform(post("/adoption-requests/" + stored.id() + "/reject"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("A notificação não pôde ser enviada no momento.")
        ));
  }

  @Test
  void shouldCancelWithNotificationSent() throws Exception {
    AdoptionRequest stored = adoptionRequestGateway.registerAdoptionRequest(sampleRequest());

    mockMvc.perform(post("/adoption-requests/" + stored.id() + "/cancel"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("Notificação enviada ao solicitante.")
        ));
  }

  @Test
  void shouldCancelWithNotificationFailure() throws Exception {
    programmableNotificationGateway.setScenarioMode(ScenarioMode.NETWORK_ERROR);
    AdoptionRequest stored = adoptionRequestGateway.registerAdoptionRequest(sampleRequest());

    mockMvc.perform(post("/adoption-requests/" + stored.id() + "/cancel"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute(
            "successMessage",
            org.hamcrest.Matchers.containsString("A notificação não pôde ser enviada no momento.")
        ));
  }

  @Test
  void shouldRetryAnalysis() throws Exception {
    AdoptionRequest stored = adoptionRequestGateway.registerAdoptionRequest(sampleRequest());

    mockMvc.perform(post("/adoption-requests/" + stored.id() + "/retry-analysis"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/adoption-requests/" + stored.id()))
        .andExpect(flash().attribute("successMessage", "Nova análise executada com sucesso. O pet voltou para disponível."));

    assertEquals(PetStatus.AVAILABLE, petGateway.findById(1L).orElseThrow().status());
  }

  @Test
  void shouldSetEligibilitySimulation() throws Exception {
    mockMvc.perform(post("/pets/1/adoption-requests/simulation/eligibility/TIMEOUT"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute("successMessage", "Simulação da análise atualizada."));

    assertEquals(ScenarioMode.TIMEOUT, programmableEligibilityAnalysisGateway.getScenarioMode());
  }

  @Test
  void shouldSetNotificationSimulation() throws Exception {
    mockMvc.perform(post("/pets/1/adoption-requests/simulation/notification/NETWORK_ERROR"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute("successMessage", "Simulação da notificação atualizada."));

    assertEquals(ScenarioMode.NETWORK_ERROR, programmableNotificationGateway.getScenarioMode());
  }

  @Test
  void shouldDeleteRequest() throws Exception {
    AdoptionRequest stored = adoptionRequestGateway.registerAdoptionRequest(sampleRequest());

    mockMvc.perform(post("/adoption-requests/" + stored.id() + "/delete"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/pets/1/adoption-requests"))
        .andExpect(flash().attribute("successMessage", "Solicitação removida com sucesso."));

    assertEquals(Optional.empty(), adoptionRequestGateway.findById(stored.id()));
  }

  private MockHttpServletRequestBuilder validCreatePost() {
    return post("/adoption-requests")
        .param("petId", "1")
        .param("shelterId", "10")
        .param("applicantName", "Maria da Silva")
        .param("applicantEmail", "maria@email.com")
        .param("applicantPhone", "31999999999")
        .param("applicantDocument", "12345678900")
        .param("housingType", "HOUSE")
        .param("hasOtherPets", "true")
        .param("reason", "Quero adotar com responsabilidade e carinho.");
  }

  private AdoptionRequest sampleRequest() {
    LocalDateTime createdAt = LocalDateTime.now().minusMinutes(1);
    return new AdoptionRequest(
        1L,
        1L,
        10L,
        new ApplicantName("Maria da Silva"),
        new ApplicantEmail("maria@email.com"),
        new ApplicantPhone("31999999999"),
        new ApplicantDocument("12345678900"),
        HousingType.HOUSE,
        true,
        new ReasonText("Quero adotar com responsabilidade e carinho."),
        AdoptionRequestStatus.PENDING,
        EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
        createdAt,
        createdAt
    );
  }

  private Pet samplePet() {
    return new Pet(
        1L,
        PetType.CACHORRO,
        new PetName("Rex"),
        "Vira-lata",
        new AgeYears(2),
        "Caramelo",
        new WeightKg(10.5),
        PetStatus.AVAILABLE
    );
  }

  private static final class InMemoryAdoptionRequestGateway implements AdoptionRequestGateway {

    private final Map<Long, AdoptionRequest> storage = new LinkedHashMap<>();
    private long nextId = 2L;

    @Override
    public List<AdoptionRequest> listAdoptionRequests(String status, Long petId, Long shelterId) {
      return storage.values().stream()
          .filter(request -> status == null || status.isBlank() || request.status().name().equalsIgnoreCase(status))
          .filter(request -> petId == null || request.petId().equals(petId))
          .filter(request -> shelterId == null || request.shelterId().equals(shelterId))
          .toList();
    }

    @Override
    public Optional<AdoptionRequest> findById(Long id) {
      return Optional.ofNullable(storage.get(id));
    }

    @Override
    public AdoptionRequest registerAdoptionRequest(AdoptionRequest adoptionRequest) {
      Long id = adoptionRequest.id() != null ? adoptionRequest.id() : nextId++;
      AdoptionRequest stored = new AdoptionRequest(
          id,
          adoptionRequest.petId(),
          adoptionRequest.shelterId(),
          adoptionRequest.applicantName(),
          adoptionRequest.applicantEmail(),
          adoptionRequest.applicantPhone(),
          adoptionRequest.applicantDocument(),
          adoptionRequest.housingType(),
          adoptionRequest.hasOtherPets(),
          adoptionRequest.reason(),
          adoptionRequest.status(),
          adoptionRequest.eligibilityAnalysis(),
          adoptionRequest.createdAt(),
          adoptionRequest.updatedAt()
      );
      storage.put(id, stored);
      return stored;
    }

    @Override
    public AdoptionRequest updateAdoptionRequest(Long id, AdoptionRequest updated) {
      storage.put(id, updated);
      return updated;
    }

    @Override
    public void deleteAdoptionRequest(Long id) {
      storage.remove(id);
    }

    @Override
    public boolean existsActiveRequestForPetAndDocument(Long petId, String applicantDocument) {
      return storage.values().stream().anyMatch(request ->
          request.petId().equals(petId)
              && request.applicantDocument().value().equals(applicantDocument)
              && request.status() != AdoptionRequestStatus.REJECTED
              && request.status() != AdoptionRequestStatus.CANCELLED);
    }

    @Override
    public void clear() {
      storage.clear();
    }
  }

  private static final class InMemoryPetGateway extends AbstractFakePetGateway {

    private final Map<Long, Pet> pets = new LinkedHashMap<>();
    private final Map<Long, Long> shelterIds = new LinkedHashMap<>();

    void seed(Long shelterId, Pet pet) {
      pets.put(pet.id(), pet);
      shelterIds.put(pet.id(), shelterId);
    }

    @Override
    public Optional<Pet> findById(Long petId) {
      return Optional.ofNullable(pets.get(petId));
    }

    @Override
    public Optional<Long> findShelterIdByPetId(Long petId) {
      return Optional.ofNullable(shelterIds.get(petId));
    }

    @Override
    public Pet updatePetStatus(Long petId, PetStatus status) {
      Pet existing = pets.get(petId);
      Pet updated = new Pet(
          existing.id(),
          existing.type(),
          existing.name(),
          existing.breed(),
          existing.age(),
          existing.color(),
          existing.weight(),
          status
      );
      pets.put(petId, updated);
      return updated;
    }
  }
}
