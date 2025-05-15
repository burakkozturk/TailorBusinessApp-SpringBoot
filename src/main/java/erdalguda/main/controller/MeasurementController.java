package erdalguda.main.controller;

import erdalguda.main.model.Customer;
import erdalguda.main.model.Measurement;
import erdalguda.main.repository.CustomerRepository;
import erdalguda.main.repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/measurements")
@RequiredArgsConstructor
public class MeasurementController {

    private final MeasurementRepository measurementRepo;
    private final CustomerRepository customerRepo;

    @PostMapping("/{customerId}")
    public ResponseEntity<?> createOrUpdateMeasurement(@PathVariable Long customerId,
                                                       @RequestBody Measurement request) {
        Optional<Customer> customerOpt = customerRepo.findById(customerId);
        if (customerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Customer not found");
        }

        request.setCustomer(customerOpt.get());
        Measurement saved = measurementRepo.save(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getByCustomer(@PathVariable Long customerId) {
        return measurementRepo.findByCustomerId(customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateMeasurement(@PathVariable Long customerId,
                                               @RequestBody Measurement updated) {
        Optional<Measurement> measurementOpt = measurementRepo.findByCustomerId(customerId);
        if (measurementOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Measurement existing = measurementOpt.get();

        existing.setChest(updated.getChest());
        existing.setWaist(updated.getWaist());
        existing.setHip(updated.getHip());
        existing.setShoulder(updated.getShoulder());
        existing.setNeck(updated.getNeck());
        existing.setLeftArm(updated.getLeftArm());
        existing.setRightArm(updated.getRightArm());
        existing.setLeftThigh(updated.getLeftThigh());
        existing.setRightThigh(updated.getRightThigh());
        existing.setLeftCalf(updated.getLeftCalf());
        existing.setRightCalf(updated.getRightCalf());
        existing.setElbowLength(updated.getElbowLength());

        return ResponseEntity.ok(measurementRepo.save(existing));
    }


    @PatchMapping("/{customerId}")
    public ResponseEntity<?> partialUpdate(@PathVariable Long customerId,
                                           @RequestBody Map<String, Object> updates) {
        Optional<Measurement> optional = measurementRepo.findByCustomerId(customerId);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Measurement m = optional.get();

        updates.forEach((key, value) -> {
            try {
                Field field = Measurement.class.getDeclaredField(key);
                field.setAccessible(true);
                field.set(m, Double.valueOf(value.toString()));
            } catch (Exception e) {
                // log error if needed
            }
        });

        return ResponseEntity.ok(measurementRepo.save(m));
    }

}
