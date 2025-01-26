package com.pharmasearch.controller;

import com.pharmasearch.model.InquiryMessage;
import com.pharmasearch.model.MedicationInquiry;
import com.pharmasearch.service.MedicationInquiryService;
import com.pharmasearch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medication-inquiries")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MedicationInquiryController {
    private final MedicationInquiryService medicationInquiryService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<MedicationInquiry> createInquiry(@RequestBody Map<String, String> request) {
        String medicationName = request.get("medicationName");
        String patientNote = request.get("patientNote");

        MedicationInquiry inquiry = medicationInquiryService.createInquiry(medicationName, patientNote);
        return ResponseEntity.ok(inquiry);
    }

    @GetMapping("/my")
    public ResponseEntity<List<MedicationInquiry>> getMyInquiries() {
        List<MedicationInquiry> inquiries = medicationInquiryService.getInquiriesForCurrentUser();
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping
    public ResponseEntity<List<MedicationInquiry>> getUserInquiries() {
        List<MedicationInquiry> inquiries = medicationInquiryService.getUserInquiries();
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<MedicationInquiry>> getPendingInquiries() {
        List<MedicationInquiry> inquiries = medicationInquiryService.getPendingInquiries();
        return ResponseEntity.ok(inquiries);
    }

    @PostMapping("/{inquiryId}/messages")
    public ResponseEntity<InquiryMessage> addMessage(
            @PathVariable Long inquiryId,
            @RequestBody Map<String, String> request) {
        String content = request.get("content");
        InquiryMessage message = medicationInquiryService.addMessage(inquiryId, content);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/{inquiryId}/close")
    public ResponseEntity<Void> closeInquiry(@PathVariable Long inquiryId) {
        medicationInquiryService.closeInquiry(inquiryId);
        return ResponseEntity.ok().build();
    }
}
