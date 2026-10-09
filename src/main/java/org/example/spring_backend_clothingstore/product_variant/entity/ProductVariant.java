package org.example.spring_backend_clothingstore.product_variant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.spring_backend_clothingstore.product.entity.Product;
import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "product_variants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "variant")
    private List<ProductVariant> variants;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "size", nullable = false)
    private ProductSize size;

    @Min(0)
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "color", nullable = false, length = 50)
    private String color;

    @Column(name = "image_url", nullable = false, length = 250)
    private String imageUrl;
}
