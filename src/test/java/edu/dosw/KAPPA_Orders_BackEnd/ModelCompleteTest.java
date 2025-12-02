package edu.dosw.KAPPA_Orders_BackEnd;

import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderItem;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderStatus;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderType;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderFactory;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelCompleteTest {

    private OrderItem item1;
    private OrderItem item2;

    @BeforeEach
    void setUp() {
        item1 = OrderItem.builder()
                .productId("prod1")
                .productName("Product 1")
                .productType(OrderType.CAFETERIA)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .details("Detalles 1")
                .build();

        item2 = OrderItem.builder()
                .productId("prod2")
                .productName("Product 2")
                .productType(OrderType.PAPELERIA)
                .quantity(1)
                .unitPrice(new BigDecimal("15.00"))
                .details("Detalles 2")
                .build();
    }

    @Test
    void testOrderBuilderWithDefaults() {
        Order order = Order.builder()
                .userId("user123")
                .orderType(OrderType.CAFETERIA)
                .scheduledPickup(LocalDateTime.now().plusHours(1))
                .build();

        assertNotNull(order);
        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
        assertNotNull(order.getCreatedAt());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void testOrderBuilderWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pickupTime = now.plusHours(2);

        Order order = Order.builder()
                .id("test-id")
                .userId("user123")
                .orderType(OrderType.CAFETERIA)
                .status(OrderStatus.CONFIRMED)
                .item(item1)
                .item(item2)
                .scheduledPickup(pickupTime)
                .pickupLocation("Main Hall")
                .specialInstructions("Extra sugar")
                .createdAt(now)
                .build();

        assertEquals("test-id", order.getId());
        assertEquals("user123", order.getUserId());
        assertEquals(OrderType.CAFETERIA, order.getOrderType());
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertEquals(2, order.getItems().size());
        assertEquals(pickupTime, order.getScheduledPickup());
        assertEquals("Main Hall", order.getPickupLocation());
        assertEquals("Extra sugar", order.getSpecialInstructions());
    }


    @Test
    void testOrderWithAddedItemThrowsExceptionForNull() {
        Order order = Order.builder()
                .userId("user123")
                .orderType(OrderType.CAFETERIA)
                .scheduledPickup(LocalDateTime.now().plusHours(1))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> order.withAddedItem(null));
        assertTrue(exception.getMessage().contains("item no puede ser nulo"));
    }

    @Test
    void testOrderCalculateTotal() {
        Order order = Order.builder()
                .userId("user123")
                .orderType(OrderType.CAFETERIA)
                .item(item1)
                .item(item2)
                .scheduledPickup(LocalDateTime.now().plusHours(1))
                .build();

        BigDecimal total = order.calculateTotal();
        assertEquals(new BigDecimal("35.00"), total);
    }

    @Test
    void testOrderWithCalculatedTotal() {
        Order order = Order.builder()
                .userId("user123")
                .orderType(OrderType.CAFETERIA)
                .item(item1)
                .item(item2)
                .scheduledPickup(LocalDateTime.now().plusHours(1))
                .build();

        Order orderWithTotal = order.withCalculatedTotal();
        assertEquals(new BigDecimal("35.00"), orderWithTotal.getTotal());
    }

    @Test
    void testOrderValidateOrderAmountWithSufficientAmount() {
        OrderItem expensiveItem = OrderItem.builder()
                .productId("prod3")
                .productName("Expensive")
                .productType(OrderType.CAFETERIA)
                .quantity(1)
                .unitPrice(new BigDecimal("6000"))
                .details("Detalles")
                .build();

        Order order = Order.builder()
                .userId("user123")
                .orderType(OrderType.CAFETERIA)
                .item(expensiveItem)
                .scheduledPickup(LocalDateTime.now().plusHours(1))
                .total(new BigDecimal("6000"))
                .build();

        assertDoesNotThrow(() -> order.validateOrderAmount());
    }

    @Test
    void testOrderValidateOrderAmountWithInsufficientAmount() {
        Order order = Order.builder()
                .userId("user123")
                .orderType(OrderType.CAFETERIA)
                .item(item1)
                .scheduledPickup(LocalDateTime.now().plusHours(1))
                .total(new BigDecimal("20.00"))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> order.validateOrderAmount());
        assertTrue(exception.getMessage().contains("El pedido mínimo es de $5000"));
    }

    @Test
    void testOrderValidateOrderAmountWithNoItems() {
        Order order = Order.builder()
                .userId("user123")
                .orderType(OrderType.CAFETERIA)
                .scheduledPickup(LocalDateTime.now().plusHours(1))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> order.validateOrderAmount());
        assertTrue(exception.getMessage().contains("debe tener al menos un item"));
    }

    @Test
    void testOrderValidateAllFields() {
        OrderItem expensiveItem = OrderItem.builder()
                .productId("prod3")
                .productName("Expensive")
                .productType(OrderType.CAFETERIA)
                .quantity(1)
                .unitPrice(new BigDecimal("6000"))
                .build();

        Order validOrder = Order.builder()
                .userId("user123")
                .orderType(OrderType.CAFETERIA)
                .item(expensiveItem)
                .scheduledPickup(LocalDateTime.now().plusHours(1))
                .total(new BigDecimal("6000"))
                .build();

        assertDoesNotThrow(() -> validOrder.validate());
    }

    @Test
    void testOrderValidateThrowsExceptionForMissingUserId() {
        Order order = Order.builder()
                .orderType(OrderType.CAFETERIA)
                .scheduledPickup(LocalDateTime.now().plusHours(1))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> order.validate());
        assertTrue(exception.getMessage().contains("User ID es requerido"));
    }

    @Test
    void testOrderCalculateTotalWithNoItems() {
        Order order = Order.builder()
                .userId("user123")
                .orderType(OrderType.CAFETERIA)
                .scheduledPickup(LocalDateTime.now().plusHours(1))
                .build();

        BigDecimal total = order.calculateTotal();
        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void testOrderGetItemsReturnsUnmodifiableList() {
        Order order = Order.builder()
                .userId("user123")
                .orderType(OrderType.CAFETERIA)
                .item(item1)
                .scheduledPickup(LocalDateTime.now().plusHours(1))
                .build();

        List<OrderItem> items = order.getItems();

        assertThrows(UnsupportedOperationException.class,
                () -> items.add(item2));
    }

    @Test
    void testOrderItemBuilder() {
        OrderItem item = OrderItem.builder()
                .productId("prod123")
                .productName("Coffee")
                .productType(OrderType.CAFETERIA)
                .quantity(2)
                .unitPrice(new BigDecimal("12.50"))
                .details("Large")
                .build();

        assertEquals("prod123", item.getProductId());
        assertEquals("Coffee", item.getProductName());
        assertEquals(OrderType.CAFETERIA, item.getProductType());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("12.50"), item.getUnitPrice());
        assertEquals("Large", item.getDetails());
    }

    @Test
    void testOrderItemBuilderWithOrderId() {
        OrderItem item = OrderItem.builder()
                .orderId("order123")
                .productId("prod456")
                .productName("Notebook")
                .productType(OrderType.PAPELERIA)
                .quantity(1)
                .unitPrice(new BigDecimal("8.75"))
                .details("Spiral")
                .build();

        assertEquals("order123", item.getOrderId());
        assertEquals("prod456", item.getProductId());
        assertEquals("Notebook", item.getProductName());
        assertEquals(OrderType.PAPELERIA, item.getProductType());
    }

    @Test
    void testOrderItemCalculateSubtotal() {
        OrderItem item = OrderItem.builder()
                .productId("prod1")
                .productName("Item")
                .productType(OrderType.CAFETERIA)
                .quantity(3)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        BigDecimal subtotal = item.calculateSubtotal();
        assertEquals(new BigDecimal("30.00"), subtotal);
    }

    @Test
    void testOrderItemCalculateSubtotalWithZeroQuantity() {
        OrderItem item = OrderItem.builder()
                .productId("prod1")
                .productName("Item")
                .productType(OrderType.CAFETERIA)
                .quantity(0)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> item.calculateSubtotal());
        assertTrue(exception.getMessage().contains("La cantidad debe ser mayor a 0"));
    }

    @Test
    void testOrderItemValidate() {
        OrderItem validItem = OrderItem.builder()
                .productId("prod1")
                .productName("Coffee")
                .productType(OrderType.CAFETERIA)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        assertDoesNotThrow(() -> validItem.validate());
    }

    @Test
    void testOrderItemValidateThrowsExceptionForInvalidQuantity() {
        OrderItem item = OrderItem.builder()
                .productId("prod1")
                .productName("Coffee")
                .productType(OrderType.CAFETERIA)
                .quantity(0)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> item.validate());
        assertTrue(exception.getMessage().contains("La cantidad debe ser mayor a 0"));
    }

    @Test
    void testOrderFactoryCreateOrderWithValidParameters() {
        String userId = "user123";
        LocalDateTime pickupTime = LocalDateTime.now().plusHours(1);
        OrderItem item = OrderItem.builder()
                .productId("prod1")
                .productName("Coffee")
                .productType(OrderType.CAFETERIA)
                .quantity(400)
                .unitPrice(new BigDecimal("12.50"))
                .details("Large")
                .build();

        Order order = OrderFactory.createOrder(userId, OrderType.CAFETERIA,
                Arrays.asList(item), pickupTime, "Cafeteria A", "Reggio");

        assertEquals(userId, order.getUserId());
        assertEquals(OrderType.CAFETERIA, order.getOrderType());
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("5000.00"), order.getTotal());
    }

    @Test
    void testOrderFactoryCreateOrderWithNullUserId() {
        LocalDateTime pickupTime = LocalDateTime.now().plusHours(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> OrderFactory.createOrder(null, OrderType.CAFETERIA, null, pickupTime, "Location", "Reggio"));
        assertEquals("User ID es requerido", exception.getMessage());
    }

    @Test
    void testOrderFactoryCreateOrderWithNullPickupTime() {
        String userId = "user789";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> OrderFactory.createOrder(userId, OrderType.CAFETERIA, null, null, "Location", "Reggio"));
        assertEquals("La hora de recogida es requerida", exception.getMessage());
    }

    @Test
    void testOrderFactoryCreateOrderWithSpecialInstructions() {
        String userId = "user123";
        LocalDateTime pickupTime = LocalDateTime.now().plusHours(1);
        OrderItem item = OrderItem.builder()
                .productId("prod1")
                .productName("Coffee")
                .productType(OrderType.CAFETERIA)
                .quantity(400)
                .unitPrice(new BigDecimal("12.50"))
                .build();

        Order order = OrderFactory.createOrder(userId, OrderType.CAFETERIA,
                Arrays.asList(item), pickupTime, "Cafeteria A","Reggio","Extra hot");

        assertEquals("Extra hot", order.getSpecialInstructions());
        assertEquals(new BigDecimal("5000.00"), order.getTotal());
    }

    @Test
    void testOrderFilterBuilder() {
        LocalDate fromDate = LocalDate.now().minusDays(7);
        LocalDate toDate = LocalDate.now();

        OrderFilter filter = OrderFilter.builder()
                .userId("user123")
                .orderType(OrderType.CAFETERIA)
                .status(OrderStatus.CONFIRMED)
                .fromDate(fromDate)
                .toDate(toDate)
                .pickupLocation("Cafeteria Central")
                .build();

        assertEquals("user123", filter.getUserId());
        assertEquals(OrderType.CAFETERIA, filter.getOrderType());
        assertEquals(OrderStatus.CONFIRMED, filter.getStatus());
        assertEquals(fromDate, filter.getFromDate());
        assertEquals(toDate, filter.getToDate());
        assertEquals("Cafeteria Central", filter.getPickupLocation());
    }

    @Test
    void testOrderFilterValidateDateRange() {
        OrderFilter validFilter = OrderFilter.builder()
                .fromDate(LocalDate.now().minusDays(7))
                .toDate(LocalDate.now())
                .build();

        assertDoesNotThrow(() -> validFilter.validateDateRange());
    }

    @Test
    void testOrderFilterValidateDateRangeThrowsException() {
        OrderFilter invalidFilter = OrderFilter.builder()
                .fromDate(LocalDate.now())
                .toDate(LocalDate.now().minusDays(7))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> invalidFilter.validateDateRange());
        assertTrue(exception.getMessage().contains("fecha inicial no puede ser posterior"));
    }

    @Test
    void testOrderFilterHasAnyFilter() {
        OrderFilter emptyFilter = OrderFilter.builder().build();
        assertFalse(emptyFilter.hasAnyFilter());

        OrderFilter filterWithUserId = OrderFilter.builder()
                .userId("user123")
                .build();
        assertTrue(filterWithUserId.hasAnyFilter());
    }

    @Test
    void testOrderStatusValues() {
        assertEquals(6, OrderStatus.values().length);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.CONFIRMED, OrderStatus.valueOf("CONFIRMED"));
        assertEquals(OrderStatus.PREPARING, OrderStatus.valueOf("PREPARING"));
        assertEquals(OrderStatus.READY, OrderStatus.valueOf("READY"));
        assertEquals(OrderStatus.DELIVERED, OrderStatus.valueOf("DELIVERED"));
        assertEquals(OrderStatus.CANCELED, OrderStatus.valueOf("CANCELED"));
    }

    @Test
    void testOrderTypeValues() {
        assertEquals(2, OrderType.values().length);
        assertEquals(OrderType.CAFETERIA, OrderType.valueOf("CAFETERIA"));
        assertEquals(OrderType.PAPELERIA, OrderType.valueOf("PAPELERIA"));
    }

    @Test
    void testOrderToBuilder() {
        Order original = Order.builder()
                .userId("user123")
                .orderType(OrderType.CAFETERIA)
                .scheduledPickup(LocalDateTime.now().plusHours(1))
                .build();

        Order modified = original.toBuilder()
                .status(OrderStatus.CONFIRMED)
                .build();

        // Original unchanged
        assertEquals(OrderStatus.PENDING, original.getStatus());

        // Modified has new status
        assertEquals(OrderStatus.CONFIRMED, modified.getStatus());
        assertEquals("user123", modified.getUserId());
    }

    @Test
    void testOrderItemToBuilder() {
        OrderItem original = OrderItem.builder()
                .productId("prod1")
                .productName("Coffee")
                .productType(OrderType.CAFETERIA)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        OrderItem modified = original.toBuilder()
                .quantity(5)
                .build();

        assertEquals(2, original.getQuantity());

        assertEquals(5, modified.getQuantity());
        assertEquals("Coffee", modified.getProductName());
    }
}