package org.example.spring_backend_clothingstore.config;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.spring_backend_clothingstore.api_response_schema.ApiResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;

// GlobalExceptionHandler không xử lý được trường hợp exceptions ở Security Filter Chain
// This bean is created to make the API response for session cookie is invalid,missing,expired when /api/v1/admin/sessions/current occurs
@Component
public class AdminAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public AdminAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request, // handled in servlet / filter  <>  in controller
            HttpServletResponse response,
            AuthenticationException authenticationException
    ) throws IOException, ServletException {
        ApiResponse<Void> body = ApiResponse.from(
                HttpServletResponse.SC_UNAUTHORIZED,
                "ADMIN_SESSION_UNAUTHORIZED",
                "Admin session is missing, invalid, or expired",
                OffsetDateTime.now(),
                null
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
