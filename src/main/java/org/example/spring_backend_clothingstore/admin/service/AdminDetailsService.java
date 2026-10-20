package org.example.spring_backend_clothingstore.admin.service;


import org.example.spring_backend_clothingstore.admin.entity.Admin;
import org.example.spring_backend_clothingstore.admin.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AdminDetailsService implements UserDetailsService {
    @Autowired // hoặc là @RequiredArgsConstructor thêm final
    private AdminRepository adminRepository;

    // Phương thức load info của user lên ( của UserDetailService )
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Objects.requireNonNull(email, "Email cannot be null");
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException(email)
                );
        return org.springframework.security.core.userdetails.User
                .withUsername(admin.getEmail())
                .password(admin.getPasswordHash().passwordHash())
                .roles("ADMIN")
                .build();
    }
}
