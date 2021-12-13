package com.ksiezyk.roommanagementsystem.data.datasource.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponseDto {
    private String email;
    private String token;
}
