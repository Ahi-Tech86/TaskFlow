package com.ahicode.TextMe.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryUserDto {
    private String email;
    private String nickname;
    private String firstname;
    private String lastname;
    private String password;
    private String confirmationCode;
}
