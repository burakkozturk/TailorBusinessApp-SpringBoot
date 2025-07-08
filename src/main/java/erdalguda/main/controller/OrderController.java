package erdalguda.main.controller;

import erdalguda.main.dto.OrderResponse;
import erdalguda.main.model.Order;
import erdalguda.main.model.Order.OrderStatus;
import erdalguda.main.model.Customer;
import erdalguda.main.repository.OrderRepository;
import erdalguda.main.repository.CustomerRepository;
import erdalguda.main.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final EmailService emailService;

    @Autowired
    public OrderController(OrderRepository orderRepo, CustomerRepository customerRepo, EmailService emailService) {
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        // Müşteri kontrolü
        if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            return ResponseEntity.badRequest().body("Müşteri bilgisi gerekli");
        }

        Optional<Customer> customerOpt = customerRepo.findById(order.getCustomer().getId());
        if (customerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Müşteri bulunamadı");
        }

        order.setCustomer(customerOpt.get());
        Order saved = orderRepo.save(order);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<Order> orders = orderRepo.findAll();
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        return orderRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderRepo.findByCustomerId(customerId));
    }

    @GetMapping("/active/by-customer/{customerId}")
    public ResponseEntity<List<Order>> getActiveOrdersByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderRepo.findActiveOrdersByCustomer(customerId));
    }

    @GetMapping("/in-progress")
    public ResponseEntity<List<Order>> getInProgressOrders() {
        return ResponseEntity.ok(orderRepo.findInProgressOrders());
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderRepo.findByStatus(status));
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<Order>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(orderRepo.findByOrderDateBetween(startDate, endDate));
    }

    @GetMapping("/statistics/by-product-type")
    public ResponseEntity<List<Object[]>> getProductTypeStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(orderRepo.getProductTypeStatistics(startDate, endDate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Order updated) {
        Optional<Order> orderOpt = orderRepo.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order existing = orderOpt.get();
        OrderStatus oldStatus = existing.getStatus();

        // Temel bilgileri güncelle
        existing.setProductType(updated.getProductType());
        existing.setFitType(updated.getFitType());
        existing.setStatus(updated.getStatus());
        existing.setNotes(updated.getNotes());
        existing.setEstimatedDeliveryDate(updated.getEstimatedDeliveryDate());
        existing.setDeliveryDate(updated.getDeliveryDate());
        existing.setTotalPrice(updated.getTotalPrice());

        Order saved = orderRepo.save(existing);
        
        // Durum değişmişse email gönder
        if (oldStatus != updated.getStatus()) {
            emailService.sendOrderStatusUpdateEmail(saved);
        }
        
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, OrderStatus> statusUpdate) {

        OrderStatus newStatus = statusUpdate.get("status");
        if (newStatus == null) {
            return ResponseEntity.badRequest().body("Yeni durum belirtilmedi");
        }

        Optional<Order> orderOpt = orderRepo.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);

        // Eğer durum DELIVERED ise, teslim tarihini güncelle
        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveryDate(LocalDate.now());
        }

        Order saved = orderRepo.save(order);
        
        // Durum değişmişse email gönder
        if (oldStatus != newStatus) {
            emailService.sendOrderStatusUpdateEmail(saved);
        }
        
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        if (!orderRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        orderRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}