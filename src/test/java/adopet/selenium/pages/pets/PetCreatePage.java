package adopet.selenium.pages.pets;

import adopet.selenium.base.BasePage;
import adopet.selenium.pages.components.MessagesFragment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PetCreatePage extends BasePage {

    private static final By PAGE_TITLE = By.id("page-title");
    private static final By LINK_BACK = By.id("link-back-to-pets");

    private static final By TYPE = By.id("type");
    private static final By NAME = By.id("name");
    private static final By BREED = By.id("breed");
    private static final By AGE = By.id("age");
    private static final By COLOR = By.id("color");
    private static final By WEIGHT = By.id("weight");
    private static final By SUBMIT = By.id("submit-pet");

    private final MessagesFragment messages;

    public PetCreatePage(WebDriver driver) {
        super(driver);
        this.messages = new MessagesFragment(driver);
        ensureLoaded();
    }

    public void ensureLoaded() {
        ensureTitleStartsWith(PAGE_TITLE, "Cadastrar pet para o abrigo: ", "Pet create page");
    }

    public PetCreatePage selectType(String value) {
        selectByValue(TYPE, value);
        return this;
    }

    public PetCreatePage fillName(String value) {
        type(NAME, value);
        return this;
    }

    public PetCreatePage fillBreed(String value) {
        type(BREED, value);
        return this;
    }

    public PetCreatePage fillAge(String value) {
        type(AGE, value);
        return this;
    }

    public PetCreatePage fillColor(String value) {
        type(COLOR, value);
        return this;
    }

    public PetCreatePage fillWeight(String value) {
        type(WEIGHT, value);
        return this;
    }


    public PetsListPage submitSuccess() {
        click(SUBMIT);

        wait.until(driver -> {
            try {
                return driver.findElement(By.id("page-title"))
                        .getText()
                        .startsWith("Pets do abrigo: ");
            } catch (Exception e) {
                return false;
            }
        });

        return new PetsListPage(driver);
    }

}