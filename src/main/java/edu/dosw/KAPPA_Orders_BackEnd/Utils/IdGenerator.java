package edu.dosw.KAPPA_Orders_BackEnd.Utils;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class IdGenerator {

    public String generateOrderId() {
        return "ORD_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String generateItemId() {
        return "ITEM_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String generateTrackingCode() {
        return "TRK_" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}