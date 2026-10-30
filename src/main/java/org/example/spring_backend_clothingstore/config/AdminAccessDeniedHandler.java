package org.example.spring_backend_clothingstore.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.spring_backend_clothingstore.exception.response.ErrorResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Chuẩn hóa response 403 từ Security Filter Chain cho role và CSRF.
 */
@Component
public class AdminAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public AdminAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** Trả lỗi CSRF hoặc thiếu quyền theo đúng envelope API. */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception
    ) throws IOException, ServletException {
        boolean csrfFailure = exception instanceof CsrfException;
        int status = HttpServletResponse.SC_FORBIDDEN;
        String code = csrfFailure ? "CSRF_TOKEN_INVALID" : "ADMIN_ACCESS_FORBIDDEN";
        String message = csrfFailure
                ? "Invalid or missing CSRF token"
                : "Administrator access is required";

        ErrorResponse body = ErrorResponse.from(status, code, message);
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
