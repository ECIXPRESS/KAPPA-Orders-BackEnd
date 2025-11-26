package edu.dosw.KAPPA_Orders_BackEnd.Domain.Model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    private String id;
    private String orderId;
    private String productId;
    private String productName;
    private OrderType productType;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String details;


    public OrderItem(String productId, String productName, OrderType productType,
                     Integer quantity, BigDecimal unitPrice, String details) {
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.details = details;
    }

    public OrderItem(String orderId, String productId, String productName, OrderType productType,
                     Integer quantity, BigDecimal unitPrice, String details) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productType = productType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.details = details;
    }

    public BigDecimal calculateSubtotal() {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a 0");
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}