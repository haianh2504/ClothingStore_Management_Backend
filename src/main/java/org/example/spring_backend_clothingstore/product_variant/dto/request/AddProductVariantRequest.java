package org.example.spring_backend_clothingstore.product_variant.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

/** Chứa các part multipart cần thiết để tạo product variant. */
public class AddProductVariantRequest {
    @NotBlank(message = "Color is required")
    @Size(max = 50, message = "Invalid color")
    private String color;

    @NotBlank(message = "Size is required")
    private String size;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be zero or greater")
    private Integer quantity;

    @NotNull(message = "Image file is required")
    private MultipartFile image;

    public String getColor() { return color; }
    public String getSize() { return size; }
    public Integer getQuantity() { return quantity; }
    public MultipartFile getImage() { return image; }
    public void setColor(String color) { this.color = color; }
    public void setSize(String size) { this.size = size; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setImage(MultipartFile image) { this.image = image; }
}
