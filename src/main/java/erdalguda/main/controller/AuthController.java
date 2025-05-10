package erdalguda.main.controller;

import erdalguda.main.model.Admin;
import erdalguda.main.repository.AdminRepository;
import erdalguda.main.service.JwtService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostConstruct
    public void createDefaultAdmin() {
        if (!adminRepo.existsById("admin")) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            adminRepo.save(admin);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Admin> adminOpt = adminRepo.findById(request.getUsername());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                String token = jwtService.generateToken(admin.getUsername());
                return ResponseEntity.ok(Collections.singletonMap("token", token));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }


    @Getter @Setter
    static class LoginRequest {
        private String username;
        private String password;
    }
}
