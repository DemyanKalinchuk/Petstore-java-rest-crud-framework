package api.steps;

import api.builder.user.UserBuilder;
import api.pojo.user.User;
import com.fasterxml.jackson.databind.JsonNode;
import utils.assertions.BaseSoftAssert;
import utils.enums.ApiPath;
import utils.helpers.QueryParams;
import utils.request.HttpRequest;

public class UserSteps extends BaseSoftAssert {
    private final HttpRequest httpRequest = new HttpRequest();

    /** CREATE and assert Petstore-style "code"==200 if present. */
    public String createUser(String firstName, String lastName, String emailAddress, String jobAsUsername) {
        User requestBody = UserBuilder.buildNewUser(firstName, lastName, emailAddress, jobAsUsername);
        String responseBody = httpRequest.postRequest(null, requestBody, ApiPath.USER);

        JsonNode json = asJson(responseBody);
        assertCode200IfPresent(json, "Create User");

        finishAssertions();
        return responseBody;
    }

    /** UPDATE and assert Petstore-style "code"==200 if present. */
    public String updateUser(String username, Long id, String firstName, String lastName,
                             String emailAddress, String password, String phoneNumber, Integer userStatus) {
        User requestBody = UserBuilder.buildPetstoreUser(id, username, firstName, lastName, emailAddress, password, phoneNumber, userStatus);
        String responseBody = httpRequest.putRequest(null, requestBody, ApiPath.USER_USERNAME, username);

        JsonNode json = asJson(responseBody);
        assertCode200IfPresent(json, "Update User");

        finishAssertions();
        return responseBody;
    }

    /** GET and assert username equals requested username (if present). */
    public String getUser(String username) {
        String responseBody = httpRequest.getRequest(null, ApiPath.USER_USERNAME, username);

        JsonNode json = asJson(responseBody);
        // Petstore returns user object -> validate a few key fields if present
        if (json != null) {
            if (json.has("username")) {
                assertEqualsString(json, "username", username, "Get User");
            }
        }

        finishAssertions();
        return responseBody;
    }

    /** DELETE and assert Petstore-style "code"==200 if present. */
    public String deleteUser(String username) {
        String responseBody = httpRequest.deleteRequest(null, ApiPath.USER_USERNAME, username);

        JsonNode json = asJson(responseBody);
        assertCode200IfPresent(json, "Delete User");

        finishAssertions();
        return responseBody;
    }

    /** LOGIN using query parameters, no strict assertion beyond non-error; adjust if backend returns fields. */
    public String login(String username, String password) {
        String responseBody = httpRequest.getWithQuery(null, ApiPath.USER_LOGIN, QueryParams.forLogin(username, password));
        // Optionally assert presence of message or session info here:
        // JsonNode json = asJson(responseBody);
        // if (JsonHelper.has(json, "message")) { ... }
        finishAssertions();
        return responseBody;
    }

    public String logout() {
        String responseBody = httpRequest.getRequest(null, ApiPath.USER_LOGOUT);
        finishAssertions();
        return responseBody;
    }
}