package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderStatus;

public class UpdateOrderStatusCommand {
    public String orderId;
    public OrderStatus newStatus;

    public UpdateOrderStatusCommand() {}
}