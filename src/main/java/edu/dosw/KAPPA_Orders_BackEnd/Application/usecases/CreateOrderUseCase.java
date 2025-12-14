package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import edu.dosw.KAPPA_Orders_BackEnd.Application.services.ScheduleClient;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderFactory;
import edu.dosw.KAPPA_Orders_BackEnd.Utils.IdGenerator;
import edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.response.TimeSlotResponse;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final IdGenerator idGenerator;
    private final ScheduleClient scheduleClient;

    public CreateOrderUseCase(OrderRepositoryPort orderRepository, IdGenerator idGenerator, ScheduleClient scheduleClient) {
        this.orderRepository = orderRepository;
        this.idGenerator = idGenerator;
        this.scheduleClient = scheduleClient;
    }

    public Order crearOrden(CreateOrderCommand command) {
        if (!scheduleClient.isStoreAvailable(command.pointOfSaleId, command.scheduledPickup)) {
            throw new RuntimeException("La tienda no está disponible en ese horario");
        }

        String orderId = idGenerator.generateOrderId();

        TimeSlotResponse reservedSlot = scheduleClient.reserveTimeSlot(
                command.slotId,
                orderId,
                command.userId
        );

        if (!reservedSlot.getAvailable()) {
            throw new RuntimeException("No se pudo reservar el horario");
        }

        Order orden = OrderFactory.createOrder(
                command.userId,
                command.orderType,
                Collections.emptyList(),
                command.scheduledPickup,
                command.pickupLocation,
                command.store,
                command.pointOfSaleId,
                command.slotId,
                command.slotStartTime,
                command.slotEndTime
        );

        if (command.specialInstructions != null && !command.specialInstructions.trim().isEmpty()) {
            orden.setSpecialInstructions(command.specialInstructions);
        }

        orden.setId(orderId);
        orden.setTrackingCode(idGenerator.generateTrackingCode());

        return orderRepository.save(orden);
    }
}