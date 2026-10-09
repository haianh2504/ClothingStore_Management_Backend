package org.example.spring_backend_clothingstore.admin.initializer;

import lombok.RequiredArgsConstructor;

import org.example.spring_backend_clothingstore.admin.entity.Admin;
import org.example.spring_backend_clothingstore.admin.repository.AdminRepository;
import org.example.spring_backend_clothingstore.composition.password.service.PasswordHasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordHasher passwordHasher;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.password}")
    private String rawPassword;

    @Override
    public void run(String... args) {
        if (adminRepository.count() > 0) {
            return;
        }

        adminRepository.save(new Admin(email, passwordHasher.hash(rawPassword)));
    }
}
