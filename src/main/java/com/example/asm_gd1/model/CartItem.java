package com.example.asm_gd1.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItem {
    private Integer maTayCam;
    private String tenTayCam;
    private String hinhAnh;
    private BigDecimal donGia;
    private int soLuong;
    private BigDecimal thanhTien;

    public CartItem() {}

    public CartItem(Integer maTayCam, String tenTayCam, String hinhAnh, BigDecimal donGia, int soLuong) {
        this.maTayCam = maTayCam;
        this.tenTayCam = tenTayCam;
        this.hinhAnh = hinhAnh;
        this.donGia = (donGia != null) ? donGia : BigDecimal.ZERO;
        this.soLuong = Math.max(soLuong, 0);
    }

    public BigDecimal getThanhTien() {
        BigDecimal price = (donGia != null) ? donGia : BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(Math.max(soLuong, 0)));
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }
}
