package com.ahicode.TextMe.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivateRegistrationRequest {
    private String email;
    @JsonProperty("confirmation_code")
    private String confirmationCode;
}
