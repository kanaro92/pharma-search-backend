package com.pharmasearch.service;

import com.pharmasearch.constants.InquiryStatus;
import com.pharmasearch.model.InquiryMessage;
import com.pharmasearch.model.MedicationInquiry;
import com.pharmasearch.model.User;
import com.pharmasearch.repository.InquiryMessageRepository;
import com.pharmasearch.repository.MedicationInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationInquiryService {
    private final MedicationInquiryRepository inquiryRepository;
    private final InquiryMessageRepository messageRepository;
    private final UserService userService;

    @Transactional
    public MedicationInquiry createInquiry(String medicationName, String patientNote) {
        User currentUser = userService.getCurrentUser();
        
        MedicationInquiry inquiry = MedicationInquiry.builder()
                .medicationName(medicationName)
                .patientNote(patientNote)
                .status(InquiryStatus.PENDING)
                .user(currentUser)
                .build();

        return inquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public List<MedicationInquiry> getUserInquiries() {
        User currentUser = userService.getCurrentUser();
        return inquiryRepository.findByUser(currentUser);
    }

    @Transactional(readOnly = true)
    public List<MedicationInquiry> getPendingInquiries() {
        return inquiryRepository.findByUserAndStatus(userService.getCurrentUser(), InquiryStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<MedicationInquiry> getInquiriesForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return inquiryRepository.findByUserId(currentUser.getId());
    }

    @Transactional(readOnly = true)
    public List<InquiryMessage> getMessages(Long inquiryId) {
        MedicationInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));
        return messageRepository.findByInquiryOrderByCreatedAtAsc(inquiry);
    }

    @Transactional
    public InquiryMessage addMessage(Long inquiryId, String content) {
        User currentUser = userService.getCurrentUser();
        MedicationInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        InquiryMessage message = InquiryMessage.builder()
                .content(content)
                .inquiry(inquiry)
                .sender(currentUser)
                .build();

        message = messageRepository.save(message);

        // Update inquiry status when pharmacist responds
        if ("PHARMACIST".equals(currentUser.getRole())) {
            inquiry.setStatus(InquiryStatus.RESPONDED);
            inquiryRepository.save(inquiry);
        }

        return message;
    }

    @Transactional
    public void closeInquiry(Long inquiryId) {
        MedicationInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));
        
        inquiry.setStatus(InquiryStatus.CLOSED);
        inquiryRepository.save(inquiry);
    }
}
