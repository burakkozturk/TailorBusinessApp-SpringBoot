package erdalguda.main.repository;

import erdalguda.main.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);

    List<Order> findByStatusIgnoreCase(String status);

    @Modifying
    @Query("DELETE FROM Order o WHERE o.customer.id = :customerId")
    void deleteByCustomerId(@Param("customerId") Long customerId);

}
