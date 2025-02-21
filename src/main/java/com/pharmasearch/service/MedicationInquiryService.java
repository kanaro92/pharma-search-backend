package com.pharmasearch.service;

import com.pharmasearch.constants.InquiryStatus;
import com.pharmasearch.model.*;
import com.pharmasearch.repository.InquiryMessageRepository;
import com.pharmasearch.repository.MedicationInquiryRepository;
import com.pharmasearch.repository.MessageRepository;
import com.pharmasearch.service.FirebaseService;
import com.pharmasearch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicationInquiryService {
    private final MedicationInquiryRepository inquiryRepository;
    private final InquiryMessageRepository messageRepository;
    private final UserService userService;
    private final FirebaseService firebaseService;

    @Transactional
    public MedicationInquiry createInquiry(String medicationName, String patientNote) {
        User currentUser = userService.getCurrentUser();

        MedicationInquiry inquiry = MedicationInquiry.builder()
                .medicationName(medicationName)
                .patientNote(patientNote)
                .status(InquiryStatus.PENDING)
                .user(currentUser)
                .build();

        MedicationInquiry savedInquiry = inquiryRepository.save(inquiry);

        // Send notifications to all pharmacists individually
        List<User> pharmacists = userService.getAllPharmacists();
        for (User pharmacist : pharmacists) {
            try {
                firebaseService.sendMedicationSearchNotification(
                        pharmacist.getId(),
                        savedInquiry.getId(),
                        medicationName,
                        patientNote,
                        currentUser
                );
            } catch (Exception e) {
                // Log error but continue with other pharmacists
                System.err.println("Failed to send notification to pharmacist " + pharmacist.getId() + ": " + e.getMessage());
            }
        }

        return savedInquiry;
    }

    @Transactional(readOnly = true)
    public List<MedicationInquiry> getUserInquiries() {
        User currentUser = userService.getCurrentUser();
        return inquiryRepository.findByUserOrderByCreatedAtDesc(currentUser);
    }

    @Transactional(readOnly = true)
    public List<MedicationInquiry> getPendingInquiries() {
        User currentUser = userService.getCurrentUser();
        if ("PHARMACIST".equals(currentUser.getRole())) {
            // Pharmacists see both new inquiries and ones they're already responding to
            return inquiryRepository.findByStatusNotAndRespondingPharmaciesEmptyOrContaining(
                    InquiryStatus.CLOSED, currentUser);
        } else {
            // Regular users only see their own inquiries that aren't closed
            return inquiryRepository.findByUserAndStatusNotOrderByCreatedAtDesc(currentUser, InquiryStatus.CLOSED);
        }
    }

    @Transactional(readOnly = true)
    public MedicationInquiry getInquiryById(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("Inquiry not found"));
    }

    @Transactional(readOnly = true)
    public List<PharmacyConversation> getInquiryConversations(Long inquiryId) {
        MedicationInquiry inquiry = getInquiryById(inquiryId);

        // Get all responding pharmacies and their latest messages
        return inquiry.getRespondingPharmacies().stream()
            .map(pharmacy -> {
                PharmacyConversation conversation = new PharmacyConversation();
                conversation.setPharmacyId(pharmacy.getId());
                conversation.setPharmacyName(pharmacy.getName());
                conversation.setInquiryId(inquiryId);

                // Get the latest message for this pharmacy
                Optional<InquiryMessage> latestMessage = messageRepository.findLatestMessage(inquiryId, pharmacy.getId());
                latestMessage.ifPresent(message -> {
                    conversation.setLastMessage(message.getContent());
                    conversation.setLastMessageTime(message.getCreatedAt());
                });

                return conversation;
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InquiryMessage> getConversationMessages(Long inquiryId, Long pharmacyId) {
        User currentUser = userService.getCurrentUser();
        MedicationInquiry inquiry = getInquiryById(inquiryId);

        // Check if the current user is either the inquiry owner or the pharmacy
        boolean isOwner = inquiry.getUser().getEmail().equals(currentUser.getEmail());
        boolean isPharmacy = pharmacyId.equals(currentUser.getId()) && 
                           "PHARMACIST".equals(currentUser.getRole());

        if (!isOwner && !isPharmacy) {
            throw new RuntimeException("User does not have permission to view this conversation");
        }

        // For pharmacists, check if they are part of the conversation
        if ("PHARMACIST".equals(currentUser.getRole())) {
            // If they're not part of the conversation, try to add them
            if (!inquiry.getRespondingPharmacies().contains(currentUser)) {
                // Only allow joining if the inquiry is not closed
                if (!InquiryStatus.CLOSED.equals(inquiry.getStatus())) {
                    inquiry.getRespondingPharmacies().add(currentUser);
                    if (InquiryStatus.PENDING.equals(inquiry.getStatus())) {
                        inquiry.setStatus(InquiryStatus.RESPONDED);
                    }
                    inquiryRepository.save(inquiry);
                } else {
                    throw new RuntimeException("Cannot join a closed inquiry");
                }
            }
        }

        // If user is owner, get all messages for this inquiry
        if (isOwner) {
            return messageRepository.findByInquiryOrderByCreatedAtAsc(inquiry);
        } else {
            // If pharmacist, only get messages between them and the user
            return messageRepository.findByInquiryIdAndPharmacyIdOrderByCreatedAtAsc(inquiryId, pharmacyId);
        }
    }

    @Transactional(readOnly = true)
    public List<InquiryMessage> getMessages(Long inquiryId) {
        User currentUser = userService.getCurrentUser();
        MedicationInquiry inquiry = getInquiryById(inquiryId);

        // Check if the current user is either the inquiry owner or the pharmacy
        boolean isOwner = inquiry.getUser().getEmail().equals(currentUser.getEmail());
        boolean isPharmacy = "PHARMACIST".equals(currentUser.getRole());

        if (!isOwner && !isPharmacy) {
            throw new RuntimeException("User does not have permission to view this inquiry");
        }

        // For pharmacists, allow viewing messages if the inquiry is not closed
        if ("PHARMACIST".equals(currentUser.getRole())) {
            if (InquiryStatus.CLOSED.equals(inquiry.getStatus()) &&
                !inquiry.getRespondingPharmacies().contains(currentUser)) {
                throw new RuntimeException("You don't have access to this closed inquiry");
            }
        } else if (!inquiry.getUser().getId().equals(currentUser.getId())) {
            // Regular users can only view their own inquiries
            throw new RuntimeException("You don't have access to this inquiry");
        }

        return messageRepository.findByInquiryOrderByCreatedAtAsc(inquiry);
    }

    @Transactional
    public InquiryMessage addMessage(Long inquiryId, String content) {
        User currentUser = userService.getCurrentUser();
        MedicationInquiry inquiry = getInquiryById(inquiryId);

        // If this is a pharmacist's first response, mark the inquiry as responded
        if ("PHARMACIST".equals(currentUser.getRole())) {
            if (InquiryStatus.PENDING.equals(inquiry.getStatus())) {
                inquiry.setStatus(InquiryStatus.RESPONDED);
            }
            if (!inquiry.getRespondingPharmacies().contains(currentUser)) {
                inquiry.getRespondingPharmacies().add(currentUser);
            }
            inquiryRepository.save(inquiry);
        }

        InquiryMessage message = InquiryMessage.builder()
                .content(content)
                .sender(currentUser)
                .inquiry(inquiry)
                .build();

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<MedicationInquiry> getInquiriesForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        if ("PHARMACIST".equals(currentUser.getRole())) {
            // Pharmacists see inquiries they've responded to
            return inquiryRepository.findByRespondingPharmaciesContainingOrderByCreatedAtDesc(currentUser);
        } else {
            // Regular users see their own inquiries
            return inquiryRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        }
    }

    @Transactional(readOnly = true)
    public List<MedicationInquiry> getPharmacistInquiries() {
        User currentUser = userService.getCurrentUser();
        if (!"PHARMACIST".equals(currentUser.getRole())) {
            throw new RuntimeException("Only pharmacists can access this endpoint");
        }
        
        // Get all non-closed inquiries, regardless of who has responded
        return inquiryRepository.findByStatusNotOrderByCreatedAtDesc(InquiryStatus.CLOSED);
    }

    @Transactional
    public InquiryMessage sendMessage(Long inquiryId, Long pharmacyId, String content) {
        User currentUser = userService.getCurrentUser();
        MedicationInquiry inquiry = getInquiryById(inquiryId);

        // Check if the current user is either the inquiry owner or the pharmacy
        boolean isOwner = inquiry.getUser().getEmail().equals(currentUser.getEmail());
        boolean isPharmacy = pharmacyId.equals(currentUser.getId()) && 
                           "PHARMACIST".equals(currentUser.getRole());

        if (!isOwner && !isPharmacy) {
            throw new RuntimeException("User does not have permission to send messages in this conversation");
        }

        // For pharmacists, ensure they are part of the conversation
        if ("PHARMACIST".equals(currentUser.getRole())) {
            // If they're not part of the conversation, try to add them
            if (!inquiry.getRespondingPharmacies().contains(currentUser)) {
                // Only allow joining if the inquiry is not closed
                if (!InquiryStatus.CLOSED.equals(inquiry.getStatus())) {
                    inquiry.getRespondingPharmacies().add(currentUser);
                    if (InquiryStatus.PENDING.equals(inquiry.getStatus())) {
                        inquiry.setStatus(InquiryStatus.RESPONDED);
                    }
                    inquiryRepository.save(inquiry);
                } else {
                    throw new RuntimeException("Cannot join a closed inquiry");
                }
            }
        }

        InquiryMessage message = new InquiryMessage();
        message.setInquiry(inquiry);
        message.setContent(content);
        message.setSender(currentUser);
        message.setCreatedAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    @Transactional
    public void closeInquiry(Long inquiryId) {
        User currentUser = userService.getCurrentUser();
        MedicationInquiry inquiry = getInquiryById(inquiryId);

        if (!"PHARMACIST".equals(currentUser.getRole())) {
            throw new RuntimeException("Only pharmacists can close inquiries");
        }

        if (!inquiry.getRespondingPharmacies().contains(currentUser)) {
            throw new RuntimeException("You don't have permission to close this inquiry");
        }

        inquiry.setStatus(InquiryStatus.CLOSED);
        inquiryRepository.save(inquiry);
    }

    @Transactional
    public MedicationInquiry addRespondingPharmacy(Long inquiryId) {
        User currentUser = userService.getCurrentUser();
        MedicationInquiry inquiry = getInquiryById(inquiryId);

        if (!"PHARMACIST".equals(currentUser.getRole())) {
            throw new RuntimeException("Only pharmacists can respond to inquiries");
        }

        if (InquiryStatus.CLOSED.equals(inquiry.getStatus())) {
            throw new RuntimeException("Cannot respond to a closed inquiry");
        }

        if (!inquiry.getRespondingPharmacies().contains(currentUser)) {
            inquiry.getRespondingPharmacies().add(currentUser);
            if (InquiryStatus.PENDING.equals(inquiry.getStatus())) {
                inquiry.setStatus(InquiryStatus.RESPONDED);
            }
            return inquiryRepository.save(inquiry);
        }

        return inquiry;
    }

    @Transactional
    public MedicationInquiry removeRespondingPharmacy(Long inquiryId) {
        User currentUser = userService.getCurrentUser();
        MedicationInquiry inquiry = getInquiryById(inquiryId);

        if (!"PHARMACIST".equals(currentUser.getRole())) {
            throw new RuntimeException("Only pharmacists can withdraw from inquiries");
        }

        if (InquiryStatus.CLOSED.equals(inquiry.getStatus())) {
            throw new RuntimeException("Cannot withdraw from a closed inquiry");
        }

        if (inquiry.getRespondingPharmacies().contains(currentUser)) {
            inquiry.getRespondingPharmacies().remove(currentUser);
            // If this was the last responding pharmacy and there are no messages, set status back to pending
            if (inquiry.getRespondingPharmacies().isEmpty() && messageRepository.countByInquiry(inquiry) == 0) {
                inquiry.setStatus(InquiryStatus.PENDING);
            }
            return inquiryRepository.save(inquiry);
        }

        return inquiry;
    }
}
