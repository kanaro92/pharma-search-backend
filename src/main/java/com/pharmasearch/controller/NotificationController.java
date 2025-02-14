package com.pharmasearch.controller;

import com.pharmasearch.dto.FcmTokenRequest;
import com.pharmasearch.model.User;
import com.pharmasearch.service.FirebaseService;
import com.pharmasearch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final FirebaseService firebaseService;
    private final UserService userService;

    @PostMapping("/token")
    public ResponseEntity<Void> registerToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FcmTokenRequest request) {
        User user = userService.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        firebaseService.registerToken(user.getId(), request.getToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test")
    public ResponseEntity<String> testNotification(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            firebaseService.sendMessageNotification(
                user.getId(),
                "Test",
                "Ceci est une notification de test"
            );
            return ResponseEntity.ok("Notification envoyée avec succès");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }
}
