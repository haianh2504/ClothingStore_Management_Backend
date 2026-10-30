package org.example.spring_backend_clothingstore.admin.dto;

/**
 * Dữ liệu tối thiểu trả về cho biết trạng thái và email của quản trị viên
 * đang được xác thực bằng session Cookie phía máy chủ.
 */
public record CurrentSessionResponse(
        boolean authenticated,
        String email
) {
}
