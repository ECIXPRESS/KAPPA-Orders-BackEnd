package edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityResponse {
    private Boolean available;
    private String pointOfSaleId;
    private LocalDateTime requestedTime;
    private String reason;
    private String categoryMessage;
    private List<String> availableTimeSlots;
}