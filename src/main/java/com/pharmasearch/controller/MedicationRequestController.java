package com.pharmasearch.controller;

import com.pharmasearch.dto.MedicationRequestDTO;
import com.pharmasearch.model.Message;
import com.pharmasearch.model.MedicationRequest;
import com.pharmasearch.model.User;
import com.pharmasearch.service.MessageService;
import com.pharmasearch.service.MedicationRequestService;
import com.pharmasearch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medication-requests")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MedicationRequestController {
    private final MedicationRequestService medicationRequestService;
    private final MessageService messageService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<MedicationRequest> createRequest(@RequestBody MedicationRequestDTO requestDTO) {
        User currentUser = userService.getCurrentUser();
        MedicationRequest savedRequest = medicationRequestService.createRequest(requestDTO, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
    }

    @PostMapping("/{requestId}/messages")
    public ResponseEntity<Message> sendMessage(
            @PathVariable Long requestId,
            @RequestBody Map<String, String> request) {
        User currentUser = userService.getCurrentUser();
        MedicationRequest medicationRequest = medicationRequestService.getRequest(requestId);

        // Determine receiver based on who's sending the message
        Long receiverId;
        if (currentUser.getId().equals(medicationRequest.getUser().getId())) {
            // If user is sending, receiver is the pharmacy
            receiverId = medicationRequest.getPharmacy().getId();
        } else {
            // If pharmacy is sending, receiver is the user
            receiverId = medicationRequest.getUser().getId();
        }

        String content = request.get("content");
        Message message = messageService.sendMessage(requestId, content, receiverId);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{requestId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long requestId) {
        List<Message> messages = messageService.getMessagesByRequest(requestId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/my")
    public ResponseEntity<List<MedicationRequest>> getMyRequests() {
        User currentUser = userService.getCurrentUser();
        List<MedicationRequest> requests = medicationRequestService.getUserRequests(currentUser);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    public ResponseEntity<List<MedicationRequest>> getPharmacyRequests(@PathVariable Long pharmacyId) {
        User currentUser = userService.getCurrentUser();
        List<MedicationRequest> requests = medicationRequestService.getPharmacyRequests(pharmacyId, currentUser);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{requestId}/status")
    public ResponseEntity<MedicationRequest> updateRequestStatus(
            @PathVariable Long requestId,
            @RequestBody String status) {
        User currentUser = userService.getCurrentUser();
        MedicationRequest request = medicationRequestService.updateRequestStatus(requestId, status, currentUser);
        return ResponseEntity.ok(request);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long requestId) {
        User currentUser = userService.getCurrentUser();
        medicationRequestService.deleteRequest(requestId, currentUser);
        return ResponseEntity.ok().build();
    }
}
