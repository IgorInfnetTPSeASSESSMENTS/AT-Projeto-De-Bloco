package adopet.web.config;

import adopet.application.adoption.ApproveAdoptionRequestCommandHandler;
import adopet.application.adoption.CancelAdoptionRequestCommandHandler;
import adopet.application.adoption.CreateAdoptionRequestCommandHandler;
import adopet.application.adoption.DeleteAdoptionRequestCommandHandler;
import adopet.application.adoption.GetAdoptionRequestDetailsQuery;
import adopet.application.adoption.ListAdoptionRequestsQuery;
import adopet.application.adoption.RejectAdoptionRequestCommandHandler;
import adopet.application.adoption.RetryEligibilityAnalysisCommandHandler;
import adopet.application.adoption.UpdateAdoptionRequestCommandHandler;
import adopet.application.shelterandpet.DeletePetCommandHandler;
import adopet.application.shelterandpet.DeleteShelterCommandHandler;
import adopet.application.shelterandpet.ExportShelterPetsCommandHandler;
import adopet.application.shelterandpet.ImportShelterPetsCommandHandler;
import adopet.application.shelterandpet.ListShelterPetsQuery;
import adopet.application.shelterandpet.ListSheltersQuery;
import adopet.application.shelterandpet.RegisterPetCommandHandler;
import adopet.application.shelterandpet.RegisterShelterCommandHandler;
import adopet.application.shelterandpet.UpdatePetCommandHandler;
import adopet.application.shelterandpet.UpdateShelterCommandHandler;
import adopet.gateway.AdoptionRequestGateway;
import adopet.gateway.EligibilityAnalysisGateway;
import adopet.gateway.NotificationGateway;
import adopet.gateway.PetGateway;
import adopet.gateway.ShelterGateway;
import adopet.infrastructure.memory.InMemoryAdoptionRequestGateway;
import adopet.infrastructure.memory.InMemoryPetGateway;
import adopet.infrastructure.memory.InMemoryShelterGateway;
import adopet.infrastructure.memory.ProgrammableEligibilityAnalysisGateway;
import adopet.infrastructure.memory.ProgrammableNotificationGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ShelterGateway shelterGateway() {
        return new InMemoryShelterGateway();
    }

    @Bean
    public PetGateway petGateway(ShelterGateway shelterGateway) {
        return new InMemoryPetGateway(shelterGateway);
    }

    @Bean
    public AdoptionRequestGateway adoptionRequestGateway() {
        return new InMemoryAdoptionRequestGateway();
    }

    @Bean
    public ProgrammableEligibilityAnalysisGateway programmableEligibilityAnalysisGateway() {
        return new ProgrammableEligibilityAnalysisGateway();
    }

    @Bean
    public ProgrammableNotificationGateway programmableNotificationGateway() {
        return new ProgrammableNotificationGateway();
    }

    @Bean
    public RegisterShelterCommandHandler registerShelterCommandHandler(ShelterGateway shelterGateway) {
        return new RegisterShelterCommandHandler(shelterGateway);
    }

    @Bean
    public UpdateShelterCommandHandler updateShelterCommandHandler(ShelterGateway shelterGateway) {
        return new UpdateShelterCommandHandler(shelterGateway);
    }

    @Bean
    public DeleteShelterCommandHandler deleteShelterCommandHandler(ShelterGateway shelterGateway) {
        return new DeleteShelterCommandHandler(shelterGateway);
    }

    @Bean
    public ListSheltersQuery listSheltersQuery(ShelterGateway shelterGateway) {
        return new ListSheltersQuery(shelterGateway);
    }

    @Bean
    public RegisterPetCommandHandler registerPetCommandHandler(PetGateway petGateway) {
        return new RegisterPetCommandHandler(petGateway);
    }

    @Bean
    public UpdatePetCommandHandler updatePetCommandHandler(PetGateway petGateway) {
        return new UpdatePetCommandHandler(petGateway);
    }

    @Bean
    public DeletePetCommandHandler deletePetCommandHandler(PetGateway petGateway) {
        return new DeletePetCommandHandler(petGateway);
    }

    @Bean
    public ListShelterPetsQuery listShelterPetsQuery(PetGateway petGateway) {
        return new ListShelterPetsQuery(petGateway);
    }

    @Bean
    public ImportShelterPetsCommandHandler importShelterPetsCommandHandler(PetGateway petGateway) {
        return new ImportShelterPetsCommandHandler(petGateway);
    }

    @Bean
    public ExportShelterPetsCommandHandler exportShelterPetsCommandHandler(PetGateway petGateway) {
        return new ExportShelterPetsCommandHandler(petGateway);
    }

    @Bean
    public ListAdoptionRequestsQuery listAdoptionRequestsQuery(AdoptionRequestGateway adoptionRequestGateway) {
        return new ListAdoptionRequestsQuery(adoptionRequestGateway);
    }

    @Bean
    public GetAdoptionRequestDetailsQuery getAdoptionRequestDetailsQuery(AdoptionRequestGateway adoptionRequestGateway) {
        return new GetAdoptionRequestDetailsQuery(adoptionRequestGateway);
    }

    @Bean
    public CreateAdoptionRequestCommandHandler createAdoptionRequestCommandHandler(
            AdoptionRequestGateway adoptionRequestGateway,
            ProgrammableEligibilityAnalysisGateway programmableEligibilityAnalysisGateway,
            ProgrammableNotificationGateway programmableNotificationGateway,
            PetGateway petGateway
    ) {
        return new CreateAdoptionRequestCommandHandler(
                adoptionRequestGateway,
                programmableEligibilityAnalysisGateway,
                programmableNotificationGateway,
                petGateway
        );
    }

    @Bean
    public UpdateAdoptionRequestCommandHandler updateAdoptionRequestCommandHandler(
            AdoptionRequestGateway adoptionRequestGateway
    ) {
        return new UpdateAdoptionRequestCommandHandler(adoptionRequestGateway);
    }

    @Bean
    public ApproveAdoptionRequestCommandHandler approveAdoptionRequestCommandHandler(
            AdoptionRequestGateway adoptionRequestGateway,
            ProgrammableNotificationGateway programmableNotificationGateway,
            PetGateway petGateway
    ) {
        return new ApproveAdoptionRequestCommandHandler(
                adoptionRequestGateway,
                programmableNotificationGateway,
                petGateway
        );
    }

    @Bean
    public RejectAdoptionRequestCommandHandler rejectAdoptionRequestCommandHandler(
            AdoptionRequestGateway adoptionRequestGateway,
            ProgrammableNotificationGateway programmableNotificationGateway
    ) {
        return new RejectAdoptionRequestCommandHandler(
                adoptionRequestGateway,
                programmableNotificationGateway
        );
    }

    @Bean
    public CancelAdoptionRequestCommandHandler cancelAdoptionRequestCommandHandler(
            AdoptionRequestGateway adoptionRequestGateway,
            ProgrammableNotificationGateway programmableNotificationGateway
    ) {
        return new CancelAdoptionRequestCommandHandler(
                adoptionRequestGateway,
                programmableNotificationGateway
        );
    }

    @Bean
    public RetryEligibilityAnalysisCommandHandler retryEligibilityAnalysisCommandHandler(
            AdoptionRequestGateway adoptionRequestGateway,
            ProgrammableEligibilityAnalysisGateway programmableEligibilityAnalysisGateway,
            PetGateway petGateway
    ) {
        return new RetryEligibilityAnalysisCommandHandler(
                adoptionRequestGateway,
                programmableEligibilityAnalysisGateway,
                petGateway
        );
    }

    @Bean
    public DeleteAdoptionRequestCommandHandler deleteAdoptionRequestCommandHandler(
            AdoptionRequestGateway adoptionRequestGateway,
            PetGateway petGateway
    ) {
        return new DeleteAdoptionRequestCommandHandler(adoptionRequestGateway, petGateway);
    }
}