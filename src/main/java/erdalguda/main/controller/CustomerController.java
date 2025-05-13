package erdalguda.main.controller;

import erdalguda.main.model.Customer;
import erdalguda.main.repository.CustomerRepository;
import erdalguda.main.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;

    @GetMapping
    public List<Customer> getAll() {
        return customerRepo.findAll();
    }

    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        return customerRepo.save(customer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Long id) {
        return customerRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Customer> searchByName(@RequestParam String firstName) {
        return customerRepo.findByFirstNameContainingIgnoreCase(firstName);
    }

    // New advanced search endpoint with multiple parameters
    @GetMapping("/advanced-search")
    public List<Customer> advancedSearch(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phone) {

        if (firstName != null && !firstName.isEmpty()) {
            if (lastName != null && !lastName.isEmpty()) {
                return customerRepo.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName);
            }
            return customerRepo.findByFirstNameContainingIgnoreCase(firstName);
        } else if (lastName != null && !lastName.isEmpty()) {
            return customerRepo.findByLastNameContainingIgnoreCase(lastName);
        } else if (phone != null && !phone.isEmpty()) {
            return customerRepo.findByPhoneContaining(phone);
        }

        // Default to returning all customers if no parameters provided
        return customerRepo.findAll();
    }

    // Find customers by weight range


    // Find customers by height range


    // Get customers by BMI category

    @GetMapping("/page")
    public Page<Customer> getPagedCustomers(Pageable pageable) {
        return customerRepo.findAll(pageable);
    }

    // Patch update for partial updates
    @PatchMapping("/{id}")
    public ResponseEntity<Customer> partialUpdate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        Optional<Customer> optionalCustomer = customerRepo.findById(id);
        if (optionalCustomer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Customer customer = optionalCustomer.get();

        // Apply only the provided updates
        if (updates.containsKey("firstName")) {
            customer.setFirstName((String) updates.get("firstName"));
        }

        if (updates.containsKey("lastName")) {
            customer.setLastName((String) updates.get("lastName"));
        }

        if (updates.containsKey("address")) {
            customer.setAddress((String) updates.get("address"));
        }

        if (updates.containsKey("phone")) {
            customer.setPhone((String) updates.get("phone"));
        }

        if (updates.containsKey("height")) {
            customer.setHeight(Double.valueOf(updates.get("height").toString()));
        }

        if (updates.containsKey("weight")) {
            customer.setWeight(Double.valueOf(updates.get("weight").toString()));
        }

        if (updates.containsKey("ocrMeasurementText")) {
            customer.setOcrMeasurementText((String) updates.get("ocrMeasurementText"));
        }

        return ResponseEntity.ok(customerRepo.save(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer updated) {
        return customerRepo.findById(id)
                .map(customer -> {
                    customer.setFirstName(updated.getFirstName());
                    customer.setLastName(updated.getLastName());
                    customer.setAddress(updated.getAddress());
                    customer.setPhone(updated.getPhone());
                    customer.setHeight(updated.getHeight());
                    customer.setWeight(updated.getWeight());
                    customer.setOcrMeasurementText(updated.getOcrMeasurementText());
                    customerRepo.save(customer);
                    return ResponseEntity.ok(customer);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Bulk create customers
    @PostMapping("/bulk")
    public ResponseEntity<List<Customer>> bulkCreate(@RequestBody List<Customer> customers) {
        List<Customer> savedCustomers = customerRepo.saveAll(customers);
        return ResponseEntity.ok(savedCustomers);
    }


    // Count all customers
    @GetMapping("/count")
    public ResponseEntity<Long> countCustomers() {
        long count = customerRepo.count();
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Check if customer exists
        if (!customerRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Delete related orders first
        orderRepo.deleteByCustomerId(id);

        // Then delete the customer
        customerRepo.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // Alternative approach with safer error handling
    @DeleteMapping("/safe/{id}")
    @Transactional
    public ResponseEntity<?> safeDelete(@PathVariable Long id) {
        try {
            // Check if customer exists
            if (!customerRepo.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            // Delete related orders first
            orderRepo.deleteByCustomerId(id);

            // Then delete the customer
            customerRepo.deleteById(id);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Cannot delete customer. Error: " + e.getMessage());
        }
    }

    // Bulk delete customers
    @DeleteMapping("/bulk")
    @Transactional
    public ResponseEntity<?> bulkDelete(@RequestBody List<Long> ids) {
        try {
            for (Long id : ids) {
                if (customerRepo.existsById(id)) {
                    orderRepo.deleteByCustomerId(id);
                    customerRepo.deleteById(id);
                }
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Error during bulk deletion: " + e.getMessage());
        }
    }
}