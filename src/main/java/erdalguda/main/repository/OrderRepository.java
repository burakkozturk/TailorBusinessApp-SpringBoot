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
}