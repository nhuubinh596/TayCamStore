package com.example.asm_gd1.controller;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class SecurityForm {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
