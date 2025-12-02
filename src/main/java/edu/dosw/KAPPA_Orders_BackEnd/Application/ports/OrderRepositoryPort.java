package edu.dosw.KAPPA_Orders_BackEnd.Application.ports;

import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderItem;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderFilter;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {


    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findAll();
    void deleteById(String id);
    List<Order> findByUserId(String userId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByFilter(OrderFilter filter);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findByScheduledPickupBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findPendingOrders();
    List<Order> findCompletedOrdersToday();
    List<Order> findOrdersByPickupLocation(String location);
    boolean existsById(String orderId);
    long countByUserId(String userId);
    long countByStatus(OrderStatus status);
    OrderItem saveOrderItem(OrderItem item);
    List<OrderItem> findItemsByOrderId(String orderId);
    Optional<OrderItem> findOrderItemById(String itemId);
    void deleteOrderItem(String itemId);

}