package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import edu.dosw.KAPPA_Orders_BackEnd.Application.services.ScheduleClient;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import org.springframework.stereotype.Service;

@Service
public class DeleteOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final ScheduleClient scheduleClient;

    public DeleteOrderUseCase(OrderRepositoryPort orderRepository, ScheduleClient scheduleClient) {
        this.orderRepository = orderRepository;
        this.scheduleClient = scheduleClient;
    }

    public void eliminarOrden(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (order.getSlotId() != null) {
            scheduleClient.releaseTimeSlot(order.getSlotId(), orderId);
        }

        orderRepository.deleteById(orderId);
    }
}