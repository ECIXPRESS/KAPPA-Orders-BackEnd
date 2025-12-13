package edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.request;

import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class CreateOrderRequest {

    @Schema(example = "usuario-12345", description = "ID del usuario que crea la orden")
    public String userId;

    @Schema(example = "CAFETERIA", description = "Tipo de orden: Cafetería o Papelería")
    public OrderType orderType;

    @Schema(example = "2025-02-15T14:30:00", description = "Fecha y hora programada para la entrega o recogida")
    public LocalDateTime scheduledPickup;

    @Schema(example = "Edificio F", description = "Ubicación donde se recogerá o entregará la orden")
    public String pickupLocation;

    @Schema(example = "Llamar al llegar a la dirección", description = "Instrucciones adicionales para la tienda o el repartidor")
    public String specialInstructions;

    @Schema(example = "Café Leyenda", description = "Tienda donde se procesa la orden")
    public String store;

    public CreateOrderRequest() {}
}
