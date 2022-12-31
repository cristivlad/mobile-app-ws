package com.example.mobileappws.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PasswordResetModel {
    private String token;
    private String password;
}
