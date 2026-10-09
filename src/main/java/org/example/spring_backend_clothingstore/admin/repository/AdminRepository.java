package org.example.spring_backend_clothingstore.admin.repository;

import org.example.spring_backend_clothingstore.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {
    // Find By Email
    Optional<Admin> findByEmail(String email);
}
