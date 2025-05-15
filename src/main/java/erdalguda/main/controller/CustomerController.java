package erdalguda.main.controller;

import erdalguda.main.dto.CustomerResponse;
import erdalguda.main.model.Customer;
import erdalguda.main.model.Measurement;
import erdalguda.main.repository.CustomerRepository;
import erdalguda.main.repository.OrderRepository;
import erdalguda.main.repository.MeasurementRepository;
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
    private final MeasurementRepository measurementRepo;

    @GetMapping
    public List<CustomerResponse> getAll() {
        return customerRepo.findAll().stream()
                .map(CustomerResponse::new)
                .toList();
    }

    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        return customerRepo.save(customer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
        return customerRepo.findById(id)
                .map(CustomerResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<CustomerResponse> searchByName(@RequestParam String firstName) {
        return customerRepo.findByFirstNameContainingIgnoreCase(firstName)
                .stream()
                .map(CustomerResponse::new)
                .toList();
    }

    @GetMapping("/advanced-search")
    public List<CustomerResponse> advancedSearch(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phone) {

        List<Customer> result;
        if (firstName != null && !firstName.isEmpty()) {
            if (lastName != null && !lastName.isEmpty()) {
                result = customerRepo.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName);
            } else {
                result = customerRepo.findByFirstNameContainingIgnoreCase(firstName);
            }
        } else if (lastName != null && !lastName.isEmpty()) {
            result = customerRepo.findByLastNameContainingIgnoreCase(lastName);
        } else if (phone != null && !phone.isEmpty()) {
            result = customerRepo.findByPhoneContaining(phone);
        } else {
            result = customerRepo.findAll();
        }

        return result.stream()
                .map(CustomerResponse::new)
                .toList();
    }

    @GetMapping("/page")
    public Page<Customer> getPagedCustomers(Pageable pageable) {
        return customerRepo.findAll(pageable);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Customer> partialUpdate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        Optional<Customer> optionalCustomer = customerRepo.findById(id);
        if (optionalCustomer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Customer customer = optionalCustomer.get();

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
                    return ResponseEntity.ok(customerRepo.save(customer));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countCustomers() {
        long count = customerRepo.count();
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!customerRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        orderRepo.deleteByCustomerId(id);
        customerRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/safe/{id}")
    @Transactional
    public ResponseEntity<?> safeDelete(@PathVariable Long id) {
        try {
            if (!customerRepo.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            orderRepo.deleteByCustomerId(id);
            customerRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Cannot delete customer. Error: " + e.getMessage());
        }
    }
}
