package erdalguda.main.repository;

import erdalguda.main.model.Order;
import erdalguda.main.model.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByStatus(OrderStatus status);

    void deleteByCustomerId(Long customerId);

    List<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status != 'CANCELLED' ORDER BY o.createdAt DESC")
    List<Order> findActiveOrdersByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT o FROM Order o WHERE o.status IN ('PREPARING', 'CUTTING', 'SEWING', 'FITTING') ORDER BY o.orderDate ASC")
    List<Order> findInProgressOrders();

    List<Order> findByEstimatedDeliveryDateBeforeAndStatusNot(LocalDate date, OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);

    @Query("SELECT o.productType, COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate GROUP BY o.productType")
    List<Object[]> getProductTypeStatistics(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Dashboard için yeni method'lar
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate")
    Long countOrdersByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate AND o.status = 'DELIVERED'")
    Double sumRevenueByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate AND o.status = 'DELIVERED'")
    Long countCompletedOrdersByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.estimatedDeliveryDate >= :startDate AND o.estimatedDeliveryDate <= :endDate AND o.status NOT IN ('DELIVERED', 'CANCELLED')")
    Long countDeliveriesForWeek(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> getOrderStatusDistribution();

    @Query(value = "SELECT EXTRACT(YEAR FROM order_date) as year, EXTRACT(MONTH FROM order_date) as month, " +
                   "COUNT(*) as order_count, " +
                   "COALESCE(SUM(CASE WHEN status = 'DELIVERED' THEN total_price ELSE 0 END), 0) as revenue " +
                   "FROM orders WHERE order_date >= :startDate " +
                   "GROUP BY EXTRACT(YEAR FROM order_date), EXTRACT(MONTH FROM order_date) " +
                   "ORDER BY year, month", nativeQuery = true)
    List<Object[]> getMonthlyTrend(@Param("startDate") LocalDate startDate);

    @Query("SELECT o.productType, COUNT(o) FROM Order o GROUP BY o.productType")
    List<Object[]> getProductTypeDistribution();

    @Query(value = "SELECT o.id, c.first_name, c.last_name, o.product_type, o.status, o.total_price, o.order_date " +
                   "FROM orders o JOIN customer c ON o.customer_id = c.id " +
                   "ORDER BY o.order_date DESC LIMIT 5", nativeQuery = true)
    List<Object[]> getRecentOrders();
}