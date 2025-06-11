package erdalguda.main.controller;

import erdalguda.main.model.Admin;
import erdalguda.main.model.Admin.Role;
import erdalguda.main.repository.AdminRepository;
import erdalguda.main.service.AdminService;
import erdalguda.main.service.JwtService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private AdminService adminService;

    /**
     * Uygulama başlangıcında varsayılan admin hesabı oluşturur
     * Bu metod aynı zamanda mevcut admin hesaplarının rollerini de kontrol eder ve düzeltir
     */
    @PostConstruct
    public void createDefaultAdmin() {
        // Varsayılan admin hesabı oluştur
        if (!adminRepo.existsById("admin")) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole(Role.ADMIN); // Ana admin kullanıcısı
            adminRepo.save(admin);
            logger.info("Varsayılan admin kullanıcısı oluşturuldu: {}", admin.getUsername());
        } else {
            // Eğer admin varsa, rolünün ADMIN olduğundan emin ol
            Optional<Admin> existingAdmin = adminRepo.findById("admin");
            if (existingAdmin.isPresent()) {
                Admin admin = existingAdmin.get();
                if (admin.getRole() != Role.ADMIN) {
                    admin.setRole(Role.ADMIN);
                    adminRepo.save(admin);
                    logger.info("Varsayılan admin kullanıcısının rolü ADMIN olarak güncellendi");
                } else {
                    logger.info("Varsayılan admin kullanıcısı zaten ADMIN rolüne sahip");
                }
            }
        }
        
        // Tüm kullanıcıları ve rollerini logla
        List<Admin> allUsers = adminRepo.findAll();
        logger.info("Sistemdeki tüm kullanıcılar ve rolleri:");
        for (Admin user : allUsers) {
            logger.info("Kullanıcı: {}, Rol: {}", user.getUsername(), user.getRole());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logger.info("Giriş denemesi: {}", request.getUsername());
        
        Optional<Admin> adminOpt = adminRepo.findById(request.getUsername());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            logger.info("Kullanıcı bulundu: {}, Rol: {}", admin.getUsername(), admin.getRole());
            
            if (passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                // Rolü kontrol edip logla
                logger.info("Şifre doğrulandı. Token oluşturuluyor, Rol: {}", admin.getRole());
                
                String token = jwtService.generateToken(admin.getUsername(), admin.getRole());
                
                // Oluşturulan tokeni test amacıyla logda görelim
                String username = jwtService.extractUsername(token);
                Role extractedRole = jwtService.extractRole(token);
                List<String> authorities = jwtService.extractAuthorities(token)
                    .stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList());
                
                logger.info("Token oluşturuldu - Username: {}, Rol: {}, Yetkiler: {}", 
                         username, extractedRole, authorities);
                
                // Rol bilgisini de döndürelim
                LoginResponse response = new LoginResponse();
                response.setToken(token);
                response.setRole(admin.getRole().name());
                response.setUsername(admin.getUsername());
                
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Kullanıcı {} için şifre eşleşmedi", request.getUsername());
            }
        } else {
            logger.warn("Kullanıcı bulunamadı: {}", request.getUsername());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Geçersiz kullanıcı adı veya şifre");
    }
    
    /**
     * Şifre değiştirme endpoint'i
     * Mevcut şifre ve yeni şifre gereklidir
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        logger.info("Şifre değiştirme isteği: {}", request.getUsername());
        
        // Mevcut şifreyi doğrula
        if (!adminService.verifyCurrentPassword(request.getUsername(), request.getCurrentPassword())) {
            logger.warn("Kullanıcı {} için geçersiz mevcut şifre", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mevcut şifre hatalı");
        }
        
        // Şifreyi değiştir
        if (adminService.changePassword(request.getUsername(), request.getNewPassword())) {
            logger.info("Kullanıcı {} için şifre başarıyla değiştirildi", request.getUsername());
            return ResponseEntity.ok(Map.of("message", "Şifre başarıyla değiştirildi"));
        } else {
            logger.error("Kullanıcı {} için şifre değiştirme başarısız", request.getUsername());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Şifre değiştirme işlemi başarısız");
        }
    }
    
    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuth() {
        logger.info("Kimlik doğrulama test endpointi çağrıldı");
        return ResponseEntity.ok(
            Map.of("message", "Kimlik doğrulama başarılı", 
                  "timestamp", System.currentTimeMillis()));
    }
    
    @GetMapping("/admin/test")
    public ResponseEntity<?> testAdminAuth() {
        logger.info("Admin kimlik doğrulama test endpointi çağrıldı");
        return ResponseEntity.ok(
            Map.of("message", "Admin kimlik doğrulama başarılı", 
                  "timestamp", System.currentTimeMillis()));
    }
    
    // Sadece ADMIN rolüne sahip kullanıcılar tarafından erişilebilir
    @PostMapping("/admin/create-manager")
    public ResponseEntity<?> createManager(@RequestBody CreateManagerRequest request) {
        // Kullanıcı adı kontrolü
        if (adminRepo.existsById(request.getUsername())) {
            return ResponseEntity.badRequest().body("Bu kullanıcı adı zaten kullanılıyor");
        }
        
        Admin manager = new Admin();
        manager.setUsername(request.getUsername());
        manager.setPassword(passwordEncoder.encode(request.getPassword()));
        manager.setRole(Role.MANAGER); // Varsayılan olarak MANAGER rolü
        
        adminRepo.save(manager);
        logger.info("Yeni manager oluşturuldu: {}", manager.getUsername());
        return ResponseEntity.ok(new AdminResponse(manager));
    }
    
    // Yöneticileri listele (sadece ADMIN erişebilir)
    @GetMapping("/admin/managers")
    public ResponseEntity<List<AdminResponse>> listManagers() {
        List<Admin> managers = adminRepo.findByRole(Role.MANAGER);
        List<AdminResponse> response = managers.stream()
                .map(AdminResponse::new)
                .collect(Collectors.toList());
        logger.info("{} adet manager listelendi", managers.size());
        return ResponseEntity.ok(response);
    }
    
    // Yönetici silme (sadece ADMIN erişebilir)
    @DeleteMapping("/admin/managers/{username}")
    public ResponseEntity<?> deleteManager(@PathVariable String username) {
        Optional<Admin> managerOpt = adminRepo.findById(username);
        
        if (managerOpt.isEmpty()) {
            logger.warn("Silinecek manager bulunamadı: {}", username);
            return ResponseEntity.notFound().build();
        }
        
        Admin manager = managerOpt.get();
        if (manager.getRole() != Role.MANAGER) {
            logger.warn("Sadece MANAGER rolündeki hesaplar silinebilir: {}", username);
            return ResponseEntity.badRequest().body("Sadece yönetici hesapları silinebilir");
        }
        
        adminRepo.delete(manager);
        logger.info("Manager silindi: {}", username);
        return ResponseEntity.ok().build();
    }

    @Getter @Setter
    static class LoginRequest {
        private String username;
        private String password;
    }
    
    @Getter @Setter
    static class CreateManagerRequest {
        private String username;
        private String password;
    }
    
    @Getter @Setter
    static class ChangePasswordRequest {
        private String username;
        private String currentPassword;
        private String newPassword;
    }
    
    @Getter @Setter
    static class LoginResponse {
        private String token;
        private String role;
        private String username;
    }
    
    @Getter @Setter
    static class AdminResponse {
        private String username;
        private String role;
        
        public AdminResponse(Admin admin) {
            this.username = admin.getUsername();
            this.role = admin.getRole().name();
        }
    }
}
