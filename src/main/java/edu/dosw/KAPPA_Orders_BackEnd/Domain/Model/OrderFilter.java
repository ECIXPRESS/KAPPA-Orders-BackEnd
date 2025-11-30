package edu.dosw.KAPPA_Orders_BackEnd.Domain.Model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilter {
    private String userId;
    private OrderType orderType;
    private OrderStatus status;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String pickupLocation;

    /**
     * Validates that the date range is valid
     */
    public void validateDateRange() {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la fecha final");
        }
    }

    /**
     * Checks if any filter criteria is set
     */
    public boolean hasAnyFilter() {
        return userId != null
                || orderType != null
                || status != null
                || fromDate != null
                || toDate != null
                || pickupLocation != null;
    }
}