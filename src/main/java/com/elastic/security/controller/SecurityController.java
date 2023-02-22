package com.elastic.security.controller;

import com.elastic.security.payload.LoginRequest;
import com.elastic.security.payload.LoginResponse;
import com.elastic.security.service.SecurityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class SecurityController {
    private final SecurityService securityService;
    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/signin")
    public @ResponseBody
    LoginResponse login(@RequestBody LoginRequest request) {
        return securityService.login(request);
    }
}
