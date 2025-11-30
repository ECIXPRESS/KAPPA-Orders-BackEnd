package edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.dto.request;

import edu.dosw.KAPPA_Orders_BackEnd.Domain.Model.OrderType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class OrderItemRequest {

    @Schema(example = "orden-789", description = "ID de la orden a la que pertenece este ítem")
    public String orderId;

    @Schema(example = "producto-456", description = "Identificador del producto")
    public String productId;

    @Schema(example = "Hamburguesa", description = "Nombre del producto")
    public String productName;

    @Schema(example = "CAFETERIA", description = "Tipo de producto (si es de cafetería o papelería")
    public OrderType productType;

    @Schema(example = "1", description = "Cantidad del producto")
    public Integer quantity;

    @Schema(example = "6000.00", description = "Precio por unidad del producto")
    public BigDecimal unitPrice;

    @Schema(example = "Color negro, diseño ergonómico", description = "Detalles adicionales del producto")
    public String details;

    public OrderItemRequest() {}
}
