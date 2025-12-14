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

    @Schema(example = "tienda-123", description = "ID único del punto de venta (para verificación de horarios con Operation Schedule)")
    public String pointOfSaleId;

    @Schema(example = "slot-123", description = "ID del slot a reservar en Operation Schedule")
    public String slotId;

    @Schema(example = "2025-02-15T14:30:00", description = "Hora de inicio del slot")
    public LocalDateTime slotStartTime;

    @Schema(example = "2025-02-15T15:00:00", description = "Hora de fin del slot")
    public LocalDateTime slotEndTime;

    public CreateOrderRequest() {}
}