package com.pranjal.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private long id;
    private String name;
    private String email;
    private String role;
    private boolean isActive;
    private LocalDateTime createdAt;
}
