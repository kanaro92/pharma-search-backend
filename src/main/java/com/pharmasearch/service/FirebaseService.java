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

        // Get only the most recently used token
        FcmToken userToken = tokenRepository.findTopByUserAndIsActiveTrueOrderByLastUsedAtDesc(
                userRepository.getReferenceById(userId)
        ).orElse(null);

        if (userToken == null) {
            log.warn("No active FCM token found for user {}", userId);
            return;
        }

        try {
            Notification notification = Notification.builder()
                    .setTitle("New message from " + senderName)
                    .setBody(messageContent)
                    .build();

            Message message = Message.builder()
                    .setToken(userToken.getToken())
                    .setNotification(notification)
                    .putData("type", "message")
                    .build();

            firebaseMessaging.send(message);
            userToken.setLastUsedAt(LocalDateTime.now());
            tokenRepository.save(userToken);

            log.debug("Notification sent successfully to token {}", userToken.getToken());
        } catch (Exception e) {
            log.error("Error sending notification to token {}: {}", userToken.getToken(), e.getMessage());
            if (e.getMessage().contains("registration-token-not-registered")) {
                log.info("Token is invalid, deactivating: {}", userToken.getToken());
                userToken.setActive(false);
                tokenRepository.save(userToken);
            }
        }
    }

    @Transactional
    public void sendMedicationSearchNotification(Long pharmacistId, Long medicationInquiryId, String medicationName, String patientNote, User patient) {
        log.debug("Sending medication search notification to pharmacist {}", pharmacistId);

        // Get only the most recently used token
        FcmToken userToken = tokenRepository.findTopByUserAndIsActiveTrueOrderByLastUsedAtDesc(
                userRepository.getReferenceById(pharmacistId)
        ).orElse(null);

        if (userToken == null) {
            log.warn("No active FCM token found for pharmacist {}", pharmacistId);
            return;
        }

        try {
            Notification notification = Notification.builder()
                    .setTitle("Nouvelle demande de médicament")
                    .setBody("Un patient recherche " + medicationName)
                    .build();

            Message message = Message.builder()
                    .setToken(userToken.getToken())
                    .setNotification(notification)
                    .putData("type", "medication_search")
                    .putData("medication_name", medicationName)
                    .putData("patient_note", patientNote)
                    .putData("user_id", patient.getId().toString())
                    .putData("user_name", patient.getName())
                    .putData("notification_type", "notification")
                    .putData("request_id", medicationInquiryId.toString()) // Empty string for backward compatibility
                    .putData("pharmacy_id", "") // Empty string for backward compatibility
                    .putData("latitude", "0.0") // Default value for backward compatibility
                    .putData("longitude", "0.0") // Default value for backward compatibility
                    .build();

            String result = firebaseMessaging.send(message);
            userToken.setLastUsedAt(LocalDateTime.now());
            tokenRepository.save(userToken);

            log.debug("Medication search notification sent successfully to token {} with result: {}",
                    userToken.getToken(), result);
        } catch (Exception e) {
            log.error("Error sending medication search notification to pharmacist {}: {}", pharmacistId, e.getMessage());
            log.warn("Failed to send notification to pharmacist {}", pharmacistId);
        }
    }

    @Transactional
    public void sendMedicationRequestNotification(Long userId, String medicationName, String userName, Map<String, String> data) {
        log.debug("Sending medication request notification to user {}", userId);

        // Get only the most recently used token
        FcmToken userToken = tokenRepository.findTopByUserAndIsActiveTrueOrderByLastUsedAtDesc(
                userRepository.getReferenceById(userId)
        ).orElse(null);

        if (userToken == null) {
            log.warn("No active FCM token found for user {}", userId);
            return;
        }

        try {
            Notification notification = Notification.builder()
                    .setTitle("Nouvelle demande de médicament")
                    .setBody("Un patient recherche " + medicationName)
                    .build();

            Message.Builder messageBuilder = Message.builder()
                    .setToken(userToken.getToken())
                    .setNotification(notification)
                    .putData("type", "medication_search")
                    .putData("medication", medicationName)
                    .putData("userName", userName);

            // Add any additional data
            if (data != null) {
                data.forEach(messageBuilder::putData);
            }

            firebaseMessaging.send(messageBuilder.build());
            userToken.setLastUsedAt(LocalDateTime.now());
            tokenRepository.save(userToken);

            log.debug("Medication request notification sent successfully to token {}", userToken.getToken());
        } catch (Exception e) {
            log.error("Error sending notification to token {}: {}", userToken.getToken(), e.getMessage());
            if (e.getMessage().contains("registration-token-not-registered")) {
                log.info("Token is invalid, deactivating: {}", userToken.getToken());
                userToken.setActive(false);
                tokenRepository.save(userToken);
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
