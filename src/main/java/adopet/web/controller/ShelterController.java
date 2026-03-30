package adopet.web.controller;

import adopet.application.shelterandpet.DeleteShelterCommandHandler;
import adopet.application.shelterandpet.ListSheltersQuery;
import adopet.application.shelterandpet.RegisterShelterCommandHandler;
import adopet.application.shelterandpet.UpdateShelterCommandHandler;
import adopet.domain.shelterandpet.Shelter;
import adopet.exception.EntityNotFoundException;
import adopet.gateway.ShelterGateway;
import adopet.web.dto.shelterandpet.ShelterForm;
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
public class ShelterController {

    private final ShelterGateway shelterGateway;
    private final ListSheltersQuery listSheltersQuery;
    private final RegisterShelterCommandHandler registerShelterCommandHandler;
    private final UpdateShelterCommandHandler updateShelterCommandHandler;
    private final DeleteShelterCommandHandler deleteShelterCommandHandler;

    public ShelterController(
            ShelterGateway shelterGateway,
            ListSheltersQuery listSheltersQuery,
            RegisterShelterCommandHandler registerShelterCommandHandler,
            UpdateShelterCommandHandler updateShelterCommandHandler,
            DeleteShelterCommandHandler deleteShelterCommandHandler
    ) {
        this.shelterGateway = shelterGateway;
        this.listSheltersQuery = listSheltersQuery;
        this.registerShelterCommandHandler = registerShelterCommandHandler;
        this.updateShelterCommandHandler = updateShelterCommandHandler;
        this.deleteShelterCommandHandler = deleteShelterCommandHandler;
    }

    @GetMapping("/shelters")
    public String listShelters(Model model) {
        model.addAttribute("shelters", listSheltersQuery.execute());
        return "shelters/list";
    }

    @GetMapping("/shelters/new")
    public String showCreateForm(Model model) {
        model.addAttribute("shelterForm", new ShelterForm());
        return "shelters/create";
    }

    @PostMapping("/shelters")
    public String createShelter(
            @Valid @ModelAttribute("shelterForm") ShelterForm shelterForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "shelters/create";
        }

        registerShelterCommandHandler.execute(
                shelterForm.getName(),
                shelterForm.getPhoneNumber(),
                shelterForm.getEmail()
        );

        redirectAttributes.addFlashAttribute("successMessage", "Abrigo cadastrado com sucesso.");
        return "redirect:/shelters";
    }

    @GetMapping("/shelters/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Shelter shelter = shelterGateway.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado (id=" + id + ")."));

        ShelterForm shelterForm = new ShelterForm(
                shelter.name(),
                shelter.phoneNumber().value(),
                shelter.email().value()
        );

        model.addAttribute("shelterId", id);
        model.addAttribute("shelterForm", shelterForm);

        return "shelters/edit";
    }

    @PostMapping("/shelters/{id}")
    public String updateShelter(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("shelterForm") ShelterForm shelterForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("shelterId", id);
            return "shelters/edit";
        }

        updateShelterCommandHandler.execute(
                id,
                shelterForm.getName(),
                shelterForm.getPhoneNumber(),
                shelterForm.getEmail()
        );

        redirectAttributes.addFlashAttribute("successMessage", "Abrigo atualizado com sucesso.");
        return "redirect:/shelters";
    }

    @PostMapping("/shelters/{id}/delete")
    public String deleteShelter(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        deleteShelterCommandHandler.execute(id);
        redirectAttributes.addFlashAttribute("successMessage", "Abrigo excluído com sucesso.");
        return "redirect:/shelters";
    }
}