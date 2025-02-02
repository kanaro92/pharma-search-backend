package com.pharmasearch.controller;

import com.pharmasearch.model.InquiryMessage;
import com.pharmasearch.model.MedicationInquiry;
import com.pharmasearch.service.MedicationInquiryService;
import com.pharmasearch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MedicationInquiryController {
    private final MedicationInquiryService medicationInquiryService;
    private final UserService userService;

    @PostMapping("/medication-inquiries")
    public ResponseEntity<MedicationInquiry> createInquiry(@RequestBody Map<String, String> request) {
        String medicationName = request.get("medicationName");
        String patientNote = request.get("patientNote");

        MedicationInquiry inquiry = medicationInquiryService.createInquiry(medicationName, patientNote);
        return ResponseEntity.ok(inquiry);
    }

    @GetMapping("/medication-inquiries/my")
    public ResponseEntity<List<MedicationInquiry>> getMyInquiries() {
        List<MedicationInquiry> inquiries = medicationInquiryService.getInquiriesForCurrentUser();
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping("/medication-inquiries")
    public ResponseEntity<List<MedicationInquiry>> getUserInquiries() {
        List<MedicationInquiry> inquiries = medicationInquiryService.getUserInquiries();
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping("/medication-inquiries/pending")
    @PreAuthorize("hasRole('PHARMACIST')")
    public ResponseEntity<List<MedicationInquiry>> getPendingInquiries() {
        List<MedicationInquiry> inquiries = medicationInquiryService.getPendingInquiries();
        return ResponseEntity.ok(inquiries);
    }

    // New endpoint specifically for pharmacists
    @GetMapping("/pharmacist/inquiries")
    @PreAuthorize("hasRole('PHARMACIST')")
    public ResponseEntity<List<MedicationInquiry>> getPharmacistInquiries() {
        List<MedicationInquiry> inquiries = medicationInquiryService.getPendingInquiries();
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping("/medication-inquiries/{inquiryId}/messages")
    public ResponseEntity<List<InquiryMessage>> getMessages(@PathVariable Long inquiryId) {
        List<InquiryMessage> messages = medicationInquiryService.getMessages(inquiryId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/medication-inquiries/{inquiryId}/messages")
    @PreAuthorize("hasRole('PHARMACIST')")
    public ResponseEntity<InquiryMessage> addMessage(
            @PathVariable Long inquiryId,
            @RequestBody Map<String, String> request) {
        String content = request.get("content");
        InquiryMessage message = medicationInquiryService.addMessage(inquiryId, content);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/medication-inquiries/{inquiryId}/close")
    @PreAuthorize("hasRole('PHARMACIST')")
    public ResponseEntity<Void> closeInquiry(@PathVariable Long inquiryId) {
        medicationInquiryService.closeInquiry(inquiryId);
        return ResponseEntity.ok().build();
    }
}
