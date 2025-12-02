package edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.dto.request;

import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderStatus;

public class UpdateOrderStatusRequest {
    public OrderStatus newStatus;

    public UpdateOrderStatusRequest() {}
}