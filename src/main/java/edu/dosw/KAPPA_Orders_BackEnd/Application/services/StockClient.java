package edu.dosw.KAPPA_Orders_BackEnd.Application.services;

import edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class StockClient {

    private final WebClient stockWebClient;

    public boolean hasStock(String productId, int quantity) {
        try {
            ProductResponse response = stockWebClient.get()
                    .uri("/api/products/" + productId + "/stock")
                    .retrieve()
                    .bodyToMono(ProductResponse.class)
                    .block();

            return response.getQuantity() >= quantity;

        } catch (Exception e) {
            throw new RuntimeException("Error verifying stock", e);
        }
    }
}
