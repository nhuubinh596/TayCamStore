package com.example.asm_gd1.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserProfileForm {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 50, message = "Họ tên từ 2–50 ký tự")
    private String hoTen;

    @NotBlank(message = "SĐT không được để trống")
    @Pattern(regexp = "^[0-9]{9,11}$", message = "SĐT phải 9–11 chữ số")
    private String soDienThoai;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String diaChi;

    private String username;

    private String password;

}