package erdalguda.main.controller;

import erdalguda.main.dto.FabricSummary;
import erdalguda.main.dto.OrderRequest;
import erdalguda.main.dto.OrderResponse;
import erdalguda.main.dto.UpdateOrderRequest;
import erdalguda.main.model.Customer;
import erdalguda.main.model.Fabric;
import erdalguda.main.model.Order;
import erdalguda.main.model.PatternTemplate;
import erdalguda.main.repository.CustomerRepository;
import erdalguda.main.repository.FabricRepository;
import erdalguda.main.repository.OrderRepository;
import erdalguda.main.repository.PatternTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final PatternTemplateRepository patternTemplateRepo;
    private final FabricRepository fabricRepo;

    // OCR'dan ölçü çekme metodu
    private Double extractValueFromText(String text, String keyword) {
        try {
            Pattern pattern = Pattern.compile(keyword + "\\D*(\\d+(\\.\\d+)?)");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return Double.parseDouble(matcher.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Sipariş oluşturma
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        Optional<Customer> customerOpt = customerRepo.findById(request.getCustomerId());
        if (customerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Customer not found");
        }

        Customer customer = customerOpt.get();

        Order order = new Order();
        order.setCustomer(customer);
        order.setProductType(request.getProductType());
        order.setFitType(request.getFitType());
        order.setOrderDate(LocalDate.now());
        order.setStatus(request.getStatus() != null ? request.getStatus() : "Hazırlanıyor");
        order.setSelectedFabricId(request.getSelectedFabricId());
        order.setNotes(request.getNotes());
        orderRepo.save(order);

        // Ölçüleri OCR metninden çıkar
        Double chest = extractValueFromText(customer.getOcrMeasurementText(), "Göğüs");
        Double waist = extractValueFromText(customer.getOcrMeasurementText(), "Bel");

        // Şablon eşleşmesi
        List<PatternTemplate> matched = patternTemplateRepo
                .findByProductTypeAndFitTypeAndMinChestLessThanEqualAndMaxChestGreaterThanEqualAndMinWaistLessThanEqualAndMaxWaistGreaterThanEqual(
                        request.getProductType(), request.getFitType(),
                        chest, chest,
                        waist, waist
                );

        String suggested = matched.isEmpty() ? "Uygun şablon bulunamadı" : matched.get(0).getName();

        // Kumaş bilgisi
        FabricSummary fabricSummary = null;
        if (request.getSelectedFabricId() != null) {
            Optional<Fabric> fabricOpt = fabricRepo.findById(request.getSelectedFabricId());
            if (fabricOpt.isPresent()) {
                Fabric fabric = fabricOpt.get();
                fabricSummary = new FabricSummary(
                        fabric.getId(),
                        fabric.getName(),
                        fabric.getTexture(),
                        fabric.getDescription(),
                        fabric.getImageUrl()
                );
            }
        }

        // Yanıt oluştur
        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getProductType(),
                order.getFitType(),
                order.getOrderDate(),
                customer.getId(),
                customer.getFirstName() + " " + customer.getLastName(),
                customer.getHeight(),
                customer.getWeight(),
                customer.getOcrMeasurementText(),
                suggested,
                order.getStatus(),
                order.getSelectedFabricId(),
                order.getNotes(),
                fabricSummary
        );

        return ResponseEntity.ok(response);
    }

    // Müşteriye ait tüm siparişler
    @GetMapping("/by-customer/{customerId}")
    public List<Order> getOrdersByCustomer(@PathVariable Long customerId) {
        return orderRepo.findByCustomerId(customerId);
    }

    // Tüm siparişler
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    // Sipariş silme
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody UpdateOrderRequest request) {
        Optional<Order> orderOpt = orderRepo.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();

        if (request.getStatus() != null) order.setStatus(request.getStatus());
        if (request.getNotes() != null) order.setNotes(request.getNotes());
        if (request.getSelectedFabricId() != null) order.setSelectedFabricId(request.getSelectedFabricId());

        orderRepo.save(order);
        return ResponseEntity.ok("Order updated");
    }

    @GetMapping("/status/{status}")
    public List<Order> getOrdersByStatus(@PathVariable String status) {
        return orderRepo.findByStatusIgnoreCase(status);
    }

}
