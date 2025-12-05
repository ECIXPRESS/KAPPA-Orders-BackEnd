package edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private BigDecimal price;
    private Integer stock;

    public int getQuantity() {
        return stock;
    }
}

