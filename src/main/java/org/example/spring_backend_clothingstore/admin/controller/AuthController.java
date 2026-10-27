package org.example.spring_backend_clothingstore.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.admin.dto.LoginRequest;
import org.example.spring_backend_clothingstore.admin.dto.LoginResponse;
import org.example.spring_backend_clothingstore.admin.dto.LogoutResponse;
import org.example.spring_backend_clothingstore.api_response_schema.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    @Value("${server.servlet.session.cookie.secure:false}")
    private boolean sessionCookieSecure;

    // LOGIN
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

    // LOGOUT
    //Frontend gửi DELETE + CSRF token
    //        ↓
    //Spring Security kiểm tra CSRF
    //        ↓
    //AuthController.logout()
    //        ↓
    //Tìm session
    //        ↓
    //Invalidate session trong Redis
    //        ↓
    //Xóa SecurityContext
    //        ↓
    //Gửi cookie ADMIN_SESSION với Max-Age=0
    //        ↓
    //Trả authenticated=false
    @DeleteMapping("/sessions/current")
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(
            HttpServletRequest request
    ) {
        // return null if do not have session
        var session = request.getSession(false);
        if (session != null) { // if request has session
            try {
                session.invalidate(); // delete session and everything bound to it
            } catch (IllegalStateException ignored) {
                // The session may have expired between lookup and invalidation.
            }
        }

        // delete authentication in current memory
        //Admin đang đăng nhập
        //        ↓
        //clearContext()
        //        ↓
        //Request hiện tại không còn authentication
        SecurityContextHolder.clearContext();

        // creating expired cookie
        ResponseCookie expiredSessionCookie = ResponseCookie.from("ADMIN_SESSION", "")
                .httpOnly(true)
                .secure(sessionCookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        ApiResponse<LogoutResponse> body = ApiResponse.from(
                200,
                "ADMIN_LOGOUT_SUCCESS",
                "Admin logout successful",
                OffsetDateTime.now(),
                new LogoutResponse(false)
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredSessionCookie.toString())
                .body(body);
    }
}
