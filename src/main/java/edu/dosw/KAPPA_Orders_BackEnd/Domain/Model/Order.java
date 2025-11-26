package edu.dosw.KAPPA_Orders_BackEnd.Domain.Model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private OrderType orderType;
    private OrderStatus status;
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime scheduledPickup;
    private String pickupLocation;
    private BigDecimal total;
    private String trackingCode;
    private Integer estimatedPreparationTime;
    private String specialInstructions;
    private static final BigDecimal MIN_ORDER_AMOUNT = new BigDecimal("5000");

    public void calculateTotal() {
        this.total = items.stream()
                .map(OrderItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
        calculateTotal();
    }

    public void validateOrderAmount() {
        if (this.total == null) {
            calculateTotal();
        }

        if (this.items == null || this.items.isEmpty()) {
            return;
        }

        if (this.total.compareTo(MIN_ORDER_AMOUNT) < 0) {
            throw new IllegalArgumentException(
                    "El pedido mínimo es de $" + MIN_ORDER_AMOUNT
            );
        }
    }
}