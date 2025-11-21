package edu.dosw.KAPPA_Orders_BackEnd.Domain.Model;

import java.time.LocalDate;

public class OrderFilter {
    private String userId;
    private OrderType orderType;
    private OrderStatus status;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String pickupLocation;

    public OrderFilter() {}

    public String getUserId() { return userId; }
    public OrderFilter setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public OrderType getOrderType() { return orderType; }
    public OrderFilter setOrderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    public OrderStatus getStatus() { return status; }
    public OrderFilter setStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public LocalDate getFromDate() { return fromDate; }
    public OrderFilter setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public LocalDate getToDate() { return toDate; }
    public OrderFilter setToDate(LocalDate toDate) {
        this.toDate = toDate;
        return this;
    }

    public String getPickupLocation() { return pickupLocation; }
    public OrderFilter setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
        return this;
    }
}