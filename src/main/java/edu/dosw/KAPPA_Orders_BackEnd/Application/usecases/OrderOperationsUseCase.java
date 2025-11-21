package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderOperationsUseCase {

    private final OrderRepositoryPort orderRepository;

    public OrderOperationsUseCase(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    public BigDecimal calcularTotal(String orderId) {
        Order orden = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + orderId));

        orden.calculateTotal();
        return orden.getTotal();
    }

    public Order actualizarTiempoEstimado(String orderId, int minutos) {
        Order orden = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + orderId));

        if (minutos < 0) {
            throw new RuntimeException("El tiempo no puede ser negativo");
        }

        orden.setEstimatedPreparationTime(minutos);
        return orderRepository.save(orden);
    }

    public boolean existeOrden(String orderId) {
        return orderRepository.existsById(orderId);
    }

    public long contarOrdenesUsuario(String userId) {
        return orderRepository.countByUserId(userId);
    }

    public long contarOrdenesPorEstado(String estado) {
        return orderRepository.countByStatus(edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderStatus.valueOf(estado));
    }
}