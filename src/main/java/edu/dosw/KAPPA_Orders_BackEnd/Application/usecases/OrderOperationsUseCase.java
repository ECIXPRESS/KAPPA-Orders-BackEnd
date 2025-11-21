package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Exception.Excepciones;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderOperationsUseCase {

    private final OrderRepositoryPort orderRepository;

    public OrderOperationsUseCase(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    public BigDecimal calcularTotal(String orderId) {
        Excepciones.throwIfEmpty(orderId, "orderId");

        Order orden = orderRepository.findById(orderId)
                .orElseThrow(() -> new Excepciones.OrderNotFoundException(orderId));

        orden.calculateTotal();
        return orden.getTotal();
    }

    public boolean validarMontoMinimo(String orderId) {
        Excepciones.throwIfEmpty(orderId, "orderId");

        Order orden = orderRepository.findById(orderId)
                .orElseThrow(() -> new Excepciones.OrderNotFoundException(orderId));

        try {
            orden.validateOrderAmount();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Order actualizarTiempoEstimado(String orderId, int minutos) {
        Excepciones.throwIfEmpty(orderId, "orderId");
        Excepciones.throwIfNegative(minutos, "minutos");

        Order orden = orderRepository.findById(orderId)
                .orElseThrow(() -> new Excepciones.OrderNotFoundException(orderId));

        orden.setEstimatedPreparationTime(minutos);
        return orderRepository.save(orden);
    }

    public boolean existeOrden(String orderId) {
        Excepciones.throwIfEmpty(orderId, "orderId");
        return orderRepository.existsById(orderId);
    }

    public long contarOrdenesUsuario(String userId) {
        Excepciones.throwIfEmpty(userId, "userId");
        return orderRepository.countByUserId(userId);
    }
}