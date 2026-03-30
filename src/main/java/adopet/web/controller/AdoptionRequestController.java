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
import adopet.exception.EntityNotFoundException;
import adopet.gateway.AdoptionRequestGateway;
import adopet.gateway.PetGateway;
import adopet.infrastructure.memory.ProgrammableEligibilityAnalysisGateway;
import adopet.infrastructure.memory.ProgrammableNotificationGateway;
import adopet.infrastructure.memory.ScenarioMode;
import adopet.web.dto.adoption.AdoptionRequestFilterForm;
import adopet.web.dto.adoption.AdoptionRequestForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdoptionRequestController {

    private final AdoptionRequestGateway adoptionRequestGateway;
    private final PetGateway petGateway;
    private final ListAdoptionRequestsQuery listAdoptionRequestsQuery;
    private final GetAdoptionRequestDetailsQuery getAdoptionRequestDetailsQuery;
    private final CreateAdoptionRequestCommandHandler createAdoptionRequestCommandHandler;
    private final UpdateAdoptionRequestCommandHandler updateAdoptionRequestCommandHandler;
    private final ApproveAdoptionRequestCommandHandler approveAdoptionRequestCommandHandler;
    private final RejectAdoptionRequestCommandHandler rejectAdoptionRequestCommandHandler;
    private final CancelAdoptionRequestCommandHandler cancelAdoptionRequestCommandHandler;
    private final RetryEligibilityAnalysisCommandHandler retryEligibilityAnalysisCommandHandler;
    private final ProgrammableEligibilityAnalysisGateway programmableEligibilityAnalysisGateway;
    private final ProgrammableNotificationGateway programmableNotificationGateway;
    private final DeleteAdoptionRequestCommandHandler deleteAdoptionRequestCommandHandler;

    public AdoptionRequestController(
            AdoptionRequestGateway adoptionRequestGateway,
            PetGateway petGateway,
            ListAdoptionRequestsQuery listAdoptionRequestsQuery,
            GetAdoptionRequestDetailsQuery getAdoptionRequestDetailsQuery,
            CreateAdoptionRequestCommandHandler createAdoptionRequestCommandHandler,
            UpdateAdoptionRequestCommandHandler updateAdoptionRequestCommandHandler,
            ApproveAdoptionRequestCommandHandler approveAdoptionRequestCommandHandler,
            RejectAdoptionRequestCommandHandler rejectAdoptionRequestCommandHandler,
            CancelAdoptionRequestCommandHandler cancelAdoptionRequestCommandHandler,
            RetryEligibilityAnalysisCommandHandler retryEligibilityAnalysisCommandHandler,
            ProgrammableEligibilityAnalysisGateway programmableEligibilityAnalysisGateway,
            ProgrammableNotificationGateway programmableNotificationGateway,
            DeleteAdoptionRequestCommandHandler deleteAdoptionRequestCommandHandler
    ) {
        this.adoptionRequestGateway = adoptionRequestGateway;
        this.petGateway = petGateway;
        this.listAdoptionRequestsQuery = listAdoptionRequestsQuery;
        this.getAdoptionRequestDetailsQuery = getAdoptionRequestDetailsQuery;
        this.createAdoptionRequestCommandHandler = createAdoptionRequestCommandHandler;
        this.updateAdoptionRequestCommandHandler = updateAdoptionRequestCommandHandler;
        this.approveAdoptionRequestCommandHandler = approveAdoptionRequestCommandHandler;
        this.rejectAdoptionRequestCommandHandler = rejectAdoptionRequestCommandHandler;
        this.cancelAdoptionRequestCommandHandler = cancelAdoptionRequestCommandHandler;
        this.retryEligibilityAnalysisCommandHandler = retryEligibilityAnalysisCommandHandler;
        this.programmableEligibilityAnalysisGateway = programmableEligibilityAnalysisGateway;
        this.programmableNotificationGateway = programmableNotificationGateway;
        this.deleteAdoptionRequestCommandHandler = deleteAdoptionRequestCommandHandler;
    }

    @GetMapping("/pets/{petId}/adoption-requests")
    public String listRequestsByPet(
            @PathVariable("petId") Long petId,
            @ModelAttribute("filter") AdoptionRequestFilterForm incomingFilter,
            Model model
    ) {
        petGateway.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado (id=" + petId + ")."));

        Long shelterId = petGateway.findShelterIdByPetId(petId)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo do pet não encontrado."));

        AdoptionRequestFilterForm filter = new AdoptionRequestFilterForm();
        filter.setStatus(incomingFilter.getStatus());
        filter.setPetId(petId);
        filter.setShelterId(shelterId);

        model.addAttribute("filter", filter);
        model.addAttribute(
                "requests",
                listAdoptionRequestsQuery.execute(
                        filter.getStatus(),
                        filter.getPetId(),
                        filter.getShelterId()
                )
        );
        model.addAttribute("analysisMode", programmableEligibilityAnalysisGateway.getScenarioMode());
        model.addAttribute("notificationMode", programmableNotificationGateway.getScenarioMode());
        model.addAttribute("contextLocked", true);
        model.addAttribute("contextPetId", petId);
        model.addAttribute("contextShelterId", shelterId);

        return "adoption-requests/list";
    }

    @GetMapping("/pets/{petId}/adoption-requests/new")
    public String showCreateFormForPet(@PathVariable("petId") Long petId, Model model) {
        petGateway.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado (id=" + petId + ")."));

        Long shelterId = petGateway.findShelterIdByPetId(petId)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo do pet não encontrado."));

        AdoptionRequestForm form = new AdoptionRequestForm();
        form.setPetId(petId);
        form.setShelterId(shelterId);

        model.addAttribute("adoptionRequestForm", form);
        model.addAttribute("contextPetId", petId);
        model.addAttribute("contextShelterId", shelterId);
        model.addAttribute("contextLocked", true);

        return "adoption-requests/create";
    }

    @PostMapping("/adoption-requests")
    public String createRequest(
            @Valid @ModelAttribute("adoptionRequestForm") AdoptionRequestForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (form.getPetId() == null) {
            throw new EntityNotFoundException("Pet não informado.");
        }

        petGateway.findById(form.getPetId())
                .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado (id=" + form.getPetId() + ")."));

        Long resolvedShelterId = petGateway.findShelterIdByPetId(form.getPetId())
                .orElseThrow(() -> new EntityNotFoundException("Abrigo do pet não encontrado."));

        if (bindingResult.hasErrors()) {
            model.addAttribute("contextPetId", form.getPetId());
            model.addAttribute("contextShelterId", resolvedShelterId);
            model.addAttribute("contextLocked", true);
            return "adoption-requests/create";
        }

        AdoptionRequestOperationResult result = createAdoptionRequestCommandHandler.execute(
                form.getPetId(),
                resolvedShelterId,
                form.getApplicantName(),
                form.getApplicantEmail(),
                form.getApplicantPhone(),
                form.getApplicantDocument(),
                form.getHousingType(),
                form.isHasOtherPets(),
                form.getReason()
        );

        String analysisMessage;
        if (result.analysisExecutionStatus() == AnalysisExecutionStatus.SUCCESS) {
            analysisMessage = switch (result.analysisResult()) {
                case ELIGIBLE ->
                        "Análise automática executada com sucesso: ELIGIBLE. A solicitação foi encaminhada para UNDER_REVIEW.";
                case REQUIRES_MANUAL_REVIEW ->
                        "Análise automática executada com sucesso: REQUIRES_MANUAL_REVIEW. A solicitação permaneceu em PENDING.";
                case NOT_ELIGIBLE ->
                        "Análise automática executada com sucesso: NOT_ELIGIBLE.";
                case NOT_REQUESTED ->
                        "Análise automática não solicitada.";
                case UNAVAILABLE ->
                        "Análise automática retornou UNAVAILABLE.";
            };
        } else {
            analysisMessage = "A análise automática falhou e a solicitação foi registrada com resultado UNAVAILABLE.";
        }

        String notificationMessage = result.notificationSent()
                ? "Notificação enviada ao solicitante."
                : "A notificação não pôde ser enviada no momento.";

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Solicitação de adoção cadastrada com sucesso. " + analysisMessage + " " + notificationMessage
        );

        return "redirect:/pets/" + form.getPetId() + "/adoption-requests";
    }

    @GetMapping("/adoption-requests/{id}")
    public String details(@PathVariable("id") Long id, Model model) {
        model.addAttribute("request", getAdoptionRequestDetailsQuery.execute(id));
        return "adoption-requests/details";
    }

    @GetMapping("/adoption-requests/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        AdoptionRequest request = getAdoptionRequestDetailsQuery.execute(id);

        model.addAttribute("requestId", id);
        model.addAttribute("adoptionRequestForm", new AdoptionRequestForm(
                request.petId(),
                request.shelterId(),
                request.applicantName().value(),
                request.applicantEmail().value(),
                request.applicantPhone().value(),
                request.applicantDocument().value(),
                request.housingType().name(),
                request.hasOtherPets(),
                request.reason().value()
        ));

        return "adoption-requests/edit";
    }

    @PostMapping("/adoption-requests/{id}")
    public String updateRequest(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("adoptionRequestForm") AdoptionRequestForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        AdoptionRequest existing = getAdoptionRequestDetailsQuery.execute(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("requestId", id);
            return "adoption-requests/edit";
        }

        updateAdoptionRequestCommandHandler.execute(
                id,
                existing.petId(),
                existing.shelterId(),
                form.getApplicantName(),
                form.getApplicantEmail(),
                form.getApplicantPhone(),
                form.getApplicantDocument(),
                form.getHousingType(),
                form.isHasOtherPets(),
                form.getReason()
        );

        redirectAttributes.addFlashAttribute("successMessage", "Solicitação atualizada com sucesso.");
        return "redirect:/pets/" + existing.petId() + "/adoption-requests";
    }

    @PostMapping("/adoption-requests/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        AdoptionRequest existing = getAdoptionRequestDetailsQuery.execute(id);

        deleteAdoptionRequestCommandHandler.execute(id);
        redirectAttributes.addFlashAttribute("successMessage", "Solicitação removida com sucesso.");
        return "redirect:/pets/" + existing.petId() + "/adoption-requests";
    }

    @PostMapping("/adoption-requests/{id}/approve")
    public String approve(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        AdoptionRequestOperationResult result = approveAdoptionRequestCommandHandler.execute(id);

        String notificationMessage = result.notificationSent()
                ? "Notificação enviada ao solicitante."
                : "A notificação não pôde ser enviada no momento.";

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Solicitação aprovada com sucesso. " + notificationMessage
        );

        return "redirect:/pets/" + result.adoptionRequest().petId() + "/adoption-requests";
    }

    @PostMapping("/adoption-requests/{id}/reject")
    public String reject(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        AdoptionRequestOperationResult result = rejectAdoptionRequestCommandHandler.execute(id);

        String notificationMessage = result.notificationSent()
                ? "Notificação enviada ao solicitante."
                : "A notificação não pôde ser enviada no momento.";

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Solicitação rejeitada com sucesso. " + notificationMessage
        );

        return "redirect:/pets/" + result.adoptionRequest().petId() + "/adoption-requests";
    }

    @PostMapping("/adoption-requests/{id}/cancel")
    public String cancel(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        AdoptionRequestOperationResult result = cancelAdoptionRequestCommandHandler.execute(id);

        String notificationMessage = result.notificationSent()
                ? "Notificação enviada ao solicitante."
                : "A notificação não pôde ser enviada no momento.";

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Solicitação cancelada com sucesso. " + notificationMessage
        );

        return "redirect:/pets/" + result.adoptionRequest().petId() + "/adoption-requests";
    }

    @PostMapping("/adoption-requests/{id}/retry-analysis")
    public String retryAnalysis(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        retryEligibilityAnalysisCommandHandler.execute(id);
        redirectAttributes.addFlashAttribute("successMessage", "Nova análise executada com sucesso. O pet voltou para disponível.");
        return "redirect:/adoption-requests/" + id;
    }

    @PostMapping("/pets/{petId}/adoption-requests/simulation/eligibility/{mode}")
    public String setEligibilitySimulation(
            @PathVariable("petId") Long petId,
            @PathVariable("mode") String mode,
            RedirectAttributes redirectAttributes
    ) {
        petGateway.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado (id=" + petId + ")."));

        programmableEligibilityAnalysisGateway.setScenarioMode(ScenarioMode.valueOf(mode.toUpperCase()));
        redirectAttributes.addFlashAttribute("successMessage", "Simulação da análise atualizada.");
        return "redirect:/pets/" + petId + "/adoption-requests";
    }

    @PostMapping("/pets/{petId}/adoption-requests/simulation/notification/{mode}")
    public String setNotificationSimulation(
            @PathVariable("petId") Long petId,
            @PathVariable("mode") String mode,
            RedirectAttributes redirectAttributes
    ) {
        petGateway.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado (id=" + petId + ")."));

        programmableNotificationGateway.setScenarioMode(ScenarioMode.valueOf(mode.toUpperCase()));
        redirectAttributes.addFlashAttribute("successMessage", "Simulação da notificação atualizada.");
        return "redirect:/pets/" + petId + "/adoption-requests";
    }
}