package edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.response;

import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderStatus;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {
    public String id;
    public String userId;
    public OrderType orderType;
    public OrderStatus status;
    public LocalDateTime createdAt;
    public LocalDateTime scheduledPickup;
    public String pickupLocation;
    public String store;
    public BigDecimal total;
    public String trackingCode;
    public Integer estimatedPreparationTime;
    public String specialInstructions;
    public BigDecimal minOrderAmount = new BigDecimal("5000");

    public OrderResponse() {}
}