// src/main/java/erdalguda/main/config/WebConfig.java

package erdalguda.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // Development için hepsine izin ver
                .allowedMethods("*") // Tüm HTTP methodları
                .allowedHeaders("*") // Tüm header'lar
                .allowCredentials(true)
                .maxAge(3600);
    }
}
