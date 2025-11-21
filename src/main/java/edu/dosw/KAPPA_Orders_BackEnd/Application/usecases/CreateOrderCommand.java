package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderType;
import java.time.LocalDateTime;

public class CreateOrderCommand {
    public String userId;
    public OrderType orderType;
    public LocalDateTime scheduledPickup;
    public String pickupLocation;
    public String specialInstructions;

    public CreateOrderCommand() {}
}