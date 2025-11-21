package edu.dosw.KAPPA_Orders_BackEnd.Application.usecases;

import edu.dosw.KAPPA_Orders_BackEnd.Application.ports.OrderRepositoryPort;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.Order;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderFilter;
import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GetOrderUseCase {

    private final OrderRepositoryPort orderRepository;

    public GetOrderUseCase(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<Order> buscarPorId(String orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> buscarPorUsuario(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> buscarPorEstado(OrderStatus estado) {
        return orderRepository.findByStatus(estado);
    }

    public List<Order> obtenerPendientes() {
        return orderRepository.findPendingOrders();
    }

    public List<Order> obtenerCompletadosHoy() {
        return orderRepository.findCompletedOrdersToday();
    }

    public List<Order> buscarPorFecha(LocalDate fecha) {
        OrderFilter filter = new OrderFilter();
        filter.setFromDate(fecha);
        filter.setToDate(fecha);
        return orderRepository.findByFilter(filter);
    }

    public List<Order> buscarPorUbicacion(String ubicacion) {
        OrderFilter filter = new OrderFilter();
        filter.setPickupLocation(ubicacion);
        return orderRepository.findByFilter(filter);
    }

    public List<Order> buscarConFiltro(OrderFilter filtro) {
        return orderRepository.findByFilter(filtro);
    }
}