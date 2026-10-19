package org.example.spring_backend_clothingstore.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.admin.entity.Admin;
import org.example.spring_backend_clothingstore.admin.exception.EmailNotFoundException;
import org.example.spring_backend_clothingstore.admin.repository.AdminRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return loadUserByEmail(email);
    }

    public UserDetails loadUserByEmail(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(EmailNotFoundException::new);

        return User.withUsername(admin.getEmail())
                .password(admin.getPasswordHash().passwordHash())
                .roles("ADMIN")
                .build();
    }
}
