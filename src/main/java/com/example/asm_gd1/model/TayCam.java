package com.example.asm_gd1.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TayCam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TayCam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maTayCam")
    private Integer maTayCam;

    @Column(name = "hinhAnh")
    private String hinhAnh;

    @Column(name = "tenTayCam")
    private String tenTayCam;

    @Column(name = "hangSanXuat")
    private String hangSanXuat;

    @Column(name = "gia")
    private Double gia;

    @Column(name = "soLuongTon")
    private Integer soLuongTon;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "releaseDate")
    private LocalDateTime releaseDate;

    @Column(name = "preorder_discount")
    private Double preorderDiscount;

    @OneToMany(mappedBy = "tayCam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DanhGia> danhGias = new ArrayList<>();

    @OneToMany(mappedBy = "tayCam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DatTruoc> datTruocs = new ArrayList<>();
}
