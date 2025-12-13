package edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReduceStockRequest {
    private int quantity;
}
