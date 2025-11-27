package com.example.asm_gd1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DatTruoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DatTruoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tenKhachHang")
    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 50, message = "Tên phải từ 2 - 50 ký tự")
    private String tenKhachHang;

    @Column(name = "soDienThoai")
    @NotBlank(message = "SĐT không được bỏ trống")
    @Pattern(regexp = "^[0-9]{9,11}$", message = "SĐT phải 9-11 số")
    private String soDienThoai;

    @Column(name = "email")
    @NotBlank(message = "Email không được bỏ trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Column(name = "diaChi")
    @NotBlank(message = "Địa chỉ không được bỏ trống")
    private String diaChi;

    @Column(name = "giaDatTruoc")
    private Double giaDatTruoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tayCamId")
    private TayCam tayCam;

    @ToString.Exclude
    @OneToMany(mappedBy = "datTruoc", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DatTruocItem> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "trangThai")
    private String trangThai;

    @Column(name = "ngayDat")
    private LocalDateTime ngayDat;
}
