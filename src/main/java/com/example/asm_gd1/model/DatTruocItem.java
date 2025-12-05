package com.example.asm_gd1.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="DatTruocItem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DatTruocItem {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private DatTruoc datTruoc;

    @ManyToOne
    @JoinColumn(name="tayCamId")
    private TayCam tayCam;

    @Column(name = "tenSanPham")
    private String tenSanPham;

    @Column(name = "donGia")
    private Double donGia;

    @Column(name = "soLuong")
    private Integer soLuong;
}