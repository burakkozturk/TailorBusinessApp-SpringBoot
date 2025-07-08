package erdalguda.main.security;

import erdalguda.main.repository.AdminRepository;
import erdalguda.main.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AdminRepository adminRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    public JwtAuthFilter(JwtService jwtService, AdminRepository adminRepository) {
        this.jwtService = jwtService;
        this.adminRepository = adminRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7);
        try {
            username = jwtService.extractUsername(token);
            logger.info("Token çözümlendi, kullanıcı: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var adminOpt = adminRepository.findById(username);
                if (adminOpt.isPresent() && jwtService.isTokenValid(token, username)) {
                    // Rolü ve yetkileri tokendan çıkar
                    var authorities = jwtService.extractAuthorities(token);
                    
                    // Rolleri logla
                    logger.info("Kullanıcı {} için roller:", username);
                    for (GrantedAuthority auth : authorities) {
                        logger.info("Rol: {}", auth.getAuthority());
                    }
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username, null, authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    logger.info("Kimlik doğrulama başarılı: {}", username);
                } else {
                    logger.warn("Token geçersiz veya kullanıcı bulunamadı: {}", username);
                }
            }
        } catch (Exception e) {
            logger.error("JWT çözümleme hatası: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
