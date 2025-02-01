package com.pharmasearch.service;

import com.pharmasearch.model.Message;
import com.pharmasearch.model.MedicationRequest;
import com.pharmasearch.model.User;
import com.pharmasearch.repository.MessageRepository;
import com.pharmasearch.service.UserService;
import com.pharmasearch.service.MedicationRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final MedicationRequestService medicationRequestService;

    @Transactional
    public Message sendMessage(Long requestId, String content, Long receiverId) {
        User currentUser = userService.getCurrentUser();
        User receiver = userService.getUser(receiverId);
        MedicationRequest request = medicationRequestService.getRequest(requestId);

        // Verify that the current user is either the user or the pharmacy
        if (!currentUser.getId().equals(request.getUser().getId()) &&
            !currentUser.getId().equals(request.getPharmacy().getId())) {
            throw new RuntimeException("You don't have permission to send messages for this request");
        }

        // Verify that the receiver is either the user or the pharmacy of this request
        if (!receiver.getId().equals(request.getUser().getId()) &&
            !receiver.getId().equals(request.getPharmacy().getId())) {
            throw new RuntimeException("Invalid receiver for this medication request");
        }

        Message message = new Message();
        message.setRequest(request);
        message.setSender(currentUser);
        message.setReceiver(receiver);
        message.setContent(content);

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesByRequest(Long requestId) {
        User currentUser = userService.getCurrentUser();
        MedicationRequest request = medicationRequestService.getRequest(requestId);

        // Verify that the current user is either the user or the pharmacy
        if (!currentUser.getId().equals(request.getUser().getId()) &&
            !currentUser.getId().equals(request.getPharmacy().getId())) {
            throw new RuntimeException("You don't have permission to view these messages");
        }

        // Get messages between the current user and their conversation partner
        User conversationPartner;
        if (currentUser.getId().equals(request.getUser().getId())) {
            conversationPartner = request.getPharmacy();
        } else {
            conversationPartner = request.getUser();
        }

        return messageRepository.findMessagesBetweenUsers(request, currentUser, conversationPartner);
    }
}
