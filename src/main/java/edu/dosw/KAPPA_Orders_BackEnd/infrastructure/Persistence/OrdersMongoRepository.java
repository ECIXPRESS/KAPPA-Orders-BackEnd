package edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Persistence;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderItem;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderStatus;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderFilter;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class OrdersMongoRepository implements OrderRepositoryPort {

    private final MongoTemplate mongoTemplate;

    public OrdersMongoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Order save(Order order) {
        return mongoTemplate.save(order, "orders");
    }

    @Override
    public Optional<Order> findById(String id) {
        Order order = mongoTemplate.findById(id, Order.class, "orders");
        return Optional.ofNullable(order);
    }

    @Override
    public List<Order> findAll() {
        return mongoTemplate.findAll(Order.class, "orders");
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, "orders");
    }

    @Override
    public List<Order> findByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Order.class, "orders");
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.find(query, Order.class, "orders");
    }

    @Override
    public List<Order> findByFilter(OrderFilter filter) {
        Query query = new Query();

        if (filter.getUserId() != null) {
            query.addCriteria(Criteria.where("userId").is(filter.getUserId()));
        }
        if (filter.getOrderType() != null) {
            query.addCriteria(Criteria.where("orderType").is(filter.getOrderType()));
        }
        if (filter.getStatus() != null) {
            query.addCriteria(Criteria.where("status").is(filter.getStatus()));
        }
        if (filter.getFromDate() != null) {
            query.addCriteria(Criteria.where("createdAt").gte(filter.getFromDate().atStartOfDay()));
        }
        if (filter.getToDate() != null) {
            query.addCriteria(Criteria.where("createdAt").lte(filter.getToDate().plusDays(1).atStartOfDay()));
        }
        if (filter.getPickupLocation() != null) {
            query.addCriteria(Criteria.where("pickupLocation").is(filter.getPickupLocation()));
        }

        return mongoTemplate.find(query, Order.class, "orders");
    }

    @Override
    public List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        Query query = new Query(Criteria.where("createdAt").gte(start).lte(end));
        return mongoTemplate.find(query, Order.class, "orders");
    }

    @Override
    public List<Order> findByScheduledPickupBetween(LocalDateTime start, LocalDateTime end) {
        Query query = new Query(Criteria.where("scheduledPickup").gte(start).lte(end));
        return mongoTemplate.find(query, Order.class, "orders");
    }

    @Override
    public List<Order> findPendingOrders() {
        Query query = new Query(Criteria.where("status").is(OrderStatus.PENDING));
        return mongoTemplate.find(query, Order.class, "orders");
    }

    @Override
    public List<Order> findCompletedOrdersToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();

        Query query = new Query(Criteria.where("status").is(OrderStatus.PENDING)
                .and("createdAt").gte(startOfDay).lte(endOfDay));
        return mongoTemplate.find(query, Order.class, "orders");
    }

    @Override
    public List<Order> findOrdersByPickupLocation(String location) {
        Query query = new Query(Criteria.where("pickupLocation").is(location));
        return mongoTemplate.find(query, Order.class, "orders");
    }

    @Override
    public boolean existsById(String orderId) {
        Query query = new Query(Criteria.where("id").is(orderId));
        return mongoTemplate.exists(query, "orders");
    }

    @Override
    public long countByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.count(query, "orders");
    }

    @Override
    public long countByStatus(OrderStatus status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.count(query, "orders");
    }

    @Override
    public OrderItem saveOrderItem(OrderItem item) {
        return mongoTemplate.save(item, "order_items");
    }

    @Override
    public List<OrderItem> findItemsByOrderId(String orderId) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        return mongoTemplate.find(query, OrderItem.class, "order_items");
    }

    @Override
    public Optional<OrderItem> findOrderItemById(String itemId) {
        OrderItem item = mongoTemplate.findById(itemId, OrderItem.class, "order_items");
        return Optional.ofNullable(item);
    }

    @Override
    public void deleteOrderItem(String itemId) {
        Query query = new Query(Criteria.where("id").is(itemId));
        mongoTemplate.remove(query, "order_items");
    }
}