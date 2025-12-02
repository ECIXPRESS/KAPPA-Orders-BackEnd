package edu.dosw.KAPPA_Orders_BackEnd.Domain.Model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class OrderItem {

    @Id
    private String id;
    private String orderId;
    @NonNull
    private String productId;
    @NonNull
    private String productName;
    @NonNull
    private OrderType productType;
    @NonNull
    @Builder.Default
    private Integer quantity = 1;
    @NonNull
    private BigDecimal unitPrice;
    private String details;

    public OrderItem(String prod1, String s, OrderType orderType, int i, BigDecimal bigDecimal, String s1) {
    }

    public OrderItem(String order123, String prod456, String notebook, OrderType orderType, int i, BigDecimal bigDecimal, String spiral) {
    }

    /**
     * Calculates the subtotal for this order item
     */
    public BigDecimal calculateSubtotal() {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a 0");
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Validates the order item
     */
    public void validate() {
        if (productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID es requerido");
        }
        if (productName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es requerido");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a 0");
        }
    }
}