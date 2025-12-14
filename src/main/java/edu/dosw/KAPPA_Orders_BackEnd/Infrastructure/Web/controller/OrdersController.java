package edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.controller;

import edu.dosw.KAPPA_Orders_BackEnd.Application.usecases.*;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderItem;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderStatus;
import edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.dto.response.*;
import edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.dto.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Gestión de Pedidos", description = "API para gestión completa de pedidos del sistema ECIXPRESS")
@RequiredArgsConstructor
public class OrdersController {

    private final CreateOrderUseCase createOrderUseCase;
    private final AddOrderItemUseCase addOrderItemUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetOrderItemsUseCase getOrderItemsUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final OrderOperationsUseCase orderOperationsUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;

    @PostMapping
    @Operation(summary = "Crea un nuevo pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<OrderResponse> crearPedido(@RequestBody CreateOrderRequest request) {
        try {
            System.out.println("Request recibido: " + request.userId + ", " + request.orderType);

            CreateOrderCommand command = new CreateOrderCommand();
            command.userId = request.userId;
            command.orderType = request.orderType;
            command.scheduledPickup = request.scheduledPickup;
            command.pickupLocation = request.pickupLocation;
            command.specialInstructions = request.specialInstructions;
            command.store = request.store;
            command.pointOfSaleId = request.pointOfSaleId;
            command.slotId = request.slotId;
            command.slotStartTime = request.slotStartTime;
            command.slotEndTime = request.slotEndTime;

            Order order = createOrderUseCase.crearOrden(command);
            OrderResponse response = toOrderResponse(order);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error al crear pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{orderId}/items")
    @Operation(summary = "Agrega lo que vas a comprar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item agregado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "Monto mínimo no alcanzado o datos inválidos")
    })
    public ResponseEntity<OrderItemResponse> agregarItemAPedido(
            @Parameter(description = "ID del pedido") @PathVariable String orderId,
            @RequestBody OrderItemRequest request) {
        try {
            OrderItemCommand command = new OrderItemCommand();
            command.orderId = orderId;
            command.productId = request.productId;
            command.productName = request.productName;
            command.productType = request.productType;
            command.quantity = request.quantity;
            command.unitPrice = request.unitPrice;
            command.details = request.details;

            OrderItem item = addOrderItemUseCase.agregarItem(command);
            OrderItemResponse response = toOrderItemResponse(item);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Consulta un pedido por el ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<OrderResponse> consultarPedido(
            @Parameter(description = "ID del pedido") @PathVariable String orderId) {
        Optional<Order> order = getOrderUseCase.buscarPorId(orderId);
        return order.map(o -> ResponseEntity.ok(toOrderResponse(o)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lista pedidos por usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente")
    })
    public ResponseEntity<List<OrderResponse>> listarPedidosUsuario(
            @Parameter(description = "ID del usuario") @PathVariable String userId) {
        List<Order> orders = getOrderUseCase.buscarPorUsuario(userId);
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Actualiza el estado del pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "Transición de estado inválida")
    })
    public ResponseEntity<OrderResponse> actualizarEstadoPedido(
            @Parameter(description = "ID del pedido") @PathVariable String orderId,
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
    @Operation(summary = "Cancela el pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido cancelado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "No se puede cancelar el pedido en su estado actual")
    })
    public ResponseEntity<OrderResponse> cancelarPedido(
            @Parameter(description = "ID del pedido") @PathVariable String orderId) {
        try {
            Order order = updateOrderStatusUseCase.cancelarOrden(orderId);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Lista pedidos por estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente")
    })
    public ResponseEntity<List<OrderResponse>> listarPedidosPorEstado(
            @Parameter(description = "Estado del pedido") @PathVariable OrderStatus status) {
        List<Order> orders = getOrderUseCase.buscarPorEstado(status);
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/history")
    @Operation(summary = "Consulta historial de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    })
    public ResponseEntity<List<OrderResponse>> consultarHistorialUsuario(
            @Parameter(description = "ID del usuario") @PathVariable String userId) {
        List<Order> orders = getOrderUseCase.buscarPorUsuario(userId);
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Obtenen la informacion del pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items obtenidos exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<List<OrderItemResponse>> obtenerItemsDePedido(
            @Parameter(description = "ID del pedido") @PathVariable String orderId) {
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
    @Operation(summary = "Calculo del total del pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total calculado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<BigDecimal> calcularTotalPedido(
            @Parameter(description = "ID del pedido") @PathVariable String orderId) {
        try {
            BigDecimal total = orderOperationsUseCase.calcularTotal(orderId);
            return ResponseEntity.ok(total);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/estimated-time")
    @Operation(summary = "Actualizar tiempo estimado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tiempo actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "Tiempo inválido (negativo)")
    })
    public ResponseEntity<OrderResponse> actualizarTiempoEstimado(
            @Parameter(description = "ID del pedido") @PathVariable String orderId,
            @RequestBody UpdateEstimatedTimeRequest request) {
        try {
            Order order = orderOperationsUseCase.actualizarTiempoEstimado(orderId, request.minutes);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/confirm")
    @Operation(summary = "Confirmar pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido confirmado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "No se puede confirmar el pedido en su estado actual")
    })
    public ResponseEntity<OrderResponse> confirmarPedido(
            @Parameter(description = "ID del pedido") @PathVariable String orderId) {
        try {
            Order order = updateOrderStatusUseCase.confirmarOrden(orderId);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/preparation")
    @Operation(summary = "Marcar en preparación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido marcado en preparación"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "No se puede marcar en preparación en su estado actual")
    })
    public ResponseEntity<OrderResponse> marcarEnPreparacion(
            @Parameter(description = "ID del pedido") @PathVariable String orderId) {
        try {
            Order order = updateOrderStatusUseCase.marcarEnPreparacion(orderId);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/ready")
    @Operation(summary = "Marcar como listo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido marcado como listo"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "No se puede marcar como listo en su estado actual")
    })
    public ResponseEntity<OrderResponse> marcarListo(
            @Parameter(description = "ID del pedido") @PathVariable String orderId) {
        try {
            Order order = updateOrderStatusUseCase.marcarListo(orderId);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/deliver")
    @Operation(summary = "Marcar como entregado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido marcado como entregado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "No se puede marcar como entregado en su estado actual")
    })
    public ResponseEntity<OrderResponse> marcarEntregado(
            @Parameter(description = "ID del pedido") @PathVariable String orderId) {
        try {
            Order order = updateOrderStatusUseCase.marcarEntregado(orderId);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Buscar pedidos por fecha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos obtenidos exitosamente")
    })
    public ResponseEntity<List<OrderResponse>> buscarPedidosPorFecha(
            @Parameter(description = "Fecha en formato YYYY-MM-DD") @PathVariable LocalDate date) {
        List<Order> orders = getOrderUseCase.buscarPorFecha(date);
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/location/{location}")
    @Operation(summary = "listar pedidos por ubicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos obtenidos exitosamente")
    })
    public ResponseEntity<List<OrderResponse>> filtrarPedidosPorUbicacion(
            @Parameter(description = "Nombre de la ubicación de recogida") @PathVariable String location) {
        List<Order> orders = getOrderUseCase.buscarPorUbicacion(location);
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/pending")
    @Operation(summary = "ver pedidos pendientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos pendientes obtenidos exitosamente")
    })
    public ResponseEntity<List<OrderResponse>> obtenerPedidosPendientes() {
        List<Order> orders = getOrderUseCase.obtenerPendientes();
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/completed/today")
    @Operation(summary = "ver pedidos completados hoy")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos completados obtenidos exitosamente")
    })
    public ResponseEntity<List<OrderResponse>> obtenerPedidosCompletadosHoy() {
        List<Order> orders = getOrderUseCase.obtenerCompletadosHoy();
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Contar pedidos por usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo obtenido exitosamente")
    })
    public ResponseEntity<Long> contarPedidosUsuario(
            @Parameter(description = "ID del usuario") @PathVariable String userId) {
        long count = orderOperationsUseCase.contarOrdenesUsuario(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{orderId}/exists")
    @Operation(summary = "Verificar existencia de pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación completada")
    })
    public ResponseEntity<Boolean> verificarExistenciaPedido(
            @Parameter(description = "ID del pedido") @PathVariable String orderId) {
        boolean exists = orderOperationsUseCase.existeOrden(orderId);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Eliminar pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "No se puede eliminar el pedido en su estado actual")
    })
    public ResponseEntity<Void> eliminarPedido(
            @Parameter(description = "ID del pedido") @PathVariable String orderId) {
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
        response.minOrderAmount = new BigDecimal("5000");
        response.store = order.getStore();
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