package api.builder.store;

import api.pojo.store.Order;

public final class OrderBuilder {
    private OrderBuilder(){}
    public static Order buildNewOrder(Long id, Long petId, Integer quantity,
                                      String shipDateIso, String status, Boolean complete) {
        return Order.builder()
                .id(id)
                .petId(petId)
                .quantity(quantity)
                .shipDate(shipDateIso)
                .status(status)
                .complete(complete)
                .build();
    }
}
