package edu.dosw.KAPPA_Orders_BackEnd.Exception;

import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

public class Excepciones {

    public static abstract class OrderException extends RuntimeException {
        private final HttpStatus status;
        private final LocalDateTime timestamp;

        public OrderException(String message, HttpStatus status) {
            super(message);
            this.status = status;
            this.timestamp = LocalDateTime.now();
        }

        public HttpStatus getStatus() { return status; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }

    public static class OrderNotFoundException extends OrderException {
        public OrderNotFoundException(String orderId) {
            super("Orden no encontrada con ID: " + orderId, HttpStatus.NOT_FOUND);
        }
    }

    public static class OrderItemNotFoundException extends OrderException {
        public OrderItemNotFoundException(String itemId) {
            super("Item de orden no encontrado con ID: " + itemId, HttpStatus.NOT_FOUND);
        }
    }

    public static class MinimumAmountException extends OrderException {
        public MinimumAmountException(double minAmount) {
            super("El pedido no alcanza el monto mínimo de: $" + minAmount,
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static class CategoryMixingException extends OrderException {
        public CategoryMixingException() {
            super("No se pueden mezclar productos de diferentes categorías en un mismo pedido",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static class InvalidStatusTransitionException extends OrderException {
        public InvalidStatusTransitionException(String currentStatus, String newStatus) {
            super("No se puede cambiar del estado '" + currentStatus + "' a '" + newStatus + "'",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static class InvalidDataException extends OrderException {
        public InvalidDataException(String field, String reason) {
            super("Dato inválido en campo '" + field + "': " + reason,
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static class OperationNotAllowedException extends OrderException {
        public OperationNotAllowedException(String operation, String reason) {
            super("Operación no permitida '" + operation + "': " + reason,
                    HttpStatus.FORBIDDEN);
        }
    }

    public static class SystemException extends OrderException {
        public SystemException(String operation) {
            super("Error del sistema en operación: " + operation,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static void throwIfNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new InvalidDataException(fieldName, "No puede ser nulo");
        }
    }

    public static void throwIfEmpty(String str, String fieldName) {
        if (str == null || str.trim().isEmpty()) {
            throw new InvalidDataException(fieldName, "No puede estar vacío");
        }
    }

    public static void throwIfNegative(Number number, String fieldName) {
        if (number != null && number.doubleValue() < 0) {
            throw new InvalidDataException(fieldName, "No puede ser negativo");
        }
    }
}