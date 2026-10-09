package org.example.spring_backend_clothingstore.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.spring_backend_clothingstore.composition.password.PasswordHash;
import org.example.spring_backend_clothingstore.composition.password.PasswordHashConverter;

@Entity
@Table(name = "admin")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin {

    @Id
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Convert(converter = PasswordHashConverter.class)
    @Column(name = "password_hash", nullable = false, length = 255)
    private PasswordHash passwordHash;

    // constructor for initializer
    public Admin(String email, PasswordHash passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
