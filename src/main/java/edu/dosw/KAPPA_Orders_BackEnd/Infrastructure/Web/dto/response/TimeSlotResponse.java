package edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotResponse {
    private String id;
    private String pointOfSaleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer availableCapacity;
    private Boolean available;
}