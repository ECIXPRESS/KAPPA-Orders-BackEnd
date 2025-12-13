package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import edu.dosw.KAPPA_Orders_BackEnd.Application.services.StockClient;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderItem;
import edu.dosw.KAPPA_Orders_BackEnd.Utils.IdGenerator;
import org.springframework.stereotype.Service;

@Service
public class AddOrderItemUseCase {

    private final OrderRepositoryPort orderRepository;
    private final IdGenerator idGenerator;
    private final StockClient stockClient;

    public AddOrderItemUseCase(OrderRepositoryPort orderRepository, IdGenerator idGenerator,  StockClient stockClient) {
        this.orderRepository = orderRepository;
        this.idGenerator = idGenerator;
        this.stockClient = stockClient;
    }

    public OrderItem agregarItem(OrderItemCommand command) {


        Order orden = orderRepository.findById(command.orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + command.orderId));


        OrderItem item = new OrderItem(
                command.id,
                command.orderId,
                command.productId,
                command.productName,
                command.productType,
                command.quantity,
                command.unitPrice,
                command.details
        );

        if (!stockClient.hasStock(item.getId(), item.getQuantity())){
            throw new IllegalStateException("No hay stock suficiente");
        }

        stockClient.reduceStock(item.getId(), item.getQuantity());

        item.setId(idGenerator.generateItemId());

        OrderItem savedItem = orderRepository.saveOrderItem(item);
        orden.addItem(savedItem);
        orderRepository.save(orden);

        return savedItem;
    }
}