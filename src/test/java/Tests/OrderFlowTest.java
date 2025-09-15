package Tests;

import api.steps.OrderSteps;
import com.github.javafaker.Faker;
import org.testng.annotations.Test;
import utils.BaseApiTest;
import utils.constants.TestData;
import utils.enums.OrderStatus;

public class OrderFlowTest extends BaseApiTest {
    private final OrderSteps orderSteps = new OrderSteps();
    private final Faker faker = new Faker();

    @Test
    public void orderCrudFlow() {
        Long orderId = System.currentTimeMillis();
        Long petId = 1L; // or create a pet and reuse its id
        Integer quantity = faker.number().numberBetween(TestData.DEFAULT_MIN_QTY, TestData.DEFAULT_MAX_QTY);
        String shipDateIso = java.time.OffsetDateTime.now().toString();

        orderSteps.placeOrder(orderId, petId, quantity, shipDateIso, OrderStatus.placed, true);
        orderSteps.getOrder(orderId);
        orderSteps.deleteOrder(orderId);
    }
}
