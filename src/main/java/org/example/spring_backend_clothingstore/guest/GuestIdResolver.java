package org.example.spring_backend_clothingstore.guest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Lấy guest ID từ cookie hoặc tạo UUID mới khi cookie chưa có hoặc bị sửa.
 * Cookie mới được ghi lại trước khi request tiếp tục xử lý.
 */
@Component
@RequiredArgsConstructor
public class GuestIdResolver {
    private final GuestIdValidator guestIdValidator;

    public String resolve(HttpServletRequest request, HttpServletResponse response) {
        String guestId = findGuestIdCookie(request);

        if (guestIdValidator.isValid(guestId)) {
            return guestId;
        }

        String newGuestId = UUID.randomUUID().toString();
        response.addHeader(HttpHeaders.SET_COOKIE, buildGuestCookie(newGuestId).toString());
        return newGuestId;
    }

    private String findGuestIdCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (GuestCookieConfig.COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private ResponseCookie buildGuestCookie(String guestId) {
        return ResponseCookie.from(GuestCookieConfig.COOKIE_NAME, guestId)
                .httpOnly(GuestCookieConfig.COOKIE_HTTP_ONLY)
                .secure(GuestCookieConfig.COOKIE_SECURE)
                .sameSite(GuestCookieConfig.COOKIE_SAME_SITE)
                .path(GuestCookieConfig.COOKIE_PATH)
                .maxAge(GuestCookieConfig.COOKIE_MAX_AGE)
                .build();
    }
}
