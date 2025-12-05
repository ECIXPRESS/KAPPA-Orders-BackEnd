package edu.dosw.KAPPA_Orders_BackEnd;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import edu.dosw.KAPPA_Orders_BackEnd.Application.usecases.*;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.*;
import edu.dosw.KAPPA_Orders_BackEnd.Utils.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UseCasesCompleteTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private IdGenerator idGenerator;

    private CreateOrderUseCase createOrderUseCase;
    private AddOrderItemUseCase addOrderItemUseCase;
    private GetOrderUseCase getOrderUseCase;
    private GetOrderItemsUseCase getOrderItemsUseCase;
    private UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private OrderOperationsUseCase orderOperationsUseCase;
    private DeleteOrderUseCase deleteOrderUseCase;

    private Order testOrder;
    private OrderItem testItem;

    @BeforeEach
    void setUp() {
        createOrderUseCase = new CreateOrderUseCase(orderRepository, idGenerator);
        //addOrderItemUseCase = new AddOrderItemUseCase(orderRepository, idGenerator);
        getOrderUseCase = new GetOrderUseCase(orderRepository);
        getOrderItemsUseCase = new GetOrderItemsUseCase(orderRepository);
        updateOrderStatusUseCase = new UpdateOrderStatusUseCase(orderRepository);
        orderOperationsUseCase = new OrderOperationsUseCase(orderRepository);
        deleteOrderUseCase = new DeleteOrderUseCase(orderRepository, getOrderItemsUseCase);

        testOrder = new Order();
        testOrder.setId("order123");
        testOrder.setUserId("user123");
        testOrder.setOrderType(OrderType.CAFETERIA);
        testOrder.setStatus(OrderStatus.PENDING);

        testItem = new OrderItem();
        testItem.setId("item123");
        testItem.setOrderId("order123");
        testItem.setProductId("prod456");
        testItem.setProductName("Café Americano");
        testItem.setProductType(OrderType.CAFETERIA);
        testItem.setQuantity(2);
        testItem.setUnitPrice(new BigDecimal("12.50"));
    }




    @Test
    void testGetOrderByIdFound() {
        when(orderRepository.findById("order123")).thenReturn(Optional.of(testOrder));

        Optional<Order> result = getOrderUseCase.buscarPorId("order123");

        assertTrue(result.isPresent());
        assertEquals("order123", result.get().getId());
    }

    @Test
    void testGetOrderByIdNotFound() {
        when(orderRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<Order> result = getOrderUseCase.buscarPorId("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOrdersByUser() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByUserId("user123")).thenReturn(orders);

        List<Order> result = getOrderUseCase.buscarPorUsuario("user123");

        assertEquals(1, result.size());
        assertEquals("user123", result.get(0).getUserId());
    }

    @Test
    void testGetOrdersByStatus() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(orders);

        List<Order> result = getOrderUseCase.buscarPorEstado(OrderStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals(OrderStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void testGetOrdersByDate() {
        LocalDate today = LocalDate.now();
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(orders);

        List<Order> result = getOrderUseCase.buscarPorFecha(today);

        assertEquals(1, result.size());
    }

    @Test
    void testGetOrdersByFilter() {
        OrderFilter filter = new OrderFilter();
        filter.setUserId("user123");
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByFilter(filter)).thenReturn(orders);

        List<Order> result = getOrderUseCase.buscarConFiltro(filter);

        assertEquals(1, result.size());
    }

    @Test
    void testGetOrderItems() {
        List<OrderItem> items = Arrays.asList(testItem);
        when(orderRepository.findItemsByOrderId("order123")).thenReturn(items);

        List<OrderItem> result = getOrderItemsUseCase.obtenerItemsDeOrden("order123");

        assertEquals(1, result.size());
        assertEquals("order123", result.get(0).getOrderId());
    }

    @Test
    void testGetOrderItemByIdFound() {
        when(orderRepository.findOrderItemById("item123")).thenReturn(Optional.of(testItem));

        OrderItem result = getOrderItemsUseCase.obtenerItemPorId("item123");

        assertNotNull(result);
        assertEquals("item123", result.getId());
    }

    @Test
    void testGetOrderItemByIdNotFound() {
        when(orderRepository.findOrderItemById("nonexistent")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> getOrderItemsUseCase.obtenerItemPorId("nonexistent"));
        assertTrue(exception.getMessage().contains("Item no encontrado"));
    }

    @Test
    void testUpdateOrderStatusSuccess() {
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
        command.orderId = "order123";
        command.newStatus = OrderStatus.CONFIRMED;

        Order updatedOrder = new Order();
        updatedOrder.setId("order123");
        updatedOrder.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById("order123")).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Order result = updateOrderStatusUseCase.cambiarEstado(command);

        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
    }

    @Test
    void testUpdateOrderStatusInvalidTransition() {
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
        command.orderId = "order123";
        command.newStatus = OrderStatus.DELIVERED;

        when(orderRepository.findById("order123")).thenReturn(Optional.of(testOrder));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> updateOrderStatusUseCase.cambiarEstado(command));
        assertTrue(exception.getMessage().contains("No se puede cambiar"));
    }

    @Test
    void testCancelOrder() {
        Order cancelledOrder = new Order();
        cancelledOrder.setId("order123");
        cancelledOrder.setStatus(OrderStatus.CANCELED);

        when(orderRepository.findById("order123")).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(cancelledOrder);

        Order result = updateOrderStatusUseCase.cancelarOrden("order123");

        assertEquals(OrderStatus.CANCELED, result.getStatus());
    }

    @Test
    void testConfirmOrder() {
        Order confirmedOrder = new Order();
        confirmedOrder.setId("order123");
        confirmedOrder.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById("order123")).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(confirmedOrder);

        Order result = updateOrderStatusUseCase.confirmarOrden("order123");

        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
    }

    @Test
    void testUpdateEstimatedTime() {
        Order updatedOrder = new Order();
        updatedOrder.setId("order123");
        updatedOrder.setEstimatedPreparationTime(15);

        when(orderRepository.findById("order123")).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Order result = orderOperationsUseCase.actualizarTiempoEstimado("order123", 15);

        assertEquals(15, result.getEstimatedPreparationTime());
    }


    @Test
    void testCheckOrderExists() {
        when(orderRepository.existsById("order123")).thenReturn(true);

        boolean result = orderOperationsUseCase.existeOrden("order123");

        assertTrue(result);
    }

    @Test
    void testCountUserOrders() {
        when(orderRepository.countByUserId("user123")).thenReturn(5L);

        long result = orderOperationsUseCase.contarOrdenesUsuario("user123");

        assertEquals(5L, result);
    }

    @Test
    void testCountOrdersByStatus() {
        when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(3L);

        long result = orderOperationsUseCase.contarOrdenesPorEstado("PENDING");

        assertEquals(3L, result);
    }

    @Test
    void testDeleteOrderSuccess() {
        List<OrderItem> items = Arrays.asList(testItem);
        when(orderRepository.findById("order123")).thenReturn(Optional.of(testOrder));
        when(orderRepository.findItemsByOrderId("order123")).thenReturn(items);
        doNothing().when(orderRepository).deleteOrderItem("item123");
        doNothing().when(orderRepository).deleteById("order123");

        assertDoesNotThrow(() -> deleteOrderUseCase.eliminarOrden("order123"));

        verify(orderRepository).deleteOrderItem("item123");
        verify(orderRepository).deleteById("order123");
    }

    @Test
    void testDeleteOrderNotFound() {
        when(orderRepository.findById("nonexistent")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deleteOrderUseCase.eliminarOrden("nonexistent"));
        assertTrue(exception.getMessage().contains("Orden no encontrada"));
    }

    @Test
    void testCreateOrderCommandDefaultConstructor() {
        CreateOrderCommand command = new CreateOrderCommand();
        assertNotNull(command);
    }

    @Test
    void testOrderItemCommandDefaultConstructor() {
        OrderItemCommand command = new OrderItemCommand();
        assertNotNull(command);
    }

    @Test
    void testUpdateOrderStatusCommandDefaultConstructor() {
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
        assertNotNull(command);
    }

    @Test
    void testGetPendingOrders() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findPendingOrders()).thenReturn(orders);

        List<Order> result = getOrderUseCase.obtenerPendientes();

        assertEquals(1, result.size());
    }

    @Test
    void testGetCompletedOrdersToday() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findCompletedOrdersToday()).thenReturn(orders);

        List<Order> result = getOrderUseCase.obtenerCompletadosHoy();

        assertEquals(1, result.size());
    }

    @Test
    void testGetOrdersByLocation() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByFilter(any(OrderFilter.class))).thenReturn(orders);

        List<Order> result = getOrderUseCase.buscarPorUbicacion("Cafetería");

        assertEquals(1, result.size());
    }

    @Test
    void testMarcarEnPreparacion() {
        testOrder.setStatus(OrderStatus.CONFIRMED);
        Order updatedOrder = new Order();
        updatedOrder.setId("order123");
        updatedOrder.setStatus(OrderStatus.PREPARING);

        when(orderRepository.findById("order123")).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Order result = updateOrderStatusUseCase.marcarEnPreparacion("order123");

        assertEquals(OrderStatus.PREPARING, result.getStatus());
    }

    @Test
    void testMarcarListo() {
        testOrder.setStatus(OrderStatus.PREPARING);
        Order updatedOrder = new Order();
        updatedOrder.setId("order123");
        updatedOrder.setStatus(OrderStatus.READY);

        when(orderRepository.findById("order123")).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Order result = updateOrderStatusUseCase.marcarListo("order123");

        assertEquals(OrderStatus.READY, result.getStatus());
    }

    @Test
    void testMarcarEntregado() {
        testOrder.setStatus(OrderStatus.READY);
        Order updatedOrder = new Order();
        updatedOrder.setId("order123");
        updatedOrder.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById("order123")).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Order result = updateOrderStatusUseCase.marcarEntregado("order123");

        assertEquals(OrderStatus.DELIVERED, result.getStatus());
    }
}