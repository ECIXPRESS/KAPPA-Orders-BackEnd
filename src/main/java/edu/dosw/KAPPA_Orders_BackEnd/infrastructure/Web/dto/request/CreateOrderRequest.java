package edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.request;

import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderType;
import java.time.LocalDateTime;

public class CreateOrderRequest {
    public String userId;
    public OrderType orderType;
    public LocalDateTime scheduledPickup;
    public String pickupLocation;
    public String specialInstructions;


    public CreateOrderRequest() {}
}