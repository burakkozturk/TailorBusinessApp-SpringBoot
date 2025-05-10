package erdalguda.main.controller;

import erdalguda.main.model.Fabric;
import erdalguda.main.repository.FabricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fabrics")
@RequiredArgsConstructor
public class FabricController {

    private final FabricRepository fabricRepo;

    @PostMapping
    public ResponseEntity<Fabric> createFabric(@RequestBody Fabric fabric) {
        return ResponseEntity.ok(fabricRepo.save(fabric));
    }

    @GetMapping
    public List<Fabric> getAllFabrics() {
        return fabricRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fabric> getById(@PathVariable Long id) {
        return fabricRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        fabricRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
