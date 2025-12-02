package edu.dosw.KAPPA_Orders_BackEnd.Domain.Model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Factory class for creating Order instances with validation
 */


public class OrderFactory {

    /**
     * Creates a new Order with the specified parameters
     */
    public static Order createOrder(String userId, OrderType orderType, List<OrderItem> items,
                                    LocalDateTime pickupTime,
                                    String location, String store) {
        orderChecks(userId, orderType, items, pickupTime);

        Order order = Order.builder()
                .userId(userId)
                .orderType(orderType)
                .items(items)
                .scheduledPickup(pickupTime)
                .pickupLocation(location)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .store(store)
                .build();

        order = order.withCalculatedTotal();
        order.validate();

        return order;
    }

    public static Order createOrder(String userId, OrderType orderType, List<OrderItem> items,
                                    LocalDateTime pickupTime,
                                    String location, String store, String specialInstructions) {
        orderChecks(userId, orderType, items, pickupTime);

        Order order = Order.builder()
                .userId(userId)
                .orderType(orderType)
                .items(items)
                .scheduledPickup(pickupTime)
                .pickupLocation(location)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .store(store)
                .specialInstructions(specialInstructions)
                .build();

        order = order.withCalculatedTotal();
        order.validate();

        return order;
    }

    private static void orderChecks(String userId, OrderType orderType, List<OrderItem> items, LocalDateTime pickupTime) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID es requerido");
        }
        if (orderType == null) {
            throw new IllegalArgumentException("El tipo de orden es requerido");
        }
        if (pickupTime == null) {
            throw new IllegalArgumentException("La hora de recogida es requerida");
        }
        if (items == null) {
            throw new IllegalArgumentException("El pedido debe tener al menos un item");
        }
        items.forEach(OrderItem::validate);
    }
}