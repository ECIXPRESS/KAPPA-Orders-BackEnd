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
        if (estadoActual == OrderStatus.PENDIENTE) {
            return nuevoEstado == OrderStatus.CONFIRMADO || nuevoEstado == OrderStatus.CANCELADO;
        }
        if (estadoActual == OrderStatus.CONFIRMADO) {
            return nuevoEstado == OrderStatus.EN_PREPARACION || nuevoEstado == OrderStatus.CANCELADO;
        }
        if (estadoActual == OrderStatus.EN_PREPARACION) {
            return nuevoEstado == OrderStatus.LISTO || nuevoEstado == OrderStatus.CANCELADO;
        }
        if (estadoActual == OrderStatus.LISTO) {
            return nuevoEstado == OrderStatus.ENTREGADO;
        }
        return false;
    }

    public Order cancelarOrden(String orderId) {
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
        command.orderId = orderId;
        command.newStatus = OrderStatus.CANCELADO;
        return cambiarEstado(command);
    }

    public Order confirmarOrden(String orderId) {
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
        command.orderId = orderId;
        command.newStatus = OrderStatus.CONFIRMADO;
        return cambiarEstado(command);
    }

    public Order marcarEnPreparacion(String orderId) {
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
        command.orderId = orderId;
        command.newStatus = OrderStatus.EN_PREPARACION;
        return cambiarEstado(command);
    }

    public Order marcarListo(String orderId) {
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
        command.orderId = orderId;
        command.newStatus = OrderStatus.LISTO;
        return cambiarEstado(command);
    }

    public Order marcarEntregado(String orderId) {
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand();
        command.orderId = orderId;
        command.newStatus = OrderStatus.ENTREGADO;
        return cambiarEstado(command);
    }
}