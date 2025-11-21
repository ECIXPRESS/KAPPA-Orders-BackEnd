package edu.dosw.KAPPA_Orders_BackEnd.Domain.Model;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

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

    public OrderItem() {}

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public OrderType getProductType() {
        return productType;
    }

    public void setProductType(OrderType productType) {
        this.productType = productType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public BigDecimal calculateSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}