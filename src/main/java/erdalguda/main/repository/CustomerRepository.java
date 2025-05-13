package erdalguda.main.repository;

import erdalguda.main.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Existing method
    List<Customer> findByFirstNameContainingIgnoreCase(String firstName);

    // New methods for advanced search
    List<Customer> findByLastNameContainingIgnoreCase(String lastName);

    List<Customer> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
            String firstName, String lastName);

    List<Customer> findByPhoneContaining(String phone);



    // Complex queries
    @Query("SELECT c FROM Customer c WHERE " +
            "(:minBmi IS NULL OR (c.weight / (c.height * c.height / 10000)) >= :minBmi) AND " +
            "(:maxBmi IS NULL OR (c.weight / (c.height * c.height / 10000)) <= :maxBmi)")
    List<Customer> findCustomersByBmiRange(
            @Param("minBmi") Double minBmi,
            @Param("maxBmi") Double maxBmi);

    // Address-based queries
    List<Customer> findByAddressContainingIgnoreCase(String addressKeyword);

    // OCR text search
    List<Customer> findByOcrMeasurementTextContainingIgnoreCase(String keyword);
}