package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderItem;
import edu.dosw.KAPPA_Orders_BackEnd.Exception.Excepciones;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GetOrderItemsUseCase {

    private final OrderRepositoryPort orderRepository;

    public GetOrderItemsUseCase(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderItem> obtenerItemsDeOrden(String orderId) {
        Excepciones.throwIfEmpty(orderId, "orderId");
        return orderRepository.findItemsByOrderId(orderId);
    }

    public OrderItem obtenerItemPorId(String itemId) {
        Excepciones.throwIfEmpty(itemId, "itemId");
        return orderRepository.findOrderItemById(itemId)
                .orElseThrow(() -> new Excepciones.OrderItemNotFoundException(itemId));
    }
}