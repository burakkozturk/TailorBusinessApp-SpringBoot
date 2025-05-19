package erdalguda.main.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Bu controller sadece test amaçlıdır
 * Güvenlik sorunlarını çözdükten sonra kaldırılmalıdır
 */
@RestController
@RequestMapping("/test")
public class TestController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    
    @GetMapping("/public")
    public ResponseEntity<?> publicEndpoint() {
        logger.info("Public test endpointi çağrıldı");
        return ResponseEntity.ok(Map.of(
            "message", "Bu endpoint herkese açık",
            "status", "OK",
            "timestamp", System.currentTimeMillis()
        ));
    }
    
    @GetMapping("/customers")
    public ResponseEntity<?> customersEndpoint() {
        logger.info("Customers test endpointi çağrıldı");
        return ResponseEntity.ok(Map.of(
            "message", "Customers endpoint erişilebilir",
            "status", "OK",
            "timestamp", System.currentTimeMillis()
        ));
    }
    
    @GetMapping("/orders")
    public ResponseEntity<?> ordersEndpoint() {
        logger.info("Orders test endpointi çağrıldı");
        return ResponseEntity.ok(Map.of(
            "message", "Orders endpoint erişilebilir",
            "status", "OK",
            "timestamp", System.currentTimeMillis()
        ));
    }
    
    @GetMapping("/fabrics")
    public ResponseEntity<?> fabricsEndpoint() {
        logger.info("Fabrics test endpointi çağrıldı");
        return ResponseEntity.ok(Map.of(
            "message", "Fabrics endpoint erişilebilir",
            "status", "OK",
            "timestamp", System.currentTimeMillis()
        ));
    }
} 