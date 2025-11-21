package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderItem;
import edu.dosw.KAPPA_Orders_BackEnd.Exception.Excepciones;
import edu.dosw.KAPPA_Orders_BackEnd.Utils.IdGenerator;
import org.springframework.stereotype.Service;

@Service
public class AddOrderItemUseCase {

    private final OrderRepositoryPort orderRepository;
    private final IdGenerator idGenerator;

    public AddOrderItemUseCase(OrderRepositoryPort orderRepository, IdGenerator idGenerator) {
        this.orderRepository = orderRepository;
        this.idGenerator = idGenerator;
    }

    public OrderItem agregarItem(OrderItemCommand command) {

        Excepciones.throwIfEmpty(command.orderId, "orderId");
        Excepciones.throwIfEmpty(command.productId, "productId");
        Excepciones.throwIfEmpty(command.productName, "productName");
        Excepciones.throwIfNull(command.productType, "productType");
        Excepciones.throwIfNegative(command.quantity, "quantity");
        Excepciones.throwIfNegative(command.unitPrice, "unitPrice");

        Order orden = orderRepository.findById(command.orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + command.orderId));

        OrderItem item = new OrderItem(
                command.orderId,
                command.productId,
                command.productName,
                command.productType,
                command.quantity,
                command.unitPrice,
                command.details
        );

        item.setId(idGenerator.generateItemId());

        OrderItem savedItem = orderRepository.saveOrderItem(item);
        orden.addItem(savedItem);
        try {
            orden.validateOrderAmount();
        } catch (IllegalArgumentException e) {
            orderRepository.deleteOrderItem(savedItem.getId());
            throw new RuntimeException("No se puede agregar item: " + e.getMessage());
        }

        orderRepository.save(orden);

        return savedItem;
    }
}