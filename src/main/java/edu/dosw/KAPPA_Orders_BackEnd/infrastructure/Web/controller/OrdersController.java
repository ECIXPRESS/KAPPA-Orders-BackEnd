package edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.controller;

import edu.dosw.KAPPA_Orders_BackEnd.Application.usecases.*;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderItem;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderStatus;
import edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.request.*;
import edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.response.OrderResponse;
import edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.response.OrderItemResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final CreateOrderUseCase createOrderUseCase;
    private final AddOrderItemUseCase addOrderItemUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetOrderItemsUseCase getOrderItemsUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final OrderOperationsUseCase orderOperationsUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;

    public OrdersController(CreateOrderUseCase createOrderUseCase,
                            AddOrderItemUseCase addOrderItemUseCase,
                            GetOrderUseCase getOrderUseCase,
                            GetOrderItemsUseCase getOrderItemsUseCase,
                            UpdateOrderStatusUseCase updateOrderStatusUseCase,
                            OrderOperationsUseCase orderOperationsUseCase,
                            DeleteOrderUseCase deleteOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.addOrderItemUseCase = addOrderItemUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.getOrderItemsUseCase = getOrderItemsUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
        this.orderOperationsUseCase = orderOperationsUseCase;
        this.deleteOrderUseCase = deleteOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> crearPedido(@RequestBody CreateOrderRequest request) {
        try {
            CreateOrderCommand command = new CreateOrderCommand();
            command.userId = request.userId;
            command.orderType = request.orderType;
            command.scheduledPickup = request.scheduledPickup;
            command.pickupLocation = request.pickupLocation;
            command.specialInstructions = request.specialInstructions;

            Order order = createOrderUseCase.crearOrden(command);
            OrderResponse response = toOrderResponse(order);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> consultarPedido(@PathVariable String orderId) {
        Optional<Order> order = getOrderUseCase.buscarPorId(orderId);
        return order.map(o -> ResponseEntity.ok(toOrderResponse(o)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> listarPedidosUsuario(@PathVariable String userId) {
        List<Order> orders = getOrderUseCase.buscarPorUsuario(userId);
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> actualizarEstadoPedido(
            @PathVariable String orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        try {
            UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
            command.orderId = orderId;
            command.newStatus = request.newStatus;

            Order order = updateOrderStatusUseCase.cambiarEstado(command);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelarPedido(@PathVariable String orderId) {
        try {
            Order order = updateOrderStatusUseCase.cancelarOrden(orderId);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> listarPedidosPorEstado(@PathVariable OrderStatus status) {
        List<Order> orders = getOrderUseCase.buscarPorEstado(status);
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<OrderResponse>> consultarHistorialUsuario(@PathVariable String userId) {
        List<Order> orders = getOrderUseCase.buscarPorUsuario(userId);
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemResponse>> obtenerItemsDePedido(@PathVariable String orderId) {
        try {
            List<OrderItem> items = getOrderItemsUseCase.obtenerItemsDeOrden(orderId);
            List<OrderItemResponse> responses = items.stream()
                    .map(this::toOrderItemResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{orderId}/total")
    public ResponseEntity<BigDecimal> calcularTotalPedido(@PathVariable String orderId) {
        try {
            BigDecimal total = orderOperationsUseCase.calcularTotal(orderId);
            return ResponseEntity.ok(total);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/estimated-time")
    public ResponseEntity<OrderResponse> actualizarTiempoEstimado(
            @PathVariable String orderId,
            @RequestBody UpdateEstimatedTimeRequest request) {
        try {
            Order order = orderOperationsUseCase.actualizarTiempoEstimado(orderId, request.minutes);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmarPedido(@PathVariable String orderId) {
        try {
            Order order = updateOrderStatusUseCase.confirmarOrden(orderId);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/preparation")
    public ResponseEntity<OrderResponse> marcarEnPreparacion(@PathVariable String orderId) {
        try {
            Order order = updateOrderStatusUseCase.marcarEnPreparacion(orderId);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/ready")
    public ResponseEntity<OrderResponse> marcarListo(@PathVariable String orderId) {
        try {
            Order order = updateOrderStatusUseCase.marcarListo(orderId);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<OrderResponse> marcarEntregado(@PathVariable String orderId) {
        try {
            Order order = updateOrderStatusUseCase.marcarEntregado(orderId);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<OrderResponse>> buscarPedidosPorFecha(@PathVariable LocalDate date) {
        List<Order> orders = getOrderUseCase.buscarPorFecha(date);
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<OrderResponse>> filtrarPedidosPorUbicacion(@PathVariable String location) {
        List<Order> orders = getOrderUseCase.buscarPorUbicacion(location);
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<OrderResponse>> obtenerPedidosPendientes() {
        List<Order> orders = getOrderUseCase.obtenerPendientes();
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/completed/today")
    public ResponseEntity<List<OrderResponse>> obtenerPedidosCompletadosHoy() {
        List<Order> orders = getOrderUseCase.obtenerCompletadosHoy();
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> contarPedidosUsuario(@PathVariable String userId) {
        long count = orderOperationsUseCase.contarOrdenesUsuario(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{orderId}/exists")
    public ResponseEntity<Boolean> verificarExistenciaPedido(@PathVariable String orderId) {
        boolean exists = orderOperationsUseCase.existeOrden(orderId);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable String orderId) {
        try {
            deleteOrderUseCase.eliminarOrden(orderId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private OrderResponse toOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.id = order.getId();
        response.userId = order.getUserId();
        response.orderType = order.getOrderType();
        response.status = order.getStatus();
        response.createdAt = order.getCreatedAt();
        response.scheduledPickup = order.getScheduledPickup();
        response.pickupLocation = order.getPickupLocation();
        response.total = order.getTotal();
        response.trackingCode = order.getTrackingCode();
        response.estimatedPreparationTime = order.getEstimatedPreparationTime();
        response.specialInstructions = order.getSpecialInstructions();
        return response;
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.id = item.getId();
        response.orderId = item.getOrderId();
        response.productId = item.getProductId();
        response.productName = item.getProductName();
        response.productType = item.getProductType();
        response.quantity = item.getQuantity();
        response.unitPrice = item.getUnitPrice();
        response.details = item.getDetails();
        response.subtotal = item.calculateSubtotal();
        return response;
    }
}