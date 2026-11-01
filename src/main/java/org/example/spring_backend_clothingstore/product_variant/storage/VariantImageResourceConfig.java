package org.example.spring_backend_clothingstore.product_variant.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/** Cho phép frontend truy cập các ảnh variant đã lưu trên filesystem. */
@Configuration
public class VariantImageResourceConfig implements WebMvcConfigurer {
    private final String urlPrefix;
    private final String storageDirectory;

    public VariantImageResourceConfig(
            @Value("${app.storage.variant-image-url-prefix:/uploads/variant-images}") String urlPrefix,
            @Value("${app.storage.variant-image-dir:uploads/variant-images}") String storageDirectory
    ) {
        this.urlPrefix = urlPrefix.replaceAll("/+$", "");
        this.storageDirectory = Paths.get(storageDirectory).toAbsolutePath().normalize().toUri().toString();
    }

    /** Đăng ký resource handler cho URL ảnh variant. */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler(urlPrefix + "/**")
                .addResourceLocations(storageDirectory);

        /** Browser URL:
         /uploads/variant-images/file.png

         Server filesystem:
         uploads/variant-images/file.png **/

        // -> Frontend can use <image src="/bibibibi.png">
    }
}
