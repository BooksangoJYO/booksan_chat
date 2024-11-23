package io.booksan.booksan_chat.config.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    @Value("${booksan.front}")
    private String frontUrl;
    @Value("${booksan.users}")
    private String usersUrl;
    @Value("${booksan.board}")
    private String boardUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(csrf -> csrf
                .disable()
                )
                .cors(cors -> cors
                .configurationSource(corsConfigurationSource())
                )
                .authorizeHttpRequests(matchers -> matchers
                .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                .requestMatchers(
                        "/",
                        "/api/**",
                        "/js/**",
                        "/css/**",
                        "/images/**",
                        // WebSocket 관련 엔드포인트들
                        "/ws-stomp/**", // WebSocket 엔드포인트
                        "/pub/**", // Message Publishing 엔드포인트
                        "/sub/**", // Message Subscription 엔드포인트
                        "/topic/**", // STOMP destination prefix
                        "/queue/**" // User-specific messages
                ).permitAll()
                .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(frontUrl, boardUrl, usersUrl)); // 실제 운영환경에서는 구체적인 도메인 지정 필요
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
