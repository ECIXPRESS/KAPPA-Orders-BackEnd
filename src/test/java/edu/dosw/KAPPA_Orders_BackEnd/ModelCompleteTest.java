package edu.dosw.KAPPA_Orders_BackEnd;

import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderItem;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderStatus;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderType;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderBuilder;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderFactory;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ModelCompleteTest {

    private Order order;
    private OrderItem item1;
    private OrderItem item2;

    @BeforeEach
    void setUp() {
        order = new Order();
        item1 = new OrderItem("prod1", "Product 1", OrderType.CAFETERIA, 2, new BigDecimal("10.00"), "Dettalles 1");
        item2 = new OrderItem("prod2", "Product 2", OrderType.PAPELERIA, 1, new BigDecimal("15.00"), "Dettalles 2");
    }

    @Test
    void testOrderDefaultConstructor() {
        assertNotNull(order);
        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
        assertNotNull(order.getCreatedAt());
        assertEquals(OrderStatus.PENDIENTE, order.getStatus());
    }

    @Test
    void testOrderSettersAndGetters() {
        String id = "test-id";
        String userId = "user123";
        LocalDateTime now = LocalDateTime.now();

        order.setId(id);
        order.setUserId(userId);
        order.setOrderType(OrderType.CAFETERIA);
        order.setStatus(OrderStatus.CONFIRMADO);
        order.setCreatedAt(now);

        assertEquals(id, order.getId());
        assertEquals(userId, order.getUserId());
        assertEquals(OrderType.CAFETERIA, order.getOrderType());
        assertEquals(OrderStatus.CONFIRMADO, order.getStatus());
        assertEquals(now, order.getCreatedAt());
    }

    @Test
    void testOrderAddItemAndCalculateTotal() {
        order.addItem(item1);
        order.addItem(item2);

        assertEquals(2, order.getItems().size());
        assertEquals(new BigDecimal("35.00"), order.getTotal());
    }

    @Test
    void testOrderValidateOrderAmountWithSufficientAmount() {
        OrderItem expensiveItem = new OrderItem("prod3", "Expensive", OrderType.CAFETERIA,
                1, new BigDecimal("6000"), "Detalles");
        order.addItem(expensiveItem);

        assertDoesNotThrow(() -> order.validateOrderAmount());
    }

    @Test
    void testOrderValidateOrderAmountWithInsufficientAmount() {
        order.addItem(item1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> order.validateOrderAmount());
        assertTrue(exception.getMessage().contains("El pedido mínimo es de $5000"));
    }

    @Test
    void testOrderItemDefaultConstructor() {
        OrderItem item = new OrderItem();
        assertNotNull(item);
    }

    @Test
    void testOrderItemParameterizedConstructor() {
        OrderItem item = new OrderItem("prod123", "Coffee", OrderType.CAFETERIA,
                2, new BigDecimal("12.50"), "Large");

        assertEquals("prod123", item.getProductId());
        assertEquals("Coffee", item.getProductName());
        assertEquals(OrderType.CAFETERIA, item.getProductType());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("12.50"), item.getUnitPrice());
        assertEquals("Large", item.getDetails());
    }

    @Test
    void testOrderItemConstructorWithOrderId() {
        OrderItem item = new OrderItem("order123", "prod456", "Notebook", OrderType.PAPELERIA,
                1, new BigDecimal("8.75"), "Spiral");

        assertEquals("order123", item.getOrderId());
        assertEquals("prod456", item.getProductId());
        assertEquals("Notebook", item.getProductName());
        assertEquals(OrderType.PAPELERIA, item.getProductType());
    }

    @Test
    void testOrderItemCalculateSubtotal() {
        OrderItem item = new OrderItem("prod1", "Item", OrderType.CAFETERIA, 3, new BigDecimal("10.00"), "Test");
        BigDecimal subtotal = item.calculateSubtotal();

        assertEquals(new BigDecimal("30.00"), subtotal);
    }

    @Test
    void testOrderItemCalculateSubtotalWithZeroQuantity() {
        OrderItem item = new OrderItem();
        item.setQuantity(0);
        item.setUnitPrice(new BigDecimal("10.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> item.calculateSubtotal());
        assertTrue(exception.getMessage().contains("La cantidad debe ser mayor a 0"));
    }

    @Test
    void testOrderBuilderBuildOrderWithAllFields() {
        LocalDateTime pickupTime = LocalDateTime.now().plusHours(2);
        OrderItem item = new OrderItem("prod1", "Coffee", OrderType.CAFETERIA, 1, new BigDecimal("15.00"), "Hot");

        Order builtOrder = new OrderBuilder()
                .withUserId("user123")
                .withOrderType(OrderType.CAFETERIA)
                .withItem(item)
                .withScheduledPickup(pickupTime)
                .withPickupLocation("Main Hall")
                .withSpecialInstructions("Extra sugar")
                .build();

        assertEquals("user123", builtOrder.getUserId());
        assertEquals(OrderType.CAFETERIA, builtOrder.getOrderType());
        assertEquals(1, builtOrder.getItems().size());
        assertEquals(pickupTime, builtOrder.getScheduledPickup());
        assertEquals(new BigDecimal("15.00"), builtOrder.getTotal());
    }

    @Test
    void testOrderFactoryCreateOrderWithValidParameters() {
        String userId = "user123";
        LocalDateTime pickupTime = LocalDateTime.now().plusHours(1);
        OrderItem item = new OrderItem("prod1", "Coffee", OrderType.CAFETERIA, 2, new BigDecimal("12.50"), "Large");

        Order order = OrderFactory.createOrder(userId, OrderType.CAFETERIA,
                Arrays.asList(item), pickupTime, "Cafeteria A");

        assertEquals(userId, order.getUserId());
        assertEquals(OrderType.CAFETERIA, order.getOrderType());
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("25.00"), order.getTotal());
    }

    @Test
    void testOrderFactoryCreateOrderWithNullUserId() {
        LocalDateTime pickupTime = LocalDateTime.now().plusHours(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> OrderFactory.createOrder(null, OrderType.CAFETERIA, null, pickupTime, "Location"));
        assertEquals("User ID es requerido", exception.getMessage());
    }

    @Test
    void testOrderFilterSettersAndGetters() {
        LocalDate fromDate = LocalDate.now().minusDays(7);
        LocalDate toDate = LocalDate.now();
        OrderFilter filter = new OrderFilter();

        filter.setUserId("user123")
                .setOrderType(OrderType.CAFETERIA)
                .setStatus(OrderStatus.CONFIRMADO)
                .setFromDate(fromDate)
                .setToDate(toDate)
                .setPickupLocation("Cafeteria Central");

        assertEquals("user123", filter.getUserId());
        assertEquals(OrderType.CAFETERIA, filter.getOrderType());
        assertEquals(OrderStatus.CONFIRMADO, filter.getStatus());
        assertEquals(fromDate, filter.getFromDate());
        assertEquals(toDate, filter.getToDate());
        assertEquals("Cafeteria Central", filter.getPickupLocation());
    }

    @Test
    void testOrderStatusValues() {
        assertEquals(6, OrderStatus.values().length);
        assertEquals(OrderStatus.PENDIENTE, OrderStatus.valueOf("PENDIENTE"));
        assertEquals(OrderStatus.CONFIRMADO, OrderStatus.valueOf("CONFIRMADO"));
        assertEquals(OrderStatus.EN_PREPARACION, OrderStatus.valueOf("EN_PREPARACION"));
        assertEquals(OrderStatus.LISTO, OrderStatus.valueOf("LISTO"));
        assertEquals(OrderStatus.ENTREGADO, OrderStatus.valueOf("ENTREGADO"));
        assertEquals(OrderStatus.CANCELADO, OrderStatus.valueOf("CANCELADO"));
    }

    @Test
    void testOrderTypeValues() {
        assertEquals(2, OrderType.values().length);
        assertEquals(OrderType.CAFETERIA, OrderType.valueOf("CAFETERIA"));
        assertEquals(OrderType.PAPELERIA, OrderType.valueOf("PAPELERIA"));
    }

    @Test
    void testOrderCalculateTotalWithNoItems() {
        order.calculateTotal();
        assertEquals(BigDecimal.ZERO, order.getTotal());
    }

    @Test
    void testOrderItemCalculateSubtotalWithNullPrice() {
        OrderItem item = new OrderItem();
        item.setQuantity(2);
        item.setUnitPrice(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> item.calculateSubtotal());
        assertTrue(exception.getMessage().contains("El precio unitario debe ser mayor a 0"));
    }

    @Test
    void testOrderFactoryCreateOrderWithNullPickupTime() {
        String userId = "user789";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> OrderFactory.createOrder(userId, OrderType.CAFETERIA, null, null, "Location"));
        assertEquals("La hora de recogida es requerida", exception.getMessage());
    }

    @Test
    void testOrderBuilderWithMinimumFields() {
        LocalDateTime pickupTime = LocalDateTime.now().plusHours(1);

        Order order = new OrderBuilder()
                .withUserId("user456")
                .withOrderType(OrderType.PAPELERIA)
                .withScheduledPickup(pickupTime)
                .build();

        assertEquals("user456", order.getUserId());
        assertEquals(OrderType.PAPELERIA, order.getOrderType());
        assertEquals(pickupTime, order.getScheduledPickup());
        assertEquals(BigDecimal.ZERO, order.getTotal());
    }
}