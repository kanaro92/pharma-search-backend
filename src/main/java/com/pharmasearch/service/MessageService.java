package com.pharmasearch.service;

import com.pharmasearch.model.Message;
import com.pharmasearch.model.User;
import com.pharmasearch.model.MedicationRequest;
import com.pharmasearch.repository.MessageRepository;
import com.pharmasearch.repository.UserRepository;
import com.pharmasearch.repository.MedicationRequestRepository;
import com.pharmasearch.service.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MedicationRequestRepository medicationRequestRepository;
    private final FirebaseService firebaseService;

    public Message sendMessage(Long senderId, Long receiverId, String content, Long medicationRequestId) {
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        
        if (medicationRequestId != null) {
            MedicationRequest request = medicationRequestRepository.findById(medicationRequestId)
                .orElseThrow(() -> new RuntimeException("Medication request not found"));
            message.setMedicationRequest(request);
        }

        Message savedMessage = messageRepository.save(message);
        
        // Send push notification
        firebaseService.sendMessageNotification(receiver.getId(), sender.getName(), content);
        
        return savedMessage;
    }

    public List<Message> getConversation(Long senderId, Long receiverId) {
        return messageRepository.findBySenderIdAndReceiverIdOrderByTimestampDesc(senderId, receiverId);
    }

    public List<Message> getUnreadMessages(Long receiverId) {
        return messageRepository.findByReceiverIdAndReadFalseOrderByTimestampDesc(receiverId);
    }

    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        messageRepository.save(message);
    }

    public List<Message> getMessagesByRequest(Long requestId) {
        return messageRepository.findByMedicationRequestIdOrderByTimestampDesc(requestId);
    }
}
