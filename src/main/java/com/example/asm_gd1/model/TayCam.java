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
    private LocalDateTime releaseDate;  // ngày mở bán

    @Column(name = "preorder_discount")
    private Double preorderDiscount; // phần trăm giảm, ví dụ 10.0 = 10%

    // 1 tay cầm có thể có nhiều đánh giá
    @OneToMany(mappedBy = "tayCam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DanhGia> danhGias = new ArrayList<>();

    // 1 tay cầm có thể có nhiều đơn đặt trước
    @OneToMany(mappedBy = "tayCam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DatTruoc> datTruocs = new ArrayList<>();
}
