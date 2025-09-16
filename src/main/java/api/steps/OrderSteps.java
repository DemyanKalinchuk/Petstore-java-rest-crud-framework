package api.steps;

import api.pojo.dto.store.OrderDto;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import utils.assertions.BaseSoftAssert;
import utils.enums.ApiPath;
import utils.enums.HttpStatusCode;
import utils.enums.OrderStatus;
import utils.helpers.JsonHelper;
import utils.request.http.HttpRequest;

import static utils.enums.ApiPath.*;

public class OrderSteps extends BaseSoftAssert {
    private final HttpRequest httpRequest = new HttpRequest();

    /** PLACE order using DTO (no builder). Validates key fields when present. */
    @Step("Create a new order using DTO flow and check if order is created")
    public String placeOrder(Long orderId,
                             Long petId,
                             Integer quantity,
                             String shipDateIso,
                             OrderStatus orderStatus,
                             Boolean completeFlag) {

        OrderDto requestBody = new OrderDto(
                orderId,
                petId,
                quantity,
                shipDateIso,
                orderStatus.name(),
                completeFlag
        );

        String responseBody = httpRequest.postRequest(null, requestBody, STORE_ORDER);

        JsonNode json = asJson(responseBody);
        if (JsonHelper.has(json, "id"))       { assertEqualsLong(json, "id", orderId, "Place Order"); }
        if (JsonHelper.has(json, "petId"))    { assertEqualsLong(json, "petId", petId, "Place Order"); }
        if (JsonHelper.has(json, "quantity")) { assertEqualsInt(json, "quantity", quantity, "Place Order"); }

        finishAssertions();
        return responseBody;
    }

    /** GET order by id. If the API returns 'id', assert it matches. */
    @Step("Get the order")
    public String getOrder(long orderId) {
        String responseBody = httpRequest.getRequest(null, STORE_ORDER_ID, String.valueOf(orderId));

        JsonNode json = asJson(responseBody);
        if (JsonHelper.has(json, "id")) {
            assertEqualsLong(json, "id", orderId, "Get Order");
        }

        finishAssertions();
        return responseBody;
    }

    /** DELETE order and assert Petstore-style "code"==200 if present. */
    @Step("General delete order method")
    public String deleteOrder(long orderId) {
        String responseBody = httpRequest.deleteRequest(null, STORE_ORDER_ID, String.valueOf(orderId));

        JsonNode json = asJson(responseBody);
        assertCode200IfPresent(json, "Delete Order");

        finishAssertions();
        return responseBody;
    }

    @Step("Get inventory")
    public String inventory() {
        String responseBody = httpRequest.getRequest(null, STORE_INVENTORY);
        finishAssertions();
        return responseBody;
    }

    @Step("Try to get an exist order by id")
    public String getOrderExpectingStatus(long orderId, HttpStatusCode expectedStatus) {
        Response response = httpRequest.getRaw(null, ApiPath.STORE_ORDER_ID, null, String.valueOf(orderId));
        assertHttpStatusEquals(response, expectedStatus, "Get Order (negative)");
        finishAssertions();
        return (response == null) ? null : response.asString();
    }

    @Step("Try to delete an exist order by id")
    public String deleteOrderExpectingStatus(long orderId, HttpStatusCode expectedStatus) {
        Response response = httpRequest.deleteRaw(null, ApiPath.STORE_ORDER_ID, String.valueOf(orderId));
        assertHttpStatusEquals(response, expectedStatus, "Delete Order (negative)");
        finishAssertions();
        return (response == null) ? null : response.asString();
    }
}