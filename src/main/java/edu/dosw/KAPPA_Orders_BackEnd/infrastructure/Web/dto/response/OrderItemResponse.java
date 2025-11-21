package edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.response;

import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderType;

import java.math.BigDecimal;

public class OrderItemResponse {
    public String id;
    public String orderId;
    public String productId;
    public String productName;
    public OrderType productType;
    public Integer quantity;
    public BigDecimal unitPrice;
    public String details;
    public BigDecimal subtotal;

    public OrderItemResponse() {}
}