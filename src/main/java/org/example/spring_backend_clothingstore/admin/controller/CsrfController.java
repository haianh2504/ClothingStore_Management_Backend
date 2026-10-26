package org.example.spring_backend_clothingstore.admin.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Initializes the CSRF cookie for browser clients before their first unsafe request.
 * The token is intentionally not returned in the JSON body; the browser reads the
 * XSRF-TOKEN cookie and sends it back in the X-XSRF-TOKEN header.
 */
@RestController
@RequestMapping("/api/v1")
public class CsrfController {

    @GetMapping("/csrf-token")
    public void csrfToken(CsrfToken ignoredToken) {
        // Resolving CsrfToken causes CookieCsrfTokenRepository to issue XSRF-TOKEN.
    }
}
