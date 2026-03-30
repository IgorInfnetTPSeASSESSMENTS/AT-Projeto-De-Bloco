package adopet.web.controller;

import adopet.application.shelterandpet.DeletePetCommandHandler;
import adopet.application.shelterandpet.ExportShelterPetsCommandHandler;
import adopet.application.shelterandpet.ImportShelterPetsCommandHandler;
import adopet.application.shelterandpet.ListShelterPetsQuery;
import adopet.application.shelterandpet.RegisterPetCommandHandler;
import adopet.application.shelterandpet.UpdatePetCommandHandler;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.Shelter;
import adopet.exception.EntityNotFoundException;
import adopet.gateway.PetGateway;
import adopet.gateway.ShelterGateway;
import adopet.web.dto.shelterandpet.PetForm;
import adopet.web.dto.shelterandpet.PetImportForm;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Locale;

@Controller
public class PetController {

    private final ShelterGateway shelterGateway;
    private final PetGateway petGateway;
    private final ListShelterPetsQuery listShelterPetsQuery;
    private final RegisterPetCommandHandler registerPetCommandHandler;
    private final UpdatePetCommandHandler updatePetCommandHandler;
    private final DeletePetCommandHandler deletePetCommandHandler;
    private final ImportShelterPetsCommandHandler importShelterPetsCommandHandler;
    private final ExportShelterPetsCommandHandler exportShelterPetsCommandHandler;
    private static final long MAX_CSV_UPLOAD_BYTES = 1024 * 1024;

    public PetController(
            ShelterGateway shelterGateway,
            PetGateway petGateway,
            ListShelterPetsQuery listShelterPetsQuery,
            RegisterPetCommandHandler registerPetCommandHandler,
            UpdatePetCommandHandler updatePetCommandHandler,
            DeletePetCommandHandler deletePetCommandHandler,
            ImportShelterPetsCommandHandler importShelterPetsCommandHandler,
            ExportShelterPetsCommandHandler exportShelterPetsCommandHandler
    ) {
        this.shelterGateway = shelterGateway;
        this.petGateway = petGateway;
        this.listShelterPetsQuery = listShelterPetsQuery;
        this.registerPetCommandHandler = registerPetCommandHandler;
        this.updatePetCommandHandler = updatePetCommandHandler;
        this.deletePetCommandHandler = deletePetCommandHandler;
        this.importShelterPetsCommandHandler = importShelterPetsCommandHandler;
        this.exportShelterPetsCommandHandler = exportShelterPetsCommandHandler;
    }

    @GetMapping("/shelters/{shelterId}/pets")
    public String listPets(@PathVariable("shelterId") Long shelterId, Model model) {
        Shelter shelter = shelterGateway.findById(shelterId)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado (id=" + shelterId + ")."));

        model.addAttribute("shelter", shelter);
        model.addAttribute("pets", listShelterPetsQuery.execute(String.valueOf(shelterId)));
        model.addAttribute("petImportForm", new PetImportForm());

        return "pets/list";
    }

    @GetMapping("/shelters/{shelterId}/pets/new")
    public String showCreateForm(@PathVariable("shelterId") Long shelterId, Model model) {
        Shelter shelter = shelterGateway.findById(shelterId)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado (id=" + shelterId + ")."));

        model.addAttribute("shelter", shelter);
        model.addAttribute("petForm", new PetForm());

        return "pets/create";
    }

    @PostMapping("/shelters/{shelterId}/pets")
    public String createPet(
            @PathVariable("shelterId") Long shelterId,
            @Valid @ModelAttribute("petForm") PetForm petForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Shelter shelter = shelterGateway.findById(shelterId)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado (id=" + shelterId + ")."));

        if (bindingResult.hasErrors()) {
            model.addAttribute("shelter", shelter);
            return "pets/create";
        }

        registerPetCommandHandler.execute(
                String.valueOf(shelterId),
                petForm.getType(),
                petForm.getName(),
                petForm.getBreed(),
                petForm.getAge(),
                petForm.getColor(),
                petForm.getWeight()
        );

        redirectAttributes.addFlashAttribute("successMessage", "Pet cadastrado com sucesso.");
        return "redirect:/shelters/" + shelterId + "/pets";
    }

