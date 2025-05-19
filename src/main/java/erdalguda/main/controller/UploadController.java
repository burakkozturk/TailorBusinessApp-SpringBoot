package erdalguda.main.controller;

import erdalguda.main.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    private final S3Service s3Service;

    public UploadController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam(value = "file", required = false) MultipartFile file,
                                         @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            MultipartFile fileToUpload = file != null ? file : image;
            if (fileToUpload == null) {
                return ResponseEntity.badRequest().body("Dosya bulunamadı. 'file' veya 'image' parametresi gerekli.");
            }
            
            String url = s3Service.uploadFile(fileToUpload);
            return ResponseEntity.ok().body(java.util.Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Yükleme hatası: " + e.getMessage());
        }
    }
} 