package com.pharmasearch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private Long userId;
    private String userRole;
    private String name;
    private String email;
}
