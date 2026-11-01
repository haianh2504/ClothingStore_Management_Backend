package org.example.spring_backend_clothingstore.product_variant.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/** Lưu ảnh variant trên filesystem và tạo URL tương ứng cho API response. */
/**
 * save uploaded image on the server filesystem
 * Generate the URL stored in ProductVariant.imageUrl. **/
@Service
public class VariantImageStorage {
    private final Path storageDirectory; /** physical location on the server **/
    private final String urlPrefix; /** HTTP URL path returned to frontend **/

    public VariantImageStorage(
            @Value("${app.storage.variant-image-dir:uploads/variant-images}") String storageDirectory,
            @Value("${app.storage.variant-image-url-prefix:/uploads/variant-images}") String urlPrefix
    ) {
        this.storageDirectory = Paths.get(storageDirectory).toAbsolutePath().normalize();
        this.urlPrefix = urlPrefix.replaceAll("/+$", "");
    }

    /** Lưu file với tên ngẫu nhiên để tránh trùng tên và trả về URL public. */
    public String store(MultipartFile image) throws IOException {
        Files.createDirectories(storageDirectory);
        String extension = extensionOf(image.getOriginalFilename());
        String fileName = UUID.randomUUID() + extension;
        Path target = storageDirectory.resolve(fileName).normalize();
        if (!target.startsWith(storageDirectory)) {
            throw new IOException("Invalid image path");
        }
        image.transferTo(target);
        return urlPrefix + "/" + fileName;
    }

    /** Xóa file đã lưu khi transaction tạo variant thất bại. */
    public void deleteQuietly(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith(urlPrefix + "/")) return;
        try {
            Files.deleteIfExists(storageDirectory.resolve(imageUrl.substring(urlPrefix.length() + 1)).normalize());
        } catch (IOException ignored) {
            // Không che lỗi gốc của nghiệp vụ bằng lỗi dọn file tạm.
        }
    }

    private String extensionOf(String originalFilename) {
        if (originalFilename == null) return "";
        int dot = originalFilename.lastIndexOf('.');
        return dot >= 0 ? originalFilename.substring(dot).toLowerCase() : "";
    }
}
