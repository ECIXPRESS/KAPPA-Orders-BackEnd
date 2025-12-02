package edu.dosw.KAPPA_Orders_BackEnd;

import edu.dosw.KAPPA_Orders_BackEnd.Application.usecases.*;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderItem;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderStatus;
import edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.controller.OrdersController;
import edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.dto.request.*;
import edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.dto.response.OrderResponse;
import edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.dto.response.OrderItemResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderType.CAFETERIA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdersControllerTest {

    @Mock
    private CreateOrderUseCase createOrderUseCase;
    @Mock
    private AddOrderItemUseCase addOrderItemUseCase;
    @Mock
    private GetOrderUseCase getOrderUseCase;
    @Mock
    private GetOrderItemsUseCase getOrderItemsUseCase;
    @Mock
    private UpdateOrderStatusUseCase updateOrderStatusUseCase;
    @Mock
    private OrderOperationsUseCase orderOperationsUseCase;
    @Mock
    private DeleteOrderUseCase deleteOrderUseCase;

    @InjectMocks
    private OrdersController ordersController;

    private Order testOrder;
    private OrderItem testOrderItem;
    private final String TEST_USER_ID = "user123";
    private final String TEST_ORDER_ID = "order456";

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(TEST_ORDER_ID);
        testOrder.setUserId(TEST_USER_ID);
        testOrder.setOrderType(CAFETERIA);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setScheduledPickup(LocalDateTime.now().plusHours(1));
        testOrder.setPickupLocation("Cafetería Principal");
        testOrder.setTotal(new BigDecimal("15000"));
        testOrder.setTrackingCode("TRACK123");
        testOrder.setEstimatedPreparationTime(15);
        testOrder.setSpecialInstructions("Sin azúcar");

        testOrderItem = new OrderItem();
        testOrderItem.setId("item123");
        testOrderItem.setOrderId(TEST_ORDER_ID);
        testOrderItem.setProductId("product789");
        testOrderItem.setProductName("Café Americano");
        testOrderItem.setQuantity(2);
        testOrderItem.setUnitPrice(new BigDecimal("5000"));
        testOrderItem.setDetails("Grande");
    }

    @Test
    void testCrearPedidoExitoso() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.orderType = CAFETERIA;
        request.scheduledPickup = LocalDateTime.now().plusHours(1);
        request.pickupLocation = "Cafetería Principal";
        request.specialInstructions = "Sin azúcar";

        when(createOrderUseCase.crearOrden(any(CreateOrderCommand.class))).thenReturn(testOrder);

        ResponseEntity<OrderResponse> response = ordersController.crearPedido(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_ORDER_ID, response.getBody().id);
        verify(createOrderUseCase).crearOrden(any(CreateOrderCommand.class));
    }

    @Test
    void testCrearPedidoConError() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.orderType = CAFETERIA;

        when(createOrderUseCase.crearOrden(any(CreateOrderCommand.class)))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<OrderResponse> response = ordersController.crearPedido(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAgregarItemExitoso() {
        OrderItemRequest request = new OrderItemRequest();
        request.productId = "product789";
        request.productName = "Café Americano";
        request.quantity = 2;
        request.unitPrice = new BigDecimal("5000");
        request.details = "Grande";

        when(addOrderItemUseCase.agregarItem(any(OrderItemCommand.class))).thenReturn(testOrderItem);

        ResponseEntity<OrderItemResponse> response = ordersController.agregarItemAPedido(TEST_ORDER_ID, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(addOrderItemUseCase).agregarItem(any(OrderItemCommand.class));
    }

    @Test
    void testAgregarItemConError() {
        OrderItemRequest request = new OrderItemRequest();

        when(addOrderItemUseCase.agregarItem(any(OrderItemCommand.class)))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<OrderItemResponse> response = ordersController.agregarItemAPedido(TEST_ORDER_ID, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testConsultarPedidoExistente() {
        when(getOrderUseCase.buscarPorId(TEST_ORDER_ID)).thenReturn(Optional.of(testOrder));

        ResponseEntity<OrderResponse> response = ordersController.consultarPedido(TEST_ORDER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_ORDER_ID, response.getBody().id);
    }

    @Test
    void testConsultarPedidoNoEncontrado() {
        when(getOrderUseCase.buscarPorId(TEST_ORDER_ID)).thenReturn(Optional.empty());

        ResponseEntity<OrderResponse> response = ordersController.consultarPedido(TEST_ORDER_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testListarPedidosUsuarioConResultados() {
        List<Order> orders = Arrays.asList(testOrder);
        when(getOrderUseCase.buscarPorUsuario(TEST_USER_ID)).thenReturn(orders);

        ResponseEntity<List<OrderResponse>> response = ordersController.listarPedidosUsuario(TEST_USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testListarPedidosUsuarioVacio() {
        when(getOrderUseCase.buscarPorUsuario(TEST_USER_ID)).thenReturn(Collections.emptyList());

        ResponseEntity<List<OrderResponse>> response = ordersController.listarPedidosUsuario(TEST_USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testActualizarEstadoExitoso() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.newStatus = OrderStatus.PREPARING;

        when(updateOrderStatusUseCase.cambiarEstado(any(UpdateOrderStatusCommand.class))).thenReturn(testOrder);

        ResponseEntity<OrderResponse> response = ordersController.actualizarEstadoPedido(TEST_ORDER_ID, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(updateOrderStatusUseCase).cambiarEstado(any(UpdateOrderStatusCommand.class));
    }

    @Test
    void testActualizarEstadoNoEncontrado() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.newStatus = OrderStatus.PREPARING;

        when(updateOrderStatusUseCase.cambiarEstado(any(UpdateOrderStatusCommand.class)))
                .thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<OrderResponse> response = ordersController.actualizarEstadoPedido(TEST_ORDER_ID, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCancelarPedidoExitoso() {
        when(updateOrderStatusUseCase.cancelarOrden(TEST_ORDER_ID)).thenReturn(testOrder);

        ResponseEntity<OrderResponse> response = ordersController.cancelarPedido(TEST_ORDER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(updateOrderStatusUseCase).cancelarOrden(TEST_ORDER_ID);
    }

    @Test
    void testCancelarPedidoNoEncontrado() {
        when(updateOrderStatusUseCase.cancelarOrden(TEST_ORDER_ID))
                .thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<OrderResponse> response = ordersController.cancelarPedido(TEST_ORDER_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testListarPedidosPorEstado() {
        List<Order> orders = Arrays.asList(testOrder);
        when(getOrderUseCase.buscarPorEstado(OrderStatus.PENDING)).thenReturn(orders);

        ResponseEntity<List<OrderResponse>> response = ordersController.listarPedidosPorEstado(OrderStatus.PENDING);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testConsultarHistorialUsuario() {
        List<Order> orders = Arrays.asList(testOrder);
        when(getOrderUseCase.buscarPorUsuario(TEST_USER_ID)).thenReturn(orders);

        ResponseEntity<List<OrderResponse>> response = ordersController.consultarHistorialUsuario(TEST_USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testObtenerItemsDePedidoExitoso() {
        List<OrderItem> items = Arrays.asList(testOrderItem);
        when(getOrderItemsUseCase.obtenerItemsDeOrden(TEST_ORDER_ID)).thenReturn(items);

        ResponseEntity<List<OrderItemResponse>> response = ordersController.obtenerItemsDePedido(TEST_ORDER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testObtenerItemsDePedidoNoEncontrado() {
        when(getOrderItemsUseCase.obtenerItemsDeOrden(TEST_ORDER_ID))
                .thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<List<OrderItemResponse>> response = ordersController.obtenerItemsDePedido(TEST_ORDER_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCalcularTotalPedidoExitoso() {
        BigDecimal total = new BigDecimal("15000");
        when(orderOperationsUseCase.calcularTotal(TEST_ORDER_ID)).thenReturn(total);

        ResponseEntity<BigDecimal> response = ordersController.calcularTotalPedido(TEST_ORDER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(total, response.getBody());
    }

    @Test
    void testCalcularTotalPedidoNoEncontrado() {
        when(orderOperationsUseCase.calcularTotal(TEST_ORDER_ID))
                .thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<BigDecimal> response = ordersController.calcularTotalPedido(TEST_ORDER_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testActualizarTiempoEstimadoExitoso() {
        UpdateEstimatedTimeRequest request = new UpdateEstimatedTimeRequest();
        request.minutes = 20;

        when(orderOperationsUseCase.actualizarTiempoEstimado(TEST_ORDER_ID, 20)).thenReturn(testOrder);

        ResponseEntity<OrderResponse> response = ordersController.actualizarTiempoEstimado(TEST_ORDER_ID, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testActualizarTiempoEstimadoNoEncontrado() {
        UpdateEstimatedTimeRequest request = new UpdateEstimatedTimeRequest();
        request.minutes = 20;

        when(orderOperationsUseCase.actualizarTiempoEstimado(TEST_ORDER_ID, 20))
                .thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<OrderResponse> response = ordersController.actualizarTiempoEstimado(TEST_ORDER_ID, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testConfirmarPedidoExitoso() {
        when(updateOrderStatusUseCase.confirmarOrden(TEST_ORDER_ID)).thenReturn(testOrder);

        ResponseEntity<OrderResponse> response = ordersController.confirmarPedido(TEST_ORDER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testConfirmarPedidoNoEncontrado() {
        when(updateOrderStatusUseCase.confirmarOrden(TEST_ORDER_ID))
                .thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<OrderResponse> response = ordersController.confirmarPedido(TEST_ORDER_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testMarcarEnPreparacionExitoso() {
        when(updateOrderStatusUseCase.marcarEnPreparacion(TEST_ORDER_ID)).thenReturn(testOrder);

        ResponseEntity<OrderResponse> response = ordersController.marcarEnPreparacion(TEST_ORDER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testMarcarEnPreparacionNoEncontrado() {
        when(updateOrderStatusUseCase.marcarEnPreparacion(TEST_ORDER_ID))
                .thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<OrderResponse> response = ordersController.marcarEnPreparacion(TEST_ORDER_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testMarcarListoExitoso() {
        when(updateOrderStatusUseCase.marcarListo(TEST_ORDER_ID)).thenReturn(testOrder);

        ResponseEntity<OrderResponse> response = ordersController.marcarListo(TEST_ORDER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testMarcarListoNoEncontrado() {
        when(updateOrderStatusUseCase.marcarListo(TEST_ORDER_ID))
                .thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<OrderResponse> response = ordersController.marcarListo(TEST_ORDER_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testMarcarEntregadoExitoso() {
        when(updateOrderStatusUseCase.marcarEntregado(TEST_ORDER_ID)).thenReturn(testOrder);

        ResponseEntity<OrderResponse> response = ordersController.marcarEntregado(TEST_ORDER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testMarcarEntregadoNoEncontrado() {
        when(updateOrderStatusUseCase.marcarEntregado(TEST_ORDER_ID))
                .thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<OrderResponse> response = ordersController.marcarEntregado(TEST_ORDER_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testBuscarPedidosPorFecha() {
        LocalDate date = LocalDate.now();
        List<Order> orders = Arrays.asList(testOrder);
        when(getOrderUseCase.buscarPorFecha(date)).thenReturn(orders);

        ResponseEntity<List<OrderResponse>> response = ordersController.buscarPedidosPorFecha(date);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testFiltrarPedidosPorUbicacion() {
        String location = "Cafetería Principal";
        List<Order> orders = Arrays.asList(testOrder);
        when(getOrderUseCase.buscarPorUbicacion(location)).thenReturn(orders);

        ResponseEntity<List<OrderResponse>> response = ordersController.filtrarPedidosPorUbicacion(location);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testObtenerPedidosPendientes() {
        List<Order> orders = Arrays.asList(testOrder);
        when(getOrderUseCase.obtenerPendientes()).thenReturn(orders);

        ResponseEntity<List<OrderResponse>> response = ordersController.obtenerPedidosPendientes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testObtenerPedidosCompletadosHoy() {
        List<Order> orders = Arrays.asList(testOrder);
        when(getOrderUseCase.obtenerCompletadosHoy()).thenReturn(orders);

        ResponseEntity<List<OrderResponse>> response = ordersController.obtenerPedidosCompletadosHoy();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testContarPedidosUsuario() {
        long count = 5L;
        when(orderOperationsUseCase.contarOrdenesUsuario(TEST_USER_ID)).thenReturn(count);

        ResponseEntity<Long> response = ordersController.contarPedidosUsuario(TEST_USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(count, response.getBody());
    }

    @Test
    void testVerificarExistenciaPedido() {
        when(orderOperationsUseCase.existeOrden(TEST_ORDER_ID)).thenReturn(true);

        ResponseEntity<Boolean> response = ordersController.verificarExistenciaPedido(TEST_ORDER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    void testEliminarPedidoExitoso() {
        doNothing().when(deleteOrderUseCase).eliminarOrden(TEST_ORDER_ID);

        ResponseEntity<Void> response = ordersController.eliminarPedido(TEST_ORDER_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteOrderUseCase).eliminarOrden(TEST_ORDER_ID);
    }

    @Test
    void testEliminarPedidoNoEncontrado() {
        doThrow(new RuntimeException("No encontrado"))
                .when(deleteOrderUseCase).eliminarOrden(TEST_ORDER_ID);

        ResponseEntity<Void> response = ordersController.eliminarPedido(TEST_ORDER_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}