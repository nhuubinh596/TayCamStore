package com.example.asm_gd1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message="Tài khoản không được trống")
    @Column(name = "username")
    private String username;

    @NotBlank(message="Tài khoản không được trống")
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "hoTen")
    private String hoTen;

    @Column(name = "soDienThoai")
    private String soDienThoai;

    @Column(name = "email")
    private String email;

    @Column(name = "diaChi")
    private String diaChi;

}