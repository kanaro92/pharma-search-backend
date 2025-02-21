package com.pharmasearch.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PharmacyConversation {
    private Long pharmacyId;
    private String pharmacyName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long inquiryId;
}
