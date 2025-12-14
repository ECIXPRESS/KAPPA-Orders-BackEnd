package edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseTimeSlotRequest {
    private String orderId;
}