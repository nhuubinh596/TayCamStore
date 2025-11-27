package com.example.asm_gd1.dto;

import jakarta.validation.constraints.*;

public class CheckoutForm {
    @NotBlank(message = "Họ tên người nhận không được để trống")
    @Size(min = 2, max = 50, message = "Họ tên từ 2–50 ký tự")
    private String tenNhan;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{9,11}$", message = "SĐT phải 9–11 chữ số")
    private String sdtNhan;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String emailNhan;

    @NotBlank(message = "Địa chỉ nhận hàng không được để trống")
    private String diaChiNhan;

    // không bắt buộc
    private String ghiChu;

    // getters/setters
    public String getTenNhan() { return tenNhan; }
    public void setTenNhan(String tenNhan) { this.tenNhan = tenNhan; }
    public String getSdtNhan() { return sdtNhan; }
    public void setSdtNhan(String sdtNhan) { this.sdtNhan = sdtNhan; }
    public String getEmailNhan() { return emailNhan; }
    public void setEmailNhan(String emailNhan) { this.emailNhan = emailNhan; }
    public String getDiaChiNhan() { return diaChiNhan; }
    public void setDiaChiNhan(String diaChiNhan) { this.diaChiNhan = diaChiNhan; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
