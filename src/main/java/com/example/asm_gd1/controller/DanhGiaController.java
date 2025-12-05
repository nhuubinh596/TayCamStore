package com.example.asm_gd1.controller;

import com.example.asm_gd1.model.DanhGia;
import com.example.asm_gd1.model.TayCam;
import com.example.asm_gd1.repository.DanhGiaRepository;
import com.example.asm_gd1.repository.TayCamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

@Controller
@RequestMapping("/danh-gia")
public class DanhGiaController {

    @Autowired
    private DanhGiaRepository dgrp;

    @Autowired
    private TayCamRepository tcrp;

    @PostMapping("/add")
    public String themDanhGia(@ModelAttribute DanhGia danhGia,
                              @RequestParam("maTayCam") Integer maTayCam) {
        TayCam tayCam = tcrp.findById(maTayCam).orElse(null);
        if (tayCam == null) {
            return "redirect:/tay-cam/home";
        }

        if (danhGia.getTenNguoiDanhGia() != null) danhGia.setTenNguoiDanhGia(danhGia.getTenNguoiDanhGia().trim());
        if (danhGia.getNoiDung() != null) danhGia.setNoiDung(danhGia.getNoiDung().trim());
        if (danhGia.getSoSaoDanhGia() == null) danhGia.setSoSaoDanhGia(5);
        else danhGia.setSoSaoDanhGia(Math.max(1, Math.min(5, danhGia.getSoSaoDanhGia())));

        danhGia.setTayCam(tayCam);
        dgrp.save(danhGia);

        int pageSize = 5;
        long total = dgrp.countByTayCam_MaTayCam(maTayCam);
        int lastPage = (int) ((total - 1) / pageSize);
        if (lastPage < 0) lastPage = 0;
        return "redirect:/tay-cam/chi-tiet/" + maTayCam + "?page=" + lastPage;
    }

    @GetMapping("/xoa/{id}")
    public String xoaDanhGia(@PathVariable("id") Integer id) {
        DanhGia dg = dgrp.findById(id).orElse(null);
        if (dg != null) {
            Integer maTayCam = dg.getTayCam().getMaTayCam();
            dgrp.deleteById(id);
            return "redirect:/tay-cam/chi-tiet/" + maTayCam;
        }
        return "redirect:/tay-cam/home";
    }

    @GetMapping("/edit/{id}")
    public String hienThiFormEdit(@PathVariable("id") Integer id, Model model) {
        DanhGia dg = dgrp.findById(id).orElse(null);
        model.addAttribute("danhGia", dg);
        return "danh-gia-edit";
    }

    @PostMapping("/update")
    public String capNhatDanhGia(@ModelAttribute DanhGia dg) {
        dgrp.save(dg);
        return "redirect:/tay-cam/chi-tiet/" + dg.getTayCam().getMaTayCam();
    }

    @GetMapping("/chi-tiet/{maTayCam}")
    public String xemChiTietTayCam(Model model,
                                   @PathVariable(name = "maTayCam") Integer maTayCam,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size) {
        TayCam tayCam = tcrp.findById(maTayCam).orElse(null);
        if (tayCam == null) return "redirect:/tay-cam/home";

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<DanhGia> danhGiaPage = dgrp.findByTayCam_MaTayCam(maTayCam, pageable);

        model.addAttribute("tc", tayCam);
        model.addAttribute("dsDanhGia", danhGiaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", danhGiaPage.getTotalPages());
        model.addAttribute("totalItems", danhGiaPage.getTotalElements());

        long releaseEpoch = 0L;
        boolean truocMoBan = false;
        if (tayCam.getReleaseDate() != null) {
            releaseEpoch = tayCam.getReleaseDate().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            truocMoBan = tayCam.getReleaseDate().isAfter(java.time.LocalDateTime.now());
        }
        model.addAttribute("releaseEpoch", releaseEpoch);
        model.addAttribute("truocMoBan", truocMoBan);

        Double raw = tayCam.getPreorderDiscount();
        double discountPercent = 0.0;
        if (raw != null) {
            discountPercent = raw > 1.0 ? raw / 100.0 : raw;
            discountPercent = Math.max(0.0, Math.min(1.0, discountPercent));
        }
        double giaUuDai = tayCam.getGia() == null ? 0.0 : tayCam.getGia();
        if (discountPercent > 0.0 && truocMoBan) giaUuDai = tayCam.getGia() * (1.0 - discountPercent);
        model.addAttribute("giaUuDai", giaUuDai);
        model.addAttribute("discountPercent", discountPercent);
        model.addAttribute("uuDaiText", "🎉 Ưu đãi đặt trước: Giảm " + (discountPercent * 100) + "% (chỉ còn " + giaUuDai + " VND)");

        // avg rating
        Double avg = dgrp.findAverageRatingByTayCamId(tayCam.getMaTayCam());
        model.addAttribute("avgRating", avg != null ? avg : 0.0);

        return "tay-cam-details";
    }
}