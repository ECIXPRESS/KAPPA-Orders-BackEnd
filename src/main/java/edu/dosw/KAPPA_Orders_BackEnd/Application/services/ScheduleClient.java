package edu.dosw.KAPPA_Orders_BackEnd.Application.services;

import edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.request.ScheduleCheckRequest;
import edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.response.AvailabilityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduleClient {

    private final WebClient scheduleWebClient;

    public boolean isStoreAvailable(String pointOfSaleId, LocalDateTime requestedTime) {
        try {
            ScheduleCheckRequest request = new ScheduleCheckRequest(
                    pointOfSaleId,
                    requestedTime,
                    null
            );

            AvailabilityResponse response = scheduleWebClient.post()
                    .uri("/api/schedule/availability")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(AvailabilityResponse.class)
                    .block();

            return Boolean.TRUE.equals(response.getAvailable());

        } catch (Exception e) {
            throw new RuntimeException("Error verificando disponibilidad de la tienda: " + e.getMessage(), e);
        }
    }
}