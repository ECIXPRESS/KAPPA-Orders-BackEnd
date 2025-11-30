package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderFactory;
import edu.dosw.KAPPA_Orders_BackEnd.Utils.IdGenerator;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final IdGenerator idGenerator;

    public CreateOrderUseCase(OrderRepositoryPort orderRepository, IdGenerator idGenerator) {
        this.orderRepository = orderRepository;
        this.idGenerator = idGenerator;
    }

    public Order crearOrden(CreateOrderCommand command) {
        Order orden = OrderFactory.createOrder(
                command.userId,
                command.orderType,
                Collections.emptyList(),
                command.scheduledPickup,
                command.pickupLocation,
                command.store
        );

        if (command.specialInstructions != null && !command.specialInstructions.trim().isEmpty()) {
            orden.setSpecialInstructions(command.specialInstructions);
        }

        orden.setId(idGenerator.generateOrderId());
        orden.setTrackingCode(idGenerator.generateTrackingCode());
        return orderRepository.save(orden);
    }
}