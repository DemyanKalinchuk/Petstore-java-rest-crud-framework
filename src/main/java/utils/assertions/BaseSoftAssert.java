package utils.assertions;

import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import org.testng.asserts.SoftAssert;
import utils.enums.HttpStatusCode;
import utils.helpers.JsonHelper;

/** Shared SoftAssert helpers to be used from steps. */
public abstract class BaseSoftAssert {

    protected final SoftAssert softAssert = new SoftAssert();

    protected JsonNode asJson(final String responseBody) {
        return JsonHelper.parse(responseBody);
    }

    /** If a "code" field exists (Petstore create/update/delete style), assert it is 200. */
    protected void assertCode200IfPresent(final JsonNode node, final String context) {
        if (JsonHelper.has(node, "code")) {
            Integer code = JsonHelper.getInt(node, "code");
            softAssert.assertNotNull(code, context + " -> response field 'code' should exist");
            if (code != null) {
                softAssert.assertEquals(code.intValue(), 200, context + " -> response field 'code' should be 200");
            }
        }
    }

    protected void assertEqualsString(final JsonNode node, final String field, final String expected, final String context) {
        String actual = JsonHelper.getString(node, field);
        softAssert.assertEquals(actual, expected, context + " -> '" + field + "' mismatch");
    }

    protected void assertEqualsLong(final JsonNode node, final String field, final Long expected, final String context) {
        Long actual = JsonHelper.getLong(node, field);
        softAssert.assertEquals(actual, expected, context + " -> '" + field + "' mismatch");
    }

    protected void assertEqualsInt(final JsonNode node, final String field, final Integer expected, final String context) {
        Integer actual = JsonHelper.getInt(node, field);
        softAssert.assertEquals(actual, expected, context + " -> '" + field + "' mismatch");
    }

    protected void assertHttpStatusEquals(final Response response,
                                          final HttpStatusCode expectedStatus,
                                          final String context) {
        int actualStatus = response == null ? -1 : response.getStatusCode();
        softAssert.assertEquals(actualStatus, expectedStatus.getStatusCode(),
                context + " -> HTTP status mismatch");
    }

    /** Finish this step’s assertions. Each step method should call this at the end. */
    protected void finishAssertions() {
        softAssert.assertAll();
    }
}