package com.pranjal.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {
    @NotBlank(message = "Password should not be blank")
    @Length(min = 6, message = "Minimum password length is 6")
    private String oldPassword;

    @NotBlank(message = "Password should not be blank")
    @Length(min = 6, message = "Minimum password length is 6")
    private String newPassword;
}
