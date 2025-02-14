package com.pharmasearch.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.pharmasearch.model.FcmToken;
import com.pharmasearch.model.User;
import com.pharmasearch.repository.FcmTokenRepository;
import com.pharmasearch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseService {
    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerToken(Long userId, String token) {
        log.debug("Registering FCM token for user {}: {}", userId, token);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // First deactivate all tokens for this user that are different from the new token
        tokenRepository.deactivateAllTokensExceptThis(user.getId(), token);

        // Check if this exact token exists and is active
        var existingToken = tokenRepository.findByTokenAndIsActiveTrue(token);

        if (existingToken.isPresent()) {
            FcmToken fcmToken = existingToken.get();
            // If token exists but belongs to another user, deactivate it
            if (!fcmToken.getUser().getId().equals(userId)) {
                tokenRepository.deactivateToken(fcmToken.getUser(), token);
                createNewToken(user, token);
            } else {
                // Token exists for this user, just update lastUsedAt
                fcmToken.setLastUsedAt(LocalDateTime.now());
                tokenRepository.save(fcmToken);
                log.debug("Updated existing FCM token for user {}", userId);
            }
        } else {
            // Token doesn't exist, create new one
            createNewToken(user, token);
        }

        log.info("FCM token registered successfully for user {}", userId);
    }

    private void createNewToken(User user, String token) {
        FcmToken fcmToken = new FcmToken();
        fcmToken.setUser(user);
        fcmToken.setToken(token);
        fcmToken.setActive(true);
        fcmToken.setCreatedAt(LocalDateTime.now());
        fcmToken.setLastUsedAt(LocalDateTime.now());
        tokenRepository.save(fcmToken);
        log.debug("Created new FCM token for user {}", user.getId());
    }

    @Transactional
    public void sendMessageNotification(Long userId, String senderName, String messageContent) {
        log.debug("Sending message notification to user {}", userId);

        List<FcmToken> userTokens = tokenRepository.findByUserAndIsActiveTrue(
            userRepository.getReferenceById(userId)
        );

        if (userTokens.isEmpty()) {
            log.warn("No active FCM tokens found for user {}", userId);
            return;
        }

        Notification notification = Notification.builder()
            .setTitle("New message from " + senderName)
            .setBody(messageContent)
            .build();

        for (FcmToken fcmToken : userTokens) {
            try {
                Message message = Message.builder()
                    .setToken(fcmToken.getToken())
                    .setNotification(notification)
                    .putData("type", "message")
                    .build();

                firebaseMessaging.send(message);
                fcmToken.setLastUsedAt(LocalDateTime.now());
                tokenRepository.save(fcmToken);

                log.debug("Notification sent successfully to token {}", fcmToken.getToken());
            } catch (Exception e) {
                log.error("Failed to send notification to token {}: {}", fcmToken.getToken(), e.getMessage());
                if (isInvalidTokenError(e)) {
                    tokenRepository.deactivateToken(fcmToken.getUser(), fcmToken.getToken());
                }
            }
        }
    }

    @Transactional
    public void sendMedicationSearchNotification(List<Long> pharmacistIds, String medicationName, double latitude, double longitude) {
        log.debug("Sending medication search notification to {} pharmacists", pharmacistIds.size());

        for (Long pharmacistId : pharmacistIds) {
            List<FcmToken> pharmacistTokens = tokenRepository.findByUserAndIsActiveTrue(
                userRepository.getReferenceById(pharmacistId)
            );

            if (pharmacistTokens.isEmpty()) {
                log.warn("No active FCM tokens found for pharmacist {}", pharmacistId);
                continue;
            }

            Notification notification = Notification.builder()
                .setTitle("Nouvelle demande de médicament")
                .setBody("Un patient recherche " + medicationName)
                .build();

            for (FcmToken fcmToken : pharmacistTokens) {
                try {
                    Message message = Message.builder()
                        .setToken(fcmToken.getToken())
                        .setNotification(notification)
                        .putData("type", "medication_search")
                        .putData("medication", medicationName)
                        .putData("latitude", String.valueOf(latitude))
                        .putData("longitude", String.valueOf(longitude))
                        .build();

                    firebaseMessaging.send(message);
                    fcmToken.setLastUsedAt(LocalDateTime.now());
                    tokenRepository.save(fcmToken);

                    log.debug("Notification sent successfully to pharmacist {}", pharmacistId);
                } catch (Exception e) {
                    log.error("Failed to send notification to pharmacist {}: {}", pharmacistId, e.getMessage());
                    if (isInvalidTokenError(e)) {
                        tokenRepository.deactivateToken(fcmToken.getUser(), fcmToken.getToken());
                    }
                }
            }
        }
    }

    private boolean isInvalidTokenError(Exception e) {
        return e.getMessage() != null && (
            e.getMessage().contains("InvalidRegistration") ||
            e.getMessage().contains("NotRegistered") ||
            e.getMessage().contains("InvalidApnsCredential")
        );
    }
}
