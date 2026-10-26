package org.example.spring_backend_clothingstore.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.admin.dto.LoginRequest;
import org.example.spring_backend_clothingstore.admin.dto.LoginResponse;
import org.example.spring_backend_clothingstore.api_response_schema.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // Chưa xác thực và xác thực rồi sẽ cập nhật thông tin sau đó
        // Nếu đăng nhập thất bại -> tung authenticationException
        Authentication authentication = authenticationManager.authenticate(
                // Khởi tạo đối tượng authentication thô
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );
        // Đảm bảo có session trước khi áp dụng servlet/Spring session ID rotation.
        request.getSession(true);

        // session fixation protection:
        // sessionID trước login sẽ không còn được dùng cho authenticated session
        request.changeSessionId();

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return ResponseEntity.ok(ApiResponse.from(
                200,
                "ADMIN_LOGIN_SUCCESS",
                "Admin login successful",
                OffsetDateTime.now(),
                new LoginResponse(true)
        ));
    }
}
