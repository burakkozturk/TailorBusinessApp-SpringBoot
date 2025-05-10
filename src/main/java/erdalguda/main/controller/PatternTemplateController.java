package erdalguda.main.controller;

import erdalguda.main.model.PatternTemplate;
import erdalguda.main.repository.PatternTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class PatternTemplateController {

    private final PatternTemplateRepository templateRepo;

    @PostMapping
    public ResponseEntity<PatternTemplate> createTemplate(@RequestBody PatternTemplate template) {
        return ResponseEntity.ok(templateRepo.save(template));
    }

    @GetMapping
    public List<PatternTemplate> getAllTemplates() {
        return templateRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatternTemplate> getTemplateById(@PathVariable Long id) {
        return templateRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<?> getPdf(@PathVariable Long id) {
        return templateRepo.findById(id)
                .map(t -> ResponseEntity.ok(t.getFileUrlPdf()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/dxf")
    public ResponseEntity<?> getDxf(@PathVariable Long id) {
        return templateRepo.findById(id)
                .map(t -> ResponseEntity.ok(t.getFileUrlDxf()))
                .orElse(ResponseEntity.notFound().build());
    }
}
