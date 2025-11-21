package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderType;
import java.math.BigDecimal;

public class OrderItemCommand {
    public String orderId;
    public String productId;
    public String productName;
    public OrderType productType;
    public Integer quantity;
    public BigDecimal unitPrice;
    public String details;

    public OrderItemCommand() {}
}