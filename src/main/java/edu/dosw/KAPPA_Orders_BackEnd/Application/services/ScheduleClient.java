package edu.dosw.KAPPA_Orders_BackEnd.Application.services;

import edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.request.ReserveTimeSlotRequest;
import edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.request.ReleaseTimeSlotRequest;
import edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.request.ScheduleCheckRequest;
import edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.response.AvailabilityResponse;
import edu.dosw.KAPPA_Orders_BackEnd.infrastructure.Web.dto.response.TimeSlotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    public TimeSlotResponse reserveTimeSlot(String slotId, String orderId, String userId) {
        try {
            ReserveTimeSlotRequest request = new ReserveTimeSlotRequest(orderId, userId);

            return scheduleWebClient.post()
                    .uri("/api/schedule/time-slots/{slotId}/reserve", slotId)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(TimeSlotResponse.class)
                    .block();

        } catch (Exception e) {
            throw new RuntimeException("Error reservando slot: " + e.getMessage(), e);
        }
    }

    public TimeSlotResponse releaseTimeSlot(String slotId, String orderId) {
        try {
            ReleaseTimeSlotRequest request = new ReleaseTimeSlotRequest(orderId);

            return scheduleWebClient.post()
                    .uri("/api/schedule/time-slots/{slotId}/release", slotId)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(TimeSlotResponse.class)
                    .block();

        } catch (Exception e) {
            throw new RuntimeException("Error liberando slot: " + e.getMessage(), e);
        }
    }

    public List<TimeSlotResponse> getAvailableTimeSlots(String pointOfSaleId, LocalDate date) {
        try {
            return scheduleWebClient.get()
                    .uri("/api/schedule/time-slots/{pointOfSaleId}?date={date}",
                            pointOfSaleId, date.toString())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TimeSlotResponse>>() {})
                    .block();

        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo slots disponibles: " + e.getMessage(), e);
        }
    }
}