package erdalguda.main.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Admin {
    @Id
    private String username;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role = Role.USTA; // Varsayılan olarak USTA
    
    public enum Role {
        ADMIN,        // Tam yetki - Tüm modüllere erişim
        USTA,         // Orta yetki - Müşteriler, Siparişler, Kumaşlar, Şablonlar
        MUHASEBECI    // Sınırlı yetki - Sadece Müşteriler ve Siparişler
    }
}