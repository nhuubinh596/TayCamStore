package com.example.asm_gd1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String confirm;

    private String hoTen;
    private String soDienThoai;
    private String email;
    private String diaChi;

}
