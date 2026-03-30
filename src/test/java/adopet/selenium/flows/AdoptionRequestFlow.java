package adopet.selenium.flows;

import adopet.selenium.pages.HomePage;
import adopet.selenium.pages.adoptionrequests.AdoptionRequestsListPage;
import adopet.selenium.pages.pets.PetCreatePage;
import adopet.selenium.pages.pets.PetsListPage;
import adopet.selenium.pages.shelters.ShelterCreatePage;
import adopet.selenium.pages.shelters.SheltersListPage;
import org.openqa.selenium.WebDriver;

public class AdoptionRequestFlow {

    private final WebDriver driver;
    private final String baseUrl;

    public AdoptionRequestFlow(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
    }

    public AdoptionRequestsListPage createFullFlowUntilRequestCreation(
            String shelterName,
            String shelterPhone,
            String shelterEmail,
            String petType,
            String petName,
            String petBreed,
            String petAge,
            String petColor,
            String petWeight,
            String applicantName,
            String applicantEmail,
            String applicantPhone,
            String applicantDocument,
            String housingType,
            boolean hasOtherPets,
            String reason
    ) {
        HomePage homePage = new HomePage(driver).open(baseUrl);

        SheltersListPage sheltersListPage = homePage.goToShelters();
        ShelterCreatePage shelterCreatePage = sheltersListPage.goToCreateShelter();

        shelterCreatePage
                .fillName(shelterName)
                .fillPhoneNumber(shelterPhone)
                .fillEmail(shelterEmail);

        try {
            sheltersListPage = shelterCreatePage.submitSuccess();
        } catch (Exception e) {
            String title;
            try {
                title = shelterCreatePage.currentPageTitle();
            } catch (Exception ignored) {
                title = "<sem-titulo>";
            }

            String validationErrors;
            try {
                validationErrors = shelterCreatePage.debugValidationErrors();
            } catch (Exception ignored) {
                validationErrors = "";
            }

            throw new IllegalStateException(
                    "Falha ao cadastrar abrigo. " +
                            "title=" + title +
                            ", submittedName=" + shelterName +
                            ", submittedPhone=" + shelterPhone +
                            ", submittedEmail=" + shelterEmail +
                            (validationErrors.isBlank() ? "" : ", " + validationErrors),
                    e
            );
        }

        PetsListPage petsListPage = sheltersListPage.viewPetsOfShelterNamed(shelterName);
        PetCreatePage petCreatePage = petsListPage.goToCreatePet();

        petsListPage = petCreatePage
                .selectType(petType)
                .fillName(petName)
                .fillBreed(petBreed)
                .fillAge(petAge)
                .fillColor(petColor)
                .fillWeight(petWeight)
                .submitSuccess();

        AdoptionRequestsListPage requestsListPage = petsListPage.manageAdoptionsOfPetNamed(petName);

        return requestsListPage
                .goToCreate()
                .fillApplicantName(applicantName)
                .fillApplicantEmail(applicantEmail)
                .fillApplicantPhone(applicantPhone)
                .fillApplicantDocument(applicantDocument)
                .selectHousingType(housingType)
                .setHasOtherPets(hasOtherPets)
                .fillReason(reason)
                .submitSuccess();
    }

    public AdoptionRequestsListPage createContextWithoutRequest(
            String shelterName,
            String shelterPhone,
            String shelterEmail,
            String petType,
            String petName,
            String petBreed,
            String petAge,
            String petColor,
            String petWeight
    ) {
        HomePage homePage = new HomePage(driver).open(baseUrl);

        SheltersListPage sheltersListPage = homePage.goToShelters();
        ShelterCreatePage shelterCreatePage = sheltersListPage.goToCreateShelter();

        sheltersListPage = shelterCreatePage
                .fillName(shelterName)
                .fillPhoneNumber(shelterPhone)
                .fillEmail(shelterEmail)
                .submitSuccess();

        PetsListPage petsListPage = sheltersListPage.viewPetsOfShelterNamed(shelterName);
        PetCreatePage petCreatePage = petsListPage.goToCreatePet();

        petsListPage = petCreatePage
                .selectType(petType)
                .fillName(petName)
                .fillBreed(petBreed)
                .fillAge(petAge)
                .fillColor(petColor)
                .fillWeight(petWeight)
                .submitSuccess();

        return petsListPage.manageAdoptionsOfPetNamed(petName);
    }
}