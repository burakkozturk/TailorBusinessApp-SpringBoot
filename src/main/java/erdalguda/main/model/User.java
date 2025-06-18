package erdalguda.main.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    private String fullName;
    
    private String email;
    
    private String phone;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private Boolean isActive = false; // Varsayılan olarak pasif, onay bekliyor
    
    @Column(nullable = false)
    private Boolean isApproved = false; // Admin onayı gerekli
    
    private String approvedBy; // Hangi admin onayladı
    
    private LocalDateTime approvedAt; // Ne zaman onaylandı
    
    // Kullanıcı rolü - kayıt sırasında seçilebilir
    @Enumerated(EnumType.STRING)
    private Role role = Role.MUHASEBECI; // Varsayılan rol
    
    public enum Role {
        ADMIN,        // Tam yetki - Tüm modüllere erişim
        USTA,         // Orta yetki - Müşteriler, Siparişler, Kumaşlar, Şablonlar
        MUHASEBECI    // Sınırlı yetki - Sadece müşteri ve sipariş görebilir
    }
} 