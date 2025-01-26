package com.pharmasearch.security;

import com.pharmasearch.repository.PharmacyRepository;
import com.pharmasearch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final PharmacyRepository pharmacyRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email: {}", email);

        try {
            // First try to find a pharmacy user
            var pharmacyUser = pharmacyRepository.findByEmail(email);
            if (pharmacyUser.isPresent()) {
                log.debug("Found pharmacy user: {}", email);
                return pharmacyUser.get();
            }

            // If not found, try to find a regular user
            var regularUser = userRepository.findByEmail(email);
            if (regularUser.isPresent()) {
                log.debug("Found regular user: {}", email);
                return regularUser.get();
            }

            log.error("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        } catch (Exception e) {
            log.error("Error loading user by email: {}", email, e);
            throw new UsernameNotFoundException("Error loading user", e);
        }
    }
}
