package com.pharmasearch.controller;

import com.pharmasearch.model.InquiryMessage;
import com.pharmasearch.model.MedicationInquiry;
import com.pharmasearch.model.Message;
import com.pharmasearch.model.MessageRequest;
import com.pharmasearch.model.PharmacyConversation;
import com.pharmasearch.model.User;
import com.pharmasearch.model.UserRole;
import com.pharmasearch.service.MedicationInquiryService;
import com.pharmasearch.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MedicationInquiryController {
    private final MedicationInquiryService medicationInquiryService;
    private final UserService userService;

    @PostMapping("/inquiries")
    public ResponseEntity<MedicationInquiry> createInquiry(@RequestBody MedicationInquiry inquiry) {
        MedicationInquiry createdInquiry = medicationInquiryService.createInquiry(
                inquiry.getMedicationName(),
                inquiry.getPatientNote()
        );
        return ResponseEntity.ok(createdInquiry);
    }

    @GetMapping("/inquiries/my")
    public ResponseEntity<List<MedicationInquiry>> getMyInquiries() {
        List<MedicationInquiry> inquiries = medicationInquiryService.getInquiriesForCurrentUser();
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping("/inquiries/pharmacist")
    @PreAuthorize("hasRole('PHARMACIST')")
    public ResponseEntity<List<MedicationInquiry>> getPharmacistInquiries() {
        List<MedicationInquiry> inquiries = medicationInquiryService.getPharmacistInquiries();
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping("/inquiries/pending")
    @PreAuthorize("hasRole('PHARMACIST')")
    public ResponseEntity<List<MedicationInquiry>> getPendingInquiries() {
        List<MedicationInquiry> inquiries = medicationInquiryService.getPendingInquiries();
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping("/inquiries/{inquiryId}/messages")
    public ResponseEntity<List<InquiryMessage>> getMessages(@PathVariable Long inquiryId) {
        List<InquiryMessage> messages = medicationInquiryService.getMessages(inquiryId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/inquiries/{inquiryId}/messages")
    public ResponseEntity<InquiryMessage> addMessage(
            @PathVariable Long inquiryId,
            @RequestBody MessageRequest messageRequest) {
        InquiryMessage message = medicationInquiryService.addMessage(inquiryId, messageRequest.getContent());
        return ResponseEntity.ok(message);
    }

    @PostMapping("/inquiries/{inquiryId}/close")
    @PreAuthorize("hasRole('PHARMACIST')")
    public ResponseEntity<Void> closeInquiry(@PathVariable Long inquiryId) {
        medicationInquiryService.closeInquiry(inquiryId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/inquiries/{inquiryId}/respond")
    @PreAuthorize("hasRole('PHARMACIST')")
    public ResponseEntity<MedicationInquiry> addRespondingPharmacy(@PathVariable Long inquiryId) {
        MedicationInquiry inquiry = medicationInquiryService.addRespondingPharmacy(inquiryId);
        return ResponseEntity.ok(inquiry);
    }

    @DeleteMapping("/inquiries/{inquiryId}/respond")
    @PreAuthorize("hasRole('PHARMACIST')")
    public ResponseEntity<MedicationInquiry> removeRespondingPharmacy(@PathVariable Long inquiryId) {
        MedicationInquiry inquiry = medicationInquiryService.removeRespondingPharmacy(inquiryId);
        return ResponseEntity.ok(inquiry);
    }

    @GetMapping("/inquiries/{inquiryId}/conversations")
    public ResponseEntity<List<PharmacyConversation>> getInquiryConversations(
            @PathVariable Long inquiryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MedicationInquiry inquiry = medicationInquiryService.getInquiryById(inquiryId);
            User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

            // Check if the current user is the inquiry owner
            if (!inquiry.getUser().getEmail().equals(currentUser.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<PharmacyConversation> conversations = medicationInquiryService.getInquiryConversations(inquiryId);
            return ResponseEntity.ok(conversations);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/inquiries/{inquiryId}/conversations/{pharmacyId}")
    public ResponseEntity<List<InquiryMessage>> getConversationMessages(
            @PathVariable Long inquiryId,
            @PathVariable Long pharmacyId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            MedicationInquiry inquiry = medicationInquiryService.getInquiryById(inquiryId);
            User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

            // Check if the current user is either the inquiry owner or the pharmacy
            boolean isOwner = inquiry.getUser().getEmail().equals(currentUser.getEmail());
            boolean isPharmacy = pharmacyId.equals(currentUser.getId()) && 
                               "PHARMACIST".equals(currentUser.getRole());

            if (!isOwner && !isPharmacy) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<InquiryMessage> messages = medicationInquiryService.getConversationMessages(inquiryId, pharmacyId);
            return ResponseEntity.ok(messages);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/inquiries/{inquiryId}/conversations/{pharmacyId}/messages")
    @PreAuthorize("hasAnyRole('USER', 'PHARMACIST')")
    public ResponseEntity<InquiryMessage> sendMessage(
            @PathVariable Long inquiryId,
            @PathVariable Long pharmacyId,
            @RequestBody MessageRequest messageRequest) {
        InquiryMessage message = medicationInquiryService.sendMessage(
            inquiryId, 
            pharmacyId, 
            messageRequest.getContent()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
}
