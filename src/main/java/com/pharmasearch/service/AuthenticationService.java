package com.pharmasearch.service;

import com.pharmasearch.constants.UserRoles;
import com.pharmasearch.dto.AuthenticationRequest;
import com.pharmasearch.dto.AuthenticationResponse;
import com.pharmasearch.dto.RegisterRequest;
import com.pharmasearch.model.User;
import com.pharmasearch.model.Pharmacy;
import com.pharmasearch.repository.UserRepository;
import com.pharmasearch.repository.PharmacyRepository;
import com.pharmasearch.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PharmacyRepository pharmacyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private Map<String, Object> createUserClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("name", user.getName());
        claims.put("role", user.getRole());
        return claims;
    }

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        try {
            log.debug("Attempting to register user with email: {}", request.getEmail());

            // Validate role
            String role = request.getRole();
            if (role == null || role.isEmpty()) {
                role = UserRoles.USER; // Default role
            } else if (!role.equals(UserRoles.USER) && !role.equals(UserRoles.PHARMACIST)) {
                throw new IllegalArgumentException("Invalid role. Must be either USER or PHARMACIST");
            }

            // Check if email already exists
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                log.error("Registration failed: Email already exists - {}", request.getEmail());
                throw new IllegalArgumentException("Email already registered");
            }

            User user;
            if (UserRoles.PHARMACIST.equals(role)) {
                // Create pharmacy user
                var pharmacy = Pharmacy.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(role)
                        .enabled(true)
                        // Set default values for required fields
                        .address("Please update your address")
                        .latitude(0.0)
                        .longitude(0.0)
                        .build();
                user = pharmacyRepository.save(pharmacy);
            } else {
                // Create regular user
                user = User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(role)
                        .enabled(true)
                        .build();
                user = userRepository.save(user);
            }

            log.info("Successfully registered user: {}", user.getEmail());

            Map<String, Object> claims = createUserClaims(user);
            var jwtToken = jwtService.generateToken(claims, user);

            var userDto = AuthenticationResponse.UserDTO.fromUser(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .user(userDto)
                    .build();
        } catch (Exception e) {
            log.error("Registration failed with error: ", e);
            throw e;
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
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Map<String, Object> claims = createUserClaims(user);
            var jwtToken = jwtService.generateToken(claims, user);

            var userDto = AuthenticationResponse.UserDTO.fromUser(user);
            log.info("Successfully authenticated user: {}", user.getEmail());

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .user(userDto)
                    .build();
        } catch (BadCredentialsException e) {
            log.error("Authentication failed: Invalid credentials for user - {}", request.getEmail());
            throw e;
        } catch (Exception e) {
            log.error("Authentication failed with error: ", e);
            throw e;
        }
    }

    public AuthenticationResponse refreshToken(String email) {
        try {
            log.debug("Attempting to refresh token for user: {}", email);

            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Map<String, Object> claims = createUserClaims(user);
            var newToken = jwtService.generateToken(claims, user);

            log.info("Successfully refreshed token for user: {}", email);
            
            var userDto = AuthenticationResponse.UserDTO.fromUser(user);
            return AuthenticationResponse.builder()
                    .token(newToken)
                    .user(userDto)
                    .build();
        } catch (Exception e) {
            log.error("Token refresh failed with error: ", e);
            throw e;
        }
    }
}
