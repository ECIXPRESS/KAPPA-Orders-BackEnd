package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepository;

    public UpdateOrderStatusUseCase(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order cambiarEstado(UpdateOrderStatusCommand command) {
        Order orden = orderRepository.findById(command.orderId)
                .orElseThrow(() -> new RuntimeException("No se encontró la orden: " + command.orderId));

        if (!puedeCambiarEstado(orden.getStatus(), command.newStatus)) {
            throw new RuntimeException("No se puede cambiar de " + orden.getStatus() + " a " + command.newStatus);
        }

        orden.setStatus(command.newStatus);
        return orderRepository.save(orden);
    }

    private boolean puedeCambiarEstado(OrderStatus estadoActual, OrderStatus nuevoEstado) {
        if (estadoActual == OrderStatus.PENDING) {
            return nuevoEstado == OrderStatus.CONFIRMED || nuevoEstado == OrderStatus.CANCELED;
        }
        if (estadoActual == OrderStatus.CONFIRMED) {
            return nuevoEstado == OrderStatus.PREPARING || nuevoEstado == OrderStatus.CANCELED;
        }
        if (estadoActual == OrderStatus.PREPARING) {
            return nuevoEstado == OrderStatus.READY || nuevoEstado == OrderStatus.CANCELED;
        }
        if (estadoActual == OrderStatus.READY) {
            return nuevoEstado == OrderStatus.DELIVERED;
        }
        return false;
    }

    public Order cancelarOrden(String orderId) {
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
        command.orderId = orderId;
        command.newStatus = OrderStatus.CANCELED;
        return cambiarEstado(command);
    }

    public Order confirmarOrden(String orderId) {
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
        command.orderId = orderId;
        command.newStatus = OrderStatus.CONFIRMED;
        return cambiarEstado(command);
    }

    public Order marcarEnPreparacion(String orderId) {
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
        command.orderId = orderId;
        command.newStatus = OrderStatus.PREPARING;
        return cambiarEstado(command);
    }

    public Order marcarListo(String orderId) {
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
        command.orderId = orderId;
        command.newStatus = OrderStatus.READY;
        return cambiarEstado(command);
    }

    public Order marcarEntregado(String orderId) {
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
        command.orderId = orderId;
        command.newStatus = OrderStatus.DELIVERED;
        return cambiarEstado(command);
    }
}