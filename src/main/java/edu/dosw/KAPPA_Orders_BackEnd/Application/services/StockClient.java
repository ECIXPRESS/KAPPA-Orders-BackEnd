package edu.dosw.KAPPA_Orders_BackEnd.Application.services;

import edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.dto.request.ReduceStockRequest;
import edu.dosw.KAPPA_Orders_BackEnd.Infrastructure.Web.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockClient {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(StockClient.class);

    @Value("${gateway.url:http://localhost:8081/api}")
    private String gatewayBaseUrl;

    public boolean hasStock(String productId, int quantity) {
        try {
            // RUTA CORRECTA: /api/products/{id}
            String url = UriComponentsBuilder.fromHttpUrl(gatewayBaseUrl)
                    .path("/products/{id}")
                    .buildAndExpand(productId)
                    .toUriString();

            logger.info("Checking stock via gateway: {}", url);

            ResponseEntity<ProductResponse> response = restTemplate.getForEntity(url, ProductResponse.class);
            ProductResponse productResponse = response.getBody();

            if (productResponse == null) {
                logger.warn("Product not found with ID: {}", productId);
                return false;
            }

            // Verificar stock desde la respuesta del producto
            boolean hasStock = productResponse.getStock() >= quantity;  // Cambia getQuantity() por getStock()
            logger.info("Stock check result for product {}: available={}, required={}, hasStock={}",
                    productId, productResponse.getStock(), quantity, hasStock);

            return hasStock;

        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Product not found with ID: {}", productId);
            return false;
        } catch (ResourceAccessException e) {
            logger.error("Cannot connect to gateway: {} - URL: {}", e.getMessage(), gatewayBaseUrl);
            throw new RuntimeException("Cannot connect to API Gateway: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error checking stock via gateway: {}", e.getMessage());
            throw new RuntimeException("Error checking stock via gateway: " + e.getMessage(), e);
        }
    }

    public void reduceStock(String productId, int quantity) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(gatewayBaseUrl)
                    .path("/products/{id}/stock/decrease")
                    .buildAndExpand(productId)
                    .toUriString();

            logger.info("Reducing stock via gateway: {} - quantity: {}", url, quantity);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", quantity);
            requestBody.put("operationType", "SALE");
            requestBody.put("reason", "Venta de pedido");

            restTemplate.postForEntity(url, requestBody, Void.class);
            logger.info("Stock reduced successfully for product {}: amount={}", productId, quantity);

        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Product not found when reducing stock, ID: {}", productId);
            throw new RuntimeException("Product not found: " + productId, e);
        } catch (HttpClientErrorException.BadRequest e) {
            logger.error("Bad request when reducing stock for product {}: {}", productId, e.getMessage());

            // Para debug: mostrar el error completo
            if (e.getResponseBodyAsString() != null) {
                logger.error("Error response body: {}", e.getResponseBodyAsString());
            }

            throw new RuntimeException("Invalid stock reduction request: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            logger.error("Cannot connect to gateway: {} - URL: {}", e.getMessage(), gatewayBaseUrl);
            throw new RuntimeException("Cannot connect to API Gateway: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error reducing stock via gateway: {}", e.getMessage());
            throw new RuntimeException("Error reducing stock via gateway: " + e.getMessage(), e);
        }
    }
}