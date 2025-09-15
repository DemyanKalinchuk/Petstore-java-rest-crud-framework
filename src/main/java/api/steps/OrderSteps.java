package api.steps;

import api.builder.store.OrderBuilder;
import api.pojo.store.Order;
import com.fasterxml.jackson.databind.JsonNode;
import utils.assertions.BaseSoftAssert;
import utils.enums.ApiPath;
import utils.enums.OrderStatus;
import utils.helpers.JsonHelper;
import utils.request.HttpRequest;

import static utils.enums.ApiPath.STORE_ORDER_ID;

public class OrderSteps extends BaseSoftAssert {
    private final HttpRequest httpRequest = new HttpRequest();

    /** PLACE and assert id, petId, quantity (when returned as fields). */
    public String placeOrder(Long orderId, Long petId, Integer quantity, String shipDateIso,
                             OrderStatus orderStatus, Boolean completeFlag) {
        Order requestBody = OrderBuilder.buildNewOrder(orderId, petId, quantity, shipDateIso, orderStatus.name(), completeFlag);
        String responseBody = httpRequest.postRequest(null, requestBody, ApiPath.STORE_ORDER);

        JsonNode json = asJson(responseBody);
        if (JsonHelper.has(json, "id"))     { assertEqualsLong(json, "id", orderId, "Place Order"); }
        if (JsonHelper.has(json, "petId"))  { assertEqualsLong(json, "petId", petId, "Place Order"); }
        if (JsonHelper.has(json, "quantity")) { assertEqualsInt(json, "quantity", quantity, "Place Order"); }

        finishAssertions();
        return responseBody;
    }

    public String getOrder(long orderId) {
        String responseBody = httpRequest.getRequest(null, STORE_ORDER_ID, String.valueOf(orderId));

        JsonNode json = asJson(responseBody);
        if (JsonHelper.has(json, "id")) {
            assertEqualsLong(json, "id", orderId, "Get Order");
        }

        finishAssertions();
        return responseBody;
    }

    /** DELETE and assert Petstore-style "code"==200 if present. */
    public String deleteOrder(long orderId) {
        String responseBody = httpRequest.deleteRequest(null, STORE_ORDER_ID, String.valueOf(orderId));

        JsonNode json = asJson(responseBody);
        assertCode200IfPresent(json, "Delete Order");

        finishAssertions();
        return responseBody;
    }

    public String inventory() {
        String responseBody = httpRequest.getRequest(null, ApiPath.STORE_INVENTORY);
        finishAssertions();
        return responseBody;
    }
}