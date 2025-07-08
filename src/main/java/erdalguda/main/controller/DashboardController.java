package erdalguda.main.controller;

import erdalguda.main.dto.DashboardStatsDto;
import erdalguda.main.dto.OrderStatusDistributionDto;
import erdalguda.main.dto.MonthlyTrendDto;
import erdalguda.main.dto.ProductDistributionDto;
import erdalguda.main.repository.CustomerRepository;
import erdalguda.main.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;

    @Autowired
    public DashboardController(CustomerRepository customerRepo, OrderRepository orderRepo) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate thirtyDaysAgo = today.minusDays(30);
            LocalDate oneMonthAgo = today.minusMonths(1);
            LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            
            // 1. Toplam müşteri sayısı (son 30 gün içinde eklenen müşterileri hesaplamak için customer creation date gerekir)
            Long totalCustomers = customerRepo.count();
            
            // 2. Son 30 günde eklenen sipariş sayısı
            Long ordersLast30Days = orderRepo.countOrdersByDateRange(thirtyDaysAgo, today);
            
            // 3. Son 1 aydaki ciro (sadece DELIVERED siparişlerden)
            Double revenueLastMonth = orderRepo.sumRevenueByDateRange(oneMonthAgo, today);
            if (revenueLastMonth == null) revenueLastMonth = 0.0;
            
            // 4. Prova bekleyen sipariş sayısı
            Long fittingOrders = orderRepo.countByStatus(erdalguda.main.model.Order.OrderStatus.FITTING);
            
            // 5. Son 1 ayda tamamlanan sipariş sayısı
            Long completedLastMonth = orderRepo.countCompletedOrdersByDateRange(oneMonthAgo, today);
            
            // 6. Bu hafta teslim edilecek sipariş sayısı
            Long deliveriesThisWeek = orderRepo.countDeliveriesForWeek(startOfWeek, endOfWeek);
            
            // Büyüme hesaplamaları
            LocalDate sixtyDaysAgo = today.minusDays(60);
            Long ordersPrevious30Days = orderRepo.countOrdersByDateRange(sixtyDaysAgo, thirtyDaysAgo);
            Double ordersGrowth = 0.0;
            if (ordersPrevious30Days > 0) {
                ordersGrowth = ((ordersLast30Days - ordersPrevious30Days) * 100.0) / ordersPrevious30Days;
            }
            
            Double revenuePrevious30Days = orderRepo.sumRevenueByDateRange(sixtyDaysAgo, thirtyDaysAgo);
            if (revenuePrevious30Days == null) revenuePrevious30Days = 0.0;
            Double revenueGrowth = 0.0;
            if (revenuePrevious30Days > 0) {
                revenueGrowth = ((revenueLastMonth - revenuePrevious30Days) * 100.0) / revenuePrevious30Days;
            }
            
            DashboardStatsDto stats = new DashboardStatsDto();
            stats.setTotalCustomers(totalCustomers);
            stats.setOrdersLast30Days(ordersLast30Days);
            stats.setRevenueLastMonth(revenueLastMonth);
            stats.setFittingOrders(fittingOrders);
            stats.setCompletedLastMonth(completedLastMonth);
            stats.setDeliveriesThisWeek(deliveriesThisWeek);
            stats.setOrdersGrowth(ordersGrowth);
            stats.setRevenueGrowth(revenueGrowth);
            stats.setCustomersGrowth(8.5); // Müşteri growth için şimdilik varsayılan
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/order-status-distribution")
    public ResponseEntity<List<OrderStatusDistributionDto>> getOrderStatusDistribution() {
        try {
            List<Object[]> results = orderRepo.getOrderStatusDistribution();
            List<OrderStatusDistributionDto> distribution = results.stream()
                .map(result -> new OrderStatusDistributionDto(
                    result[0].toString(), 
                    ((Number) result[1]).longValue()
                ))
                .toList();
            return ResponseEntity.ok(distribution);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/monthly-trend")
    public ResponseEntity<List<MonthlyTrendDto>> getMonthlyTrend() {
        try {
            LocalDate eightMonthsAgo = LocalDate.now().minusMonths(8);
            List<Object[]> results = orderRepo.getMonthlyTrend(eightMonthsAgo);
            List<MonthlyTrendDto> trend = results.stream()
                .map(result -> new MonthlyTrendDto(
                    ((Number) result[0]).intValue(), // year
                    ((Number) result[1]).intValue(), // month
                    ((Number) result[2]).longValue(), // order count
                    result[3] != null ? ((Number) result[3]).doubleValue() : 0.0 // revenue
                ))
                .toList();
            return ResponseEntity.ok(trend);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/product-distribution")
    public ResponseEntity<List<ProductDistributionDto>> getProductDistribution() {
        try {
            List<Object[]> results = orderRepo.getProductTypeDistribution();
            List<ProductDistributionDto> distribution = results.stream()
                .map(result -> new ProductDistributionDto(
                    result[0] != null ? result[0].toString() : "Diğer",
                    ((Number) result[1]).longValue()
                ))
                .toList();
            return ResponseEntity.ok(distribution);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/recent-orders")
    public ResponseEntity<List<Map<String, Object>>> getRecentOrders() {
        try {
            List<Object[]> results = orderRepo.getRecentOrders();
            List<Map<String, Object>> recentOrders = results.stream()
                .map(result -> {
                    String firstName = result[1] != null ? result[1].toString() : "";
                    String lastName = result[2] != null ? result[2].toString() : "";
                    String customerName = (firstName + " " + lastName).trim();
                    
                    return Map.of(
                        "id", result[0],
                        "customerName", customerName.isEmpty() ? "Bilinmeyen Müşteri" : customerName,
                        "productType", result[3] != null ? result[3] : "Belirtilmemiş",
                        "status", result[4] != null ? result[4] : "UNKNOWN",
                        "totalPrice", result[5] != null ? result[5] : 0,
                        "orderDate", result[6] != null ? result[6] : ""
                    );
                })
                .toList();
            return ResponseEntity.ok(recentOrders);
        } catch (Exception e) {
            e.printStackTrace(); // Debug için hata yazdırıyoruz
            return ResponseEntity.internalServerError().build();
        }
    }
} 