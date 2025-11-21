package edu.dosw.KAPPA_Orders_BackEnd.Domain.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class OrderFactory {
    public static Order createOrder(String userId, OrderType orderType,
                                    List<OrderItem> items, LocalDateTime pickupTime,
                                    String location) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID es requerido");
        }

        if (items == null) {
            items = new ArrayList<>();
        }

        if (pickupTime == null) {
            throw new IllegalArgumentException("La hora de recogida es requerida");
        }

        Order order = new OrderBuilder()
                .withUserId(userId)
                .withOrderType(orderType)
                .withScheduledPickup(pickupTime)
                .withPickupLocation(location)
                .build();

        items.forEach(order::addItem);
        order.calculateTotal();
        order.validateOrderAmount();

        return order;
    }
}