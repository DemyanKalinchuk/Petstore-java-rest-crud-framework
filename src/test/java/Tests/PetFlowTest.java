package Tests;

import api.pojo.pet.Category;
import api.pojo.pet.Tag;
import api.steps.PetSteps;
import com.github.javafaker.Faker;
import org.testng.annotations.Test;
import utils.BaseApiTest;
import utils.constants.TestData;
import utils.enums.PetStatus;

import java.util.List;

import static core.TestStepLogger.logStep;

public class PetFlowTest extends BaseApiTest {
    private final PetSteps petSteps = new PetSteps();
    private final Faker faker = new Faker();

    @Test
    public void petCrudFlow() {
        Long petId = System.currentTimeMillis();
        Category category = Category.builder().id(TestData.DEFAULT_CATEGORY_ID).name(TestData.DEFAULT_CATEGORY_NAME).build();
        String petName = faker.dog().name();
        List<String> photoUrls = List.of(TestData.DEFAULT_PHOTO_BASE + petId);
        var tags = List.of(Tag.builder().id(1L).name("cute").build());

        logStep("Create a new Pet");
        petSteps.createPet(petId, category, petName, photoUrls, tags, PetStatus.available);

        logStep("Get created Pet by Id");
        petSteps.getPetById(petId);

        logStep("Update Pet status");
        petSteps.updatePet(petId, category, petName, photoUrls, tags, PetStatus.sold);

        logStep("Delete Pet by Id");
        petSteps.deletePet(petId);
    }
}