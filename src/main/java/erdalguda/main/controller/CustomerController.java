package erdalguda.main.controller;

import erdalguda.main.model.Customer;
import erdalguda.main.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepo;

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

    @GetMapping("/page")
    public Page<Customer> getPagedCustomers(Pageable pageable) {
        return customerRepo.findAll(pageable);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
