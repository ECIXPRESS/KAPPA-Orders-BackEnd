package edu.dosw.KAPPA_Orders_BackEnd.Domain.Model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Factory class for creating Order instances with validation
 */
public class OrderFactory {

    /**
     * Creates a new Order with the specified parameters (CON SLOT)
     */
    public static Order createOrder(String userId, OrderType orderType, List<OrderItem> items,
                                    LocalDateTime pickupTime,
                                    String location, String store,
                                    String pointOfSaleId, String slotId,
                                    LocalDateTime slotStartTime, LocalDateTime slotEndTime) {
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
                .pointOfSaleId(pointOfSaleId)
                .slotId(slotId)
                .slotStartTime(slotStartTime)
                .slotEndTime(slotEndTime)
                .build();

        order = order.withCalculatedTotal();
        order.validate();

        return order;
    }

    /**
     * Creates a new Order with the specified parameters (SIN SLOT - backward compatibility)
     */
    public static Order createOrder(String userId, OrderType orderType, List<OrderItem> items,
                                    LocalDateTime pickupTime,
                                    String location, String store) {
        return createOrder(userId, orderType, items, pickupTime, location, store,
                null, null, null, null);
    }

    /**
     * Creates a new Order with the specified parameters (SIN SLOT - con instrucciones especiales)
     */
    public static Order createOrder(String userId, OrderType orderType, List<OrderItem> items,
                                    LocalDateTime pickupTime,
                                    String location, String store, String specialInstructions) {
        Order order = createOrder(userId, orderType, items, pickupTime, location, store,
                null, null, null, null);
        order.setSpecialInstructions(specialInstructions);
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