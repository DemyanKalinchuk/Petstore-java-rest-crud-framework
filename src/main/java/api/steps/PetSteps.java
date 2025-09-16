package api.steps;

import api.builder.pet.PetBuilder;
import api.pojo.pet.Category;
import api.pojo.pet.Pet;
import api.pojo.pet.Tag;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import utils.assertions.BaseSoftAssert;
import utils.enums.ApiPath;
import utils.enums.HttpStatusCode;
import utils.enums.PetStatus;
import utils.helpers.JsonHelper;
import utils.request.http.HttpRequest;

import java.util.List;

public class PetSteps extends BaseSoftAssert {
    private final HttpRequest httpRequest = new HttpRequest();

    /** CREATE and assert 'name' and 'status'. */
    public String createPet(Long id, Category category, String name,
                            List<String> photoUrls, List<Tag> tags, PetStatus status) {
        Pet requestBody = PetBuilder.buildNewPet(id, category, name, photoUrls, tags, status.name());
        String responseBody = httpRequest.postRequest(null, requestBody, ApiPath.PET);

        JsonNode json = asJson(responseBody);
        assertEqualsString(json, "name", name, "Create Pet");
        if (JsonHelper.has(json, "status")) {
            assertEqualsString(json, "status", status.name(), "Create Pet");
        }

        finishAssertions();
        return responseBody;
    }

    /** UPDATE and assert 'status' matches requested status. */
    public String updatePet(Long id, Category category, String name,
                            List<String> photoUrls, List<Tag> tags, PetStatus status) {
        Pet requestBody = PetBuilder.buildNewPet(id, category, name, photoUrls, tags, status.name());
        String responseBody = httpRequest.putRequest(null, requestBody, ApiPath.PET);

        JsonNode json = asJson(responseBody);
        if (JsonHelper.has(json, "status")) {
            assertEqualsString(json, "status", status.name(), "Update Pet");
        }

        finishAssertions();
        return responseBody;
    }

    /** GET and assert id equals requested id. */
    public String getPetById(long petId) {
        String responseBody = httpRequest.getRequest(null, ApiPath.PET_ID, String.valueOf(petId));

        JsonNode json = asJson(responseBody);
        assertEqualsLong(json, "id", petId, "Get Pet By Id");

        finishAssertions();
        return responseBody;
    }

    /** DELETE and assert Petstore-style "code"==200 if present. */
    public String deletePet(long petId) {
        String responseBody = httpRequest.deleteRequest(null, ApiPath.PET_ID, String.valueOf(petId));

        JsonNode json = asJson(responseBody);
        assertCode200IfPresent(json, "Delete Pet");

        finishAssertions();
        return responseBody;
    }

    public String getPetByIdExpectingStatus(long petId, HttpStatusCode expectedStatus) {
        Response response = httpRequest.getRaw(null, ApiPath.PET_ID, null, String.valueOf(petId));
        assertHttpStatusEquals(response, expectedStatus, "Get Pet By Id (negative)");
        finishAssertions();
        return (response == null) ? null : response.asString();
    }
}