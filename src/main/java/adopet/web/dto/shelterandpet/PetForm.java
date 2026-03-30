package adopet.web.dto.shelterandpet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PetForm {

    @NotBlank(message = "O tipo do pet é obrigatório.")
    @Size(max = 20, message = "O tipo do pet deve ter no máximo 20 caracteres.")
    private String type;

    @NotBlank(message = "O nome do pet é obrigatório.")
    @Size(max = 80, message = "O nome do pet deve ter no máximo 80 caracteres.")
    private String name;

    @NotBlank(message = "A raça do pet é obrigatória.")
    @Size(max = 80, message = "A raça do pet deve ter no máximo 80 caracteres.")
    private String breed;

    @NotNull(message = "A idade do pet é obrigatória.")
    @Min(value = 0, message = "A idade do pet não pode ser negativa.")
    @Max(value = 50, message = "A idade do pet deve ser no máximo 50 anos.")
    private Integer age;

    @NotBlank(message = "A cor do pet é obrigatória.")
    @Size(max = 40, message = "A cor do pet deve ter no máximo 40 caracteres.")
    private String color;

    @NotNull(message = "O peso do pet é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = false, message = "O peso do pet deve ser maior que zero.")
    @DecimalMax(value = "200.0", message = "O peso do pet deve ser no máximo 200 kg.")
    private Double weight;

    public PetForm() {
    }

    public PetForm(String type, String name, String breed, Integer age, String color, Double weight) {
        this.type = type;
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.color = color;
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }


    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
