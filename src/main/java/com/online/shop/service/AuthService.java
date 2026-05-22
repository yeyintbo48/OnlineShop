package com.online.shop.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.online.shop.dtos.AuthenticationRequest;
import com.online.shop.dtos.AuthenticationResponse;
import com.online.shop.dtos.RegisterRequest;
import com.online.shop.dtos.Role;
import com.online.shop.entity.User;
import com.online.shop.exception.ResourceNotFoundException;
import com.online.shop.repository.UserRepo;
import com.online.shop.security.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request){
        var user =  User.builder()
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .role(Role.USER)
        .build();
        userRepo.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse login(AuthenticationRequest request){
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            var user = userRepo.findByEmail(request.email()).orElseThrow(() ->new ResourceNotFoundException("User not found with this email:" + request.email()));

            var jwtToken = jwtService.generateToken(user);
            return new AuthenticationResponse(jwtToken);
    }
}