    @GetMapping("/pets/{petId}/edit")
    public String showEditForm(@PathVariable("petId") Long petId, Model model) {
        Pet pet = petGateway.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado (id=" + petId + ")."));

        Long shelterId = petGateway.findShelterIdByPetId(petId)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo do pet não encontrado."));

        PetForm petForm = new PetForm(
                pet.type().name(),
                pet.name().value(),
                pet.breed(),
                pet.age().value(),
                pet.color(),
                pet.weight().value()
        );

        model.addAttribute("petId", pet.id());
        model.addAttribute("shelterId", shelterId);
        model.addAttribute("petForm", petForm);

        return "pets/edit";
    }

    @PostMapping("/pets/{petId}")
    public String updatePet(
            @PathVariable("petId") Long petId,
            @Valid @ModelAttribute("petForm") PetForm petForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        petGateway.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado (id=" + petId + ")."));

        Long shelterId = petGateway.findShelterIdByPetId(petId)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo do pet não encontrado."));

        if (bindingResult.hasErrors()) {
            model.addAttribute("petId", petId);
            model.addAttribute("shelterId", shelterId);
            return "pets/edit";
        }

        updatePetCommandHandler.execute(
                petId,
                petForm.getType(),
                petForm.getName(),
                petForm.getBreed(),
                petForm.getAge(),
                petForm.getColor(),
                petForm.getWeight()
        );

        redirectAttributes.addFlashAttribute("successMessage", "Pet atualizado com sucesso.");
        return "redirect:/shelters/" + shelterId + "/pets";
    }

    @PostMapping("/pets/{petId}/delete")
    public String deletePet(@PathVariable("petId") Long petId, RedirectAttributes redirectAttributes) {
        petGateway.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet não encontrado (id=" + petId + ")."));

        Long shelterId = petGateway.findShelterIdByPetId(petId)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo do pet não encontrado."));

        deletePetCommandHandler.execute(petId);

        redirectAttributes.addFlashAttribute("successMessage", "Pet excluído com sucesso.");
        return "redirect:/shelters/" + shelterId + "/pets";
    }

    @PostMapping("/shelters/{shelterId}/pets/import")
    public String importPets(
            @PathVariable("shelterId") Long shelterId,
            @Valid @ModelAttribute("petImportForm") PetImportForm petImportForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Shelter shelter = shelterGateway.findById(shelterId)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado (id=" + shelterId + ")."));

        MultipartFile csvFile = petImportForm.getCsvFile();
        if (csvFile == null || csvFile.isEmpty()) {
            bindingResult.addError(new FieldError("petImportForm", "csvFile", "O arquivo CSV é obrigatório."));
        } else {
            String originalFilename = csvFile.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank() || !originalFilename.toLowerCase(Locale.ROOT).endsWith(".csv")) {
                bindingResult.addError(new FieldError("petImportForm", "csvFile", "O arquivo enviado deve possuir extensão .csv."));
            }
            if (csvFile.getSize() > MAX_CSV_UPLOAD_BYTES) {
                bindingResult.addError(new FieldError("petImportForm", "csvFile", "O arquivo CSV deve ter no máximo 1 MB."));
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("shelter", shelter);
            model.addAttribute("pets", listShelterPetsQuery.execute(String.valueOf(shelterId)));
            return "pets/list";
        }

        try {
            int imported = importShelterPetsCommandHandler.execute(
                    String.valueOf(shelterId),
                    csvFile.getOriginalFilename(),
                    csvFile.getInputStream()
            );

            redirectAttributes.addFlashAttribute("successMessage", imported + " pet(s) importado(s) com sucesso.");
            return "redirect:/shelters/" + shelterId + "/pets";
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler o arquivo CSV enviado.", e);
        }
    }

    @GetMapping("/shelters/{shelterId}/pets/export")
    public ResponseEntity<ByteArrayResource> exportPets(@PathVariable("shelterId") Long shelterId) {
        Shelter shelter = shelterGateway.findById(shelterId)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado (id=" + shelterId + ")."));

        byte[] csv = exportShelterPetsCommandHandler.execute(String.valueOf(shelterId));
        String fileName = "pets-" + slugify(shelter.name()) + ".csv";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(new ByteArrayResource(csv));
    }

    private String slugify(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String slug = normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return slug.isBlank() ? "abrigo" : slug;
    }
}
