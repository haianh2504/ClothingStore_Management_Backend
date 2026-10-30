package org.example.spring_backend_clothingstore.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AdminAuthenticationEntryPoint adminAuthenticationEntryPoint;
    private final AdminAccessDeniedHandler adminAccessDeniedHandler;

    // khai bao Bean PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // Cấu hình bean AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration  authenticationConfiguration) throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Apply XOR masking to CSRF tokens exposed through request attributes.
     * This protects against BREACH-style compression side-channel attacks.
     */
    @Bean
    public CsrfTokenRequestHandler csrfTokenRequestHandler() {
        return new CsrfTokenRequestAttributeHandler();
    }

    // Cấu hình chuỗi bộ lọc ( Security Filter Chaining )
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CsrfTokenRequestHandler csrfTokenRequestHandler
    ) throws Exception {
        http
                .csrf(csrf -> csrf
                        // Browser đọc token từ XSRF-TOKEN và gửi lại qua X-XSRF-TOKEN.
                        // CSRF vẫn được áp dụng cho các guest endpoint dùng cookie GUEST_ID:
                        // thêm cart, cập nhật/xóa cart item và đặt order.
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(csrfTokenRequestHandler)
                )
                .sessionManagement(session -> session
                        // Đổi session ID sau khi authentication thành công,
                        // giữ nguyên session và tránh session fixation.
                        .sessionFixation(sessionFixation -> sessionFixation.changeSessionId())
                )
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(HttpMethod.POST, "/api/v1/admin/sessions").permitAll() // login
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/sessions/current").permitAll() // logout
                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // authorized
                                .anyRequest().permitAll()
                )
                // Thêm bộ xử lý exception riêng ở SecurityFilterChain về session ( đặc biệt là UC08.1 )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(adminAuthenticationEntryPoint)
                        .accessDeniedHandler(adminAccessDeniedHandler)
                );
        return http.build(); // tạo SecurityFilterChain cho FilterChainProxy quản lý
    }

}
