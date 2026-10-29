package org.example.spring_backend_clothingstore.guest;

import java.time.Duration;

/**
 * Chứa tên cookie và các thuộc tính mặc định của cookie nhận diện guest.
 * Các giá trị này có thể được chuyển sang application properties sau.
 */
public final class GuestCookieConfig {
    public static final String COOKIE_NAME = "GUEST_ID";
    public static final String COOKIE_PATH = "/";
    public static final String COOKIE_SAME_SITE = "Lax";
    public static final Duration COOKIE_MAX_AGE = Duration.ofDays(30);

    // HTTP local chưa có TLS; khi deploy production nên đổi thành true.
    public static final boolean COOKIE_SECURE = false;
    public static final boolean COOKIE_HTTP_ONLY = true;

    private GuestCookieConfig() {
    }
}
