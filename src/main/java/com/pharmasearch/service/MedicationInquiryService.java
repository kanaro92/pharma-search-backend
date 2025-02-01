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
        User currentUser = userService.getCurrentUser();
        if ("PHARMACIST".equals(currentUser.getRole())) {
            // Pharmacists see both new inquiries and ones they're already responding to
            return inquiryRepository.findByStatusNotAndRespondingPharmacyIsNullOrRespondingPharmacy(
                InquiryStatus.CLOSED, currentUser);
        } else {
            // Regular users only see their own inquiries that aren't closed
            return inquiryRepository.findByUserAndStatusNot(currentUser, InquiryStatus.CLOSED);
        }
    }

    @Transactional(readOnly = true)
    public List<MedicationInquiry> getInquiriesForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        if ("PHARMACIST".equals(currentUser.getRole())) {
            // Pharmacists see inquiries they've responded to
            return inquiryRepository.findByRespondingPharmacy(currentUser);
        } else {
            // Regular users see their own inquiries
            return inquiryRepository.findByUserId(currentUser.getId());
        }
    }

    @Transactional(readOnly = true)
    public List<InquiryMessage> getMessages(Long inquiryId) {
        User currentUser = userService.getCurrentUser();
        MedicationInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        // Verify the user has access to this inquiry
        if ("PHARMACIST".equals(currentUser.getRole())) {
            if (inquiry.getRespondingPharmacy() != null && 
                !inquiry.getRespondingPharmacy().getId().equals(currentUser.getId())) {
                throw new RuntimeException("You don't have access to this inquiry");
            }
        } else if (!inquiry.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have access to this inquiry");
        }

        return messageRepository.findByInquiryOrderByCreatedAtAsc(inquiry);
    }

    @Transactional
    public InquiryMessage addMessage(Long inquiryId, String content) {
        User currentUser = userService.getCurrentUser();
        MedicationInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        // If this is a pharmacist's first response, set them as the responding pharmacy
        if ("PHARMACIST".equals(currentUser.getRole())) {
            if (inquiry.getRespondingPharmacy() == null) {
                inquiry.setRespondingPharmacy(currentUser);
            } else if (!inquiry.getRespondingPharmacy().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Another pharmacy has already responded to this inquiry");
            }
        }

        InquiryMessage message = InquiryMessage.builder()
                .content(content)
                .inquiry(inquiry)
                .sender(currentUser)
                .build();

        message = messageRepository.save(message);

        // Update inquiry status when pharmacist responds
        if ("PHARMACIST".equals(currentUser.getRole()) && 
            InquiryStatus.PENDING.equals(inquiry.getStatus())) {
            inquiry.setStatus(InquiryStatus.RESPONDED);
            inquiryRepository.save(inquiry);
        }

        return message;
    }

    @Transactional
    public void closeInquiry(Long inquiryId) {
        User currentUser = userService.getCurrentUser();
        MedicationInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));
        
        // Only the responding pharmacy can close the inquiry
        if (!inquiry.getRespondingPharmacy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the responding pharmacy can close this inquiry");
        }
        
        inquiry.setStatus(InquiryStatus.CLOSED);
        inquiryRepository.save(inquiry);
    }
}
