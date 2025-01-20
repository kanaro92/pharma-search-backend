package com.pharmasearch.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class FirebaseService {
    private final FirebaseMessaging firebaseMessaging;
    private final Map<Long, String> userTokens = new ConcurrentHashMap<>();

    public void registerToken(Long userId, String token) {
        userTokens.put(userId, token);
    }

    public void sendMessageNotification(Long userId, String senderName, String messageContent) {
        String userToken = userTokens.get(userId);
        if (userToken == null) {
            return;
        }

        Notification notification = Notification.builder()
            .setTitle("New message from " + senderName)
            .setBody(messageContent)
            .build();

        Message message = Message.builder()
            .setToken(userToken)
            .setNotification(notification)
            .putData("type", "message")
            .build();

        try {
            firebaseMessaging.send(message);
        } catch (Exception e) {
            // Log error and handle appropriately
        }
    }
}
