package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class DeleteOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final GetOrderItemsUseCase getOrderItemsUseCase;

    public DeleteOrderUseCase(OrderRepositoryPort orderRepository,
                              GetOrderItemsUseCase getOrderItemsUseCase) {
        this.orderRepository = orderRepository;
        this.getOrderItemsUseCase = getOrderItemsUseCase;
    }

    public void eliminarOrden(String orderId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + orderId));

        getOrderItemsUseCase.obtenerItemsDeOrden(orderId)
                .forEach(item -> orderRepository.deleteOrderItem(item.getId()));

        orderRepository.deleteById(orderId);
    }
}