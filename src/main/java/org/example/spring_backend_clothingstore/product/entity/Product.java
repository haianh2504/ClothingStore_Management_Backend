package org.example.spring_backend_clothingstore.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.spring_backend_clothingstore.product.entity.product_enum.ProductCategory;
import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.example.spring_backend_clothingstore.product.entity.product_name.ProductName;
import org.example.spring_backend_clothingstore.product.entity.product_name.ProductNameConverter;
import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "products"
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = ProductNameConverter.class)
    @Column(name = "name", nullable = false, unique = true, length = 250)
    private ProductName name;

    @Column(name = "description",nullable = false, length = 255)
    private String description;

    @Column(name = "category",nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ProductCategory category;

    @Column(name = "sale_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private SaleStatus saleStatus = SaleStatus.INACTIVE;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    @DecimalMin(value = "0.00")
    private BigDecimal price;

    @OneToMany(mappedBy = "product")
    private List<ProductVariant> variants = new ArrayList<>();
}
