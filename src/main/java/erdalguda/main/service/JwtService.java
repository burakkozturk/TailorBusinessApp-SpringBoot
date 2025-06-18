package erdalguda.main.service;

import erdalguda.main.model.Admin.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private static final String SECRET_KEY = "my-very-secret-key-12345678901234567890123456789012";
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username, Role role) {
        logger.info("'{}' kullanıcısı için '{}' rolüyle token oluşturuluyor", username, role);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 saat geçerli
                .addClaims(Map.of("role", role.name()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            String username = Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
            logger.debug("Token'dan username çıkarıldı: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Username çıkarılırken hata: {}", e.getMessage());
            return null;
        }
    }
    
    public Role extractRole(String token) {
        try {
            String roleName = Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().get("role", String.class);
            Role role = Role.valueOf(roleName);
            logger.debug("Token'dan rol çıkarıldı: {}", role);
            return role;
        } catch (Exception e) {
            logger.error("Rol çıkarılırken hata: {}", e.getMessage());
            return null;
        }
    }
    
    public List<SimpleGrantedAuthority> extractAuthorities(String token) {
        try {
            Role role = extractRole(token);
            if (role == null) {
                logger.warn("Token'dan rol çıkarılamadı, boş yetki listesi döndürülüyor");
                return Collections.emptyList();
            }
            
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            
            // Temel rol yetkisi
            SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority("ROLE_" + role.name());
            authorities.add(roleAuthority);
            
            // Rol hiyerarşisi - ADMIN en üst seviye, tüm yetkilere sahip
            if (role == Role.ADMIN) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USTA"));
                authorities.add(new SimpleGrantedAuthority("ROLE_MUHASEBECI"));
            }
            // USTA orta seviye, MUHASEBECI yetkilerine de sahip
            else if (role == Role.USTA) {
                authorities.add(new SimpleGrantedAuthority("ROLE_MUHASEBECI"));
            }
            // MUHASEBECI en alt seviye, sadece kendi yetkilerine sahip
            
            logger.info("Token'dan çıkarılan yetkiler: {}", authorities);
            return authorities;
        } catch (Exception e) {
            logger.error("Yetkiler çıkarılırken hata: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean isTokenValid(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            logger.error("Token geçerlilik kontrolü hatası: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            logger.error("Token süre kontrolü hatası: {}", e.getMessage());
            return true;
        }
    }
}
