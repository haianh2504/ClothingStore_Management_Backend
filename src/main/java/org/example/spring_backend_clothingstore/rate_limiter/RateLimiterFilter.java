package org.example.spring_backend_clothingstore.rate_limiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // đánh dấu đây là 1 bean để Spring quản lý
@RequiredArgsConstructor
public class RateLimiterFilter extends OncePerRequestFilter {
    private final RateLimiterService rateLimiterService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String clientKey = getClientKey(request); // address IP
        String methodKey = getMethodKey(request); // HTTP method

        boolean globalAllowed = rateLimiterService.tryAcquire1stLayer(clientKey);
        boolean methodAllowed = rateLimiterService.tryAccquire2ndLayer(methodKey,clientKey);

        if(!globalAllowed || !methodAllowed) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                    {
                        "status": 429,
                        "code": "RATE_LIMIT_EXCEEDED",
                        "message": "Too many requests",
                        "data": null
                    }
                    """);
            return;
        }
        filterChain.doFilter(request, response); // đẩy xuống cho DispatcherServlet
    }

    // getClientKey ( IP address )
    private String getClientKey(HttpServletRequest request) {
        String clientIP = request.getRemoteAddr();
        return clientIP;
    }
    // getMethod
    private String getMethodKey(HttpServletRequest request) {
        String method = request.getMethod();
        return method;
    }
}

//