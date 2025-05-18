package erdalguda.main.config;

import erdalguda.main.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("SecurityConfig yapılandırması başlatılıyor - TÜM GÜVENLİK DEVRE DIŞI (TEST AMAÇLI)");
        
        http.cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> {
                try {
                    // TÜM ENDPOİNTLERE ERİŞİME İZİN VER - TEST AMAÇLI
                    auth.anyRequest().permitAll();
                    logger.info("TÜM GÜVENLİK KURALLARI DEVRE DIŞI - Herhangi bir yetkilendirme kontrolü olmayacak!");
                } catch (Exception e) {
                    logger.error("SecurityConfig yapılandırma hatası: {}", e.getMessage(), e);
                    throw e;
                }
            })
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            // JWT Filtresi de devre dışı - tamamen tüm güvenlik kontrolünü kaldırdık
            // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("SecurityFilterChain yapılandırması tamamlandı - TÜM GÜVENLİK DEVRE DIŞI");
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*")); // Tüm originlere izin ver
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*")); // Tüm headerlara izin ver
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setAllowCredentials(false); // Test için false
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        logger.info("CORS yapılandırması tamamlandı - Tüm origins için izin verildi");
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
