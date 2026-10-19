package org.example.spring_backend_clothingstore.admin.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class EmailNotFoundException extends UsernameNotFoundException {

    public EmailNotFoundException() {
        super("Admin email not found");
    }
}
