package edu.dosw.KAPPA_Orders_BackEnd.Domain.Model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderBuilder {
    private String userId;
    private OrderType orderType;
    private List<OrderItem> items = new ArrayList<>();
    private LocalDateTime scheduledPickup;
    private String pickupLocation;
    private String specialInstructions;

    public OrderBuilder withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public OrderBuilder withOrderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    public OrderBuilder withItem(OrderItem item) {
        this.items.add(item);
        return this;
    }

    public OrderBuilder withScheduledPickup(LocalDateTime scheduledPickup) {
        this.scheduledPickup = scheduledPickup;
        return this;
    }

    public OrderBuilder withPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
        return this;
    }

    public OrderBuilder withSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
        return this;
    }

    public Order build() {
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderType(orderType);
        order.setItems(items);
        order.setScheduledPickup(scheduledPickup);
        order.setPickupLocation(pickupLocation);
        order.setSpecialInstructions(specialInstructions);
        order.calculateTotal();
        return order;
    }
}