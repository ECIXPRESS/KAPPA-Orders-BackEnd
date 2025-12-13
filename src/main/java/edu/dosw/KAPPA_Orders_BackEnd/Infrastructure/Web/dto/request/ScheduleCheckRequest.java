package edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleCheckRequest {
    private String pointOfSaleId;
    private LocalDateTime requestedTime;
    private String productCategory;
}