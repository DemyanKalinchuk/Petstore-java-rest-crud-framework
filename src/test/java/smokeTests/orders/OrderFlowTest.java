package smokeTests.orders;

import api.steps.OrderSteps;
import com.github.javafaker.Faker;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.base.BaseApiTest;
import utils.constants.TestData;
import utils.enums.HttpStatusCode;
import utils.enums.OrderStatus;

import static core.TestStepLogger.logStep;

public class OrderFlowTest extends BaseApiTest {
    private final OrderSteps orderSteps = new OrderSteps();
    private final Faker faker = new Faker();

    @DataProvider(name = "orderQuantities")
    public Object[][] orderQuantities() {
        return new Object[][]{
                { TestData.DEFAULT_MIN_QTY },
                { 2 },
                { 3 },
                { 4 },
                { TestData.DEFAULT_MAX_QTY }
        };
    }

    @DataProvider(name = "nonExistingOrderIds")
    public Object[][] nonExistingOrderIds() {
        long base = System.currentTimeMillis();
        return new Object[][]{
                { base + 999_001 }, { base + 999_002 }, { base + 999_003 }
        };
    }

    @Test(dataProvider = "nonExistingOrderIds")
    public void orderGetAndDeleteShouldBeNotFound(long unknownOrderId) {
        orderSteps.getOrderExpectingStatus(unknownOrderId, HttpStatusCode.NOT_FOUND);
        orderSteps.deleteOrderExpectingStatus(unknownOrderId, HttpStatusCode.NOT_FOUND);
    }

    @Test(dataProvider = "orderQuantities")
    public void orderCrudWithQuantity(Integer quantity) {
        Long orderId = System.currentTimeMillis();
        Long petId = 1L; // could create a pet and use its id

        String shipDateIso = java.time.OffsetDateTime.now().toString();
        logStep("Create a new Order");
        orderSteps.placeOrder(orderId, petId, quantity, shipDateIso, OrderStatus.placed, true);

        logStep("Get order by Id");
        orderSteps.getOrder(orderId);

        logStep("Delete order");
        orderSteps.deleteOrder(orderId);
    }

    @Test
    public void orderCrudFlow() {
        Long orderId = System.currentTimeMillis();
        Long petId = 1L; // or create a pet and reuse its id
        Integer quantity = faker.number().numberBetween(TestData.DEFAULT_MIN_QTY, TestData.DEFAULT_MAX_QTY);
        String shipDateIso = java.time.OffsetDateTime.now().toString();

        logStep("Create a new Order");
        orderSteps.placeOrder(orderId, petId, quantity, shipDateIso, OrderStatus.placed, true);

        logStep("Get order by Id");
        orderSteps.getOrder(orderId);

        logStep("Delete order");
        orderSteps.deleteOrder(orderId);
    }

    @Test
    public void getInventoryTest() {
        logStep("Inventory: basic assertions for available/pending/sold");
        orderSteps.inventory();
    }
}
