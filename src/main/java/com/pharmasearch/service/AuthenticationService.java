package com.pharmasearch.service;

import com.pharmasearch.constants.UserRoles;
import com.pharmasearch.dto.AuthenticationRequest;
import com.pharmasearch.dto.AuthenticationResponse;
import com.pharmasearch.dto.RegisterRequest;
import com.pharmasearch.model.User;
import com.pharmasearch.repository.UserRepository;
import com.pharmasearch.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        try {
            log.debug("Attempting to register user with email: {}", request.getEmail());

            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                log.error("Registration failed: Email already exists - {}", request.getEmail());
                throw new RuntimeException("Email already exists");
            }

            var user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(UserRoles.USER)
                    .build();

            userRepository.save(user);
            var jwtToken = jwtService.generateToken(user);

            log.info("Successfully registered user: {}", user.getEmail());
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (Exception e) {
            log.error("Error during registration for email: {}", request.getEmail(), e);
            throw new RuntimeException("Registration failed", e);
        }
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            log.debug("Attempting to authenticate user: {}", request.getEmail());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.error("Authentication failed: User not found - {}", request.getEmail());
                        return new BadCredentialsException("User not found");
                    });

            var jwtToken = jwtService.generateToken(user);

            log.info("Successfully authenticated user: {}", user.getEmail());
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (BadCredentialsException e) {
            log.error("Authentication failed: Invalid credentials for user - {}", request.getEmail());
            throw e;
        } catch (Exception e) {
            log.error("Error during authentication for user: {}", request.getEmail(), e);
            throw new RuntimeException("Authentication failed", e);
        }
    }
}
