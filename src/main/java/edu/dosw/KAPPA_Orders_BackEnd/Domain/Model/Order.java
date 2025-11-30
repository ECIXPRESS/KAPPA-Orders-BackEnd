package edu.dosw.KAPPA_Orders_BackEnd.Domain.Model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private OrderType orderType;
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;
    @Singular
    private List<OrderItem> items;
    private String store;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime scheduledPickup;
    private String pickupLocation;
    private BigDecimal total;
    private String trackingCode;
    private Integer estimatedPreparationTime;
    private String specialInstructions;

    private static final BigDecimal MIN_ORDER_AMOUNT = new BigDecimal("5000");

    /**
     * Returns an unmodifiable view of the items list
     */
    public List<OrderItem> getItems() {
        return items == null ? Collections.emptyList() : Collections.unmodifiableList(items);
    }

    /**
     * Calculates the total from all order items
     */
    public BigDecimal calculateTotal() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(OrderItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Creates a new Order with an additional item
     */
    public Order withAddedItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("El item no puede ser nulo");
        }

        List<OrderItem> newItems = new ArrayList<>(this.items == null ? new ArrayList<>() : this.items);
        newItems.add(item);

        BigDecimal newTotal = calculateTotalForItems(newItems);

        return this.toBuilder()
                .items(newItems)
                .total(newTotal)
                .build();
    }

    /**
     * Creates a new Order with the total calculated
     */
    public Order withCalculatedTotal() {
        return this.toBuilder()
                .total(calculateTotal())
                .build();
    }

    /**
     * Validates the order amount meets minimum requirements
     */
    public void validateOrderAmount() {
        if (this.items == null){
            throw new IllegalArgumentException("El pedido debe tener al menos un item");
        }

        BigDecimal orderTotal = this.total != null ? this.total : calculateTotal();
    }

    /**
     * Validates all required fields are present
     */
    public void validate() {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID es requerido");
        }
        if (orderType == null) {
            throw new IllegalArgumentException("El tipo de orden es requerido");
        }
        if (scheduledPickup == null) {
            throw new IllegalArgumentException("La hora de recogida es requerida");
        }
        validateOrderAmount();
    }

    private BigDecimal calculateTotalForItems(List<OrderItem> itemsList) {
        if (itemsList == null || itemsList.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return itemsList.stream()
                .map(OrderItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }
}