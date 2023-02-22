package com.elastic.security.service;

import com.elastic.security.payload.LoginRequest;
import com.elastic.security.payload.LoginResponse;
import com.elastic.common.util.Utils;
import com.elastic.security.config.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    @Value("${fulfilman.app.jwtExpirationDay}")
    private Long expiryTime;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public SecurityService(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public LoginResponse login(LoginRequest request) {
        if(request == null) {
            return new LoginResponse("Login request is required");
        }
        if(!Utils.isOk(request.getUserName())) {
            return new LoginResponse("User name is required");
        }
        if(!Utils.isOk(request.getPassword())) {
            return new LoginResponse("Password is required");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        User userDetails = (User) authentication.getPrincipal();
        return new LoginResponse(userDetails.getUsername(),
                jwt,expiryTime+"d");
    }
}
