package api.steps;

import api.builder.user.UserBuilder;
import api.pojo.user.User;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import utils.assertions.BaseSoftAssert;
import utils.enums.ApiPath;
import utils.enums.HttpStatusCode;
import utils.helpers.JsonHelper;
import utils.helpers.QueryParams;
import utils.request.http.HttpRequest;

public class UserSteps extends BaseSoftAssert {
    private final HttpRequest httpRequest = new HttpRequest();

    @Step("CREATE and assert a new User and check success status code if present.")
    public String createUser(String firstName, String lastName, String emailAddress, String jobAsUsername) {

        User requestBody = UserBuilder.buildNewUser(firstName, lastName, emailAddress, jobAsUsername);
        String responseBody = httpRequest.postRequest(null, requestBody, ApiPath.USER);

        JsonNode json = asJson(responseBody);
        assertCode200IfPresent(json, "Create User");

        finishAssertions();
        return responseBody;
    }

    @Step("UPDATE user data and assert it, check success status code if present.")
    public String updateUser(String username, Long id, String firstName, String lastName,
                             String emailAddress, String password, String phoneNumber, Integer userStatus) {

        User requestBody = UserBuilder.buildPetstoreUser(id, username, firstName, lastName, emailAddress, password, phoneNumber, userStatus);
        String responseBody = httpRequest.putRequest(null, requestBody, ApiPath.USER_USERNAME, username);

        JsonNode json = asJson(responseBody);
        assertCode200IfPresent(json, "Update User");

        finishAssertions();
        return responseBody;
    }

    @Step("GET and assert username equals requested username (if present).")
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

    @Step("DELETE user and assert it, check success status code if present.")
    public String deleteUser(String username) {
        String responseBody = httpRequest.deleteRequest(null, ApiPath.USER_USERNAME, username);

        JsonNode json = asJson(responseBody);
        assertCode200IfPresent(json, "Delete User");

        finishAssertions();
        return responseBody;
    }

    @Step("LOGIN using query parameters, no strict assertion beyond non-error; adjust if backend returns fields.")
    public String login(String username, String password) {
        String responseBody = httpRequest.getWithQuery(null, ApiPath.USER_LOGIN, QueryParams.forLogin(username, password));

        JsonNode json = asJson(responseBody);
        assertCode200IfPresent(json, "Login");

        if (JsonHelper.has(json, "message")) {
            String message = JsonHelper.getString(json, "message");

            softAssert.assertTrue(message != null && message.toLowerCase().contains("logged in user session"),
                    "Login -> 'message' should contain 'logged in user session'");
        }
        finishAssertions();
        return responseBody;
    }

    @Step("User logout")
    public String logout() {
        String responseBody = httpRequest.getRequest(null, ApiPath.USER_LOGOUT);

        JsonNode json = asJson(responseBody);
        assertCode200IfPresent(json, "Logout");

        if (JsonHelper.has(json, "message")) {
            assertEqualsString(json, "message", "ok", "Logout");
        }

        finishAssertions();
        return responseBody;
    }

    @Step("Get unexist user by id")
    public String getUserExpectingStatus(String username, HttpStatusCode expectedStatus) {
        Response response = httpRequest.getRaw(null, ApiPath.USER_USERNAME, null, username);

        assertHttpStatusEquals(response, expectedStatus, "Get User (negative)");
        finishAssertions();

        return (response == null) ? null : response.asString();
    }

}