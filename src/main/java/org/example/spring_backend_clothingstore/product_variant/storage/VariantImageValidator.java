package org.example.spring_backend_clothingstore.product_variant.storage;

import org.example.spring_backend_clothingstore.exception.business.ProductVariantInformationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Kiểm tra ảnh theo extension, MIME header và magic bytes trước khi lưu. */
@Component
public class VariantImageValidator {
    /** Kiểm tra đủ ba lớp nhận diện file ảnh được phép upload. */
    public void validate(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw invalidImage();
        }

        String extension = extensionOf(image.getOriginalFilename()); /** đuôi file .jpg, .png,...**/
        String expectedContentType = expectedContentType(extension); /** Content-type expected **/
        String clientContentType = image.getContentType();           /** Content-type client sent **/

        if (expectedContentType == null
                || clientContentType == null
                || !expectedContentType.equalsIgnoreCase(clientContentType)
                || !matchesMagicBytes(extension, readBytes(image))) {
            throw invalidImage();
        }
    }

    /** Đọc bytes của file và chuyển lỗi đọc thành lỗi validation của API. */
    private byte[] readBytes(MultipartFile image) {
        try {
            return image.getBytes();
        } catch (IOException exception) {
            throw invalidImage();
        }
    }

    /** Chỉ cho phép các extension ảnh có magic bytes đã được hỗ trợ. */
    private String expectedContentType(String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            default -> null;
        };
    }

    /** Đối chiếu chữ ký nhị phân đầu file với extension do client gửi. */
    private boolean matchesMagicBytes(String extension, byte[] bytes) {
        return switch (extension) {
            case "jpg", "jpeg" -> startsWith(bytes, 0xFF, 0xD8, 0xFF);
            case "png" -> startsWith(bytes, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A);
            case "gif" -> startsWithAscii(bytes, "GIF87a") || startsWithAscii(bytes, "GIF89a");
            case "bmp" -> startsWithAscii(bytes, "BM");
            case "webp" -> startsWithAscii(bytes, "RIFF")
                    && startsWithAscii(bytes, 8, "WEBP");
            default -> false;
        };
    }

    /** Lấy extension cuối cùng và chuẩn hóa về chữ thường. */
    private String extensionOf(String originalFilename) {
        if (originalFilename == null) return "";
        int dot = originalFilename.lastIndexOf('.');
        return dot >= 0
                ? originalFilename.substring(dot + 1).toLowerCase(Locale.ROOT)
                : "";
    }

    /** Kiểm tra dãy byte đầu file có khớp signature đã cho hay không. */
    private boolean startsWith(byte[] bytes, int... signature) {
        if (bytes.length < signature.length) return false;
        for (int index = 0; index < signature.length; index++) {
            if ((bytes[index] & 0xFF) != signature[index]) return false;
        }
        return true;
    }

    /** Kiểm tra chuỗi ASCII tại vị trí bắt đầu của file. */
    private boolean startsWithAscii(byte[] bytes, String signature) {
        return startsWithAscii(bytes, 0, signature);
    }

    /** Kiểm tra chuỗi ASCII tại vị trí chỉ định của file. */
    private boolean startsWithAscii(byte[] bytes, int offset, String signature) {
        if (bytes.length < offset + signature.length()) return false;
        for (int index = 0; index < signature.length(); index++) {
            if (bytes[offset + index] != (byte) signature.charAt(index)) return false;
        }
        return true;
    }

    /** Tạo lỗi validation có cùng format với contract UC11. */
    private ProductVariantInformationException invalidImage() {
        return new ProductVariantInformationException(List.of(
                Map.of("field", "image", "message", "Invalid image file")
        ));
    }
}
