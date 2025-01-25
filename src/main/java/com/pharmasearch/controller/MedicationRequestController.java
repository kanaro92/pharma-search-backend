package com.pharmasearch.controller;

import com.pharmasearch.dto.MedicationRequestDTO;
import com.pharmasearch.model.MedicationRequest;
import com.pharmasearch.model.Message;
import com.pharmasearch.model.User;
import com.pharmasearch.service.MedicationRequestService;
import com.pharmasearch.service.MessageService;
import com.pharmasearch.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medication-requests")
@RequiredArgsConstructor
public class MedicationRequestController {

    private final MedicationRequestService medicationRequestService;
    private final MessageService messageService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<MedicationRequest> createRequest(
            @RequestBody MedicationRequestDTO requestDTO,
            Authentication authentication) {
        MedicationRequest savedRequest = medicationRequestService.createRequest(requestDTO, authentication);
        return ResponseEntity.ok(savedRequest);
    }

    @PostMapping("/{requestId}/messages")
    public ResponseEntity<Message> sendMessage(
            @PathVariable Long requestId,
            @RequestBody MessageRequest messageRequest,
            Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        MedicationRequest request = medicationRequestService.getRequest(requestId);
        
        // Determine receiver based on who's sending the message
        Long receiverId;
        if (currentUser.getId().equals(request.getPatient().getId())) {
            // If patient is sending, receiver is the pharmacy
            receiverId = request.getPharmacy().getId();
        } else {
            // If pharmacy is sending, receiver is the patient
            receiverId = request.getPatient().getId();
        }

        Message message = messageService.sendMessage(
            currentUser.getId(),
            receiverId,
            messageRequest.getContent(),
            requestId
        );
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{requestId}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable Long requestId,
            Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        MedicationRequest request = medicationRequestService.getRequest(requestId);
        
        // Verify that the current user is either the patient or the pharmacy
        if (!currentUser.getId().equals(request.getPatient().getId()) &&
            !currentUser.getId().equals(request.getPharmacy().getId())) {
            throw new RuntimeException("You don't have permission to view these messages");
        }
        
        List<Message> messages = messageService.getMessagesByRequest(requestId);
        return ResponseEntity.ok(messages);
    }
}

@Data
class MessageRequest {
    private String content;
}
