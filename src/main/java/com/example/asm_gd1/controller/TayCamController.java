package com.example.asm_gd1.controller;

import com.example.asm_gd1.model.DanhGia;
import com.example.asm_gd1.model.TayCam;
import com.example.asm_gd1.repository.DanhGiaRepository;
import com.example.asm_gd1.repository.TayCamRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/tay-cam")
public class TayCamController {

    @Autowired
    private TayCamRepository tcrp;

    @Autowired
    private DanhGiaRepository dgrp;

    @GetMapping("/home")
    public String home(
            Model model,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session) {

        Pageable pageable = PageRequest.of(Math.max(0, page), 100);

        Page<TayCam> data = (name == null || name.isBlank())
                ? tcrp.findAll(pageable)
                : tcrp.findByTenTayCamContainingIgnoreCase(name, pageable);

        List<TayCam> list = data.getContent();

        Map<Integer, Double> avgRatings = tinhTrungBinhDanhGia(list);

        model.addAttribute("listTayCam", list);
        model.addAttribute("avgRatings", avgRatings);
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("cart", session.getAttribute("cartItems"));

        System.out.println("DEBUG user: " + session.getAttribute("user"));
        System.out.println("DEBUG cartItems: " + session.getAttribute("cartItems"));

        for (TayCam tc : list) {
            String img = tc.getHinhAnh();
            if (img == null || img.isBlank()) {
                tc.setHinhAnh("/images/placeholder.png");
            } else {
                if (img.startsWith("/uploads") || img.startsWith("/images") || img.startsWith("http")) {
                    // keep
                } else if (img.startsWith("\\")) {
                    tc.setHinhAnh("/images/" + img.substring(1));
                } else {
                    tc.setHinhAnh("/images/" + img);
                }
            }
        }

        return "tay-cam-list";
    }

    @PostMapping("/add")
    public String themTayCam(@ModelAttribute TayCam tayCam,
                             @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + Paths.get(imageFile.getOriginalFilename()).getFileName().toString();
                Path uploadDir = Paths.get("uploads");
                if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
                Files.copy(imageFile.getInputStream(),
                        uploadDir.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);
                tayCam.setHinhAnh("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        tcrp.save(tayCam);
        return "redirect:/tay-cam/home";
    }

    @GetMapping("/chi-tiet/{id}")
    public String xemChiTietTayCam(@PathVariable("id") Integer id,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   Model model,
                                   HttpSession session) {
        Optional<TayCam> opt = tcrp.findById(id);
        if (!opt.isPresent()) {
            return "redirect:/tay-cam/home";
        }
        TayCam tc = opt.get();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<DanhGia> dgPage = dgrp.findByTayCam_MaTayCam(tc.getMaTayCam(), pageable);

        model.addAttribute("tc", tc);
        model.addAttribute("dsDanhGia", dgPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", dgPage.getTotalPages());
        model.addAttribute("totalItems", dgPage.getTotalElements());

        Double avg = dgrp.findAverageRatingByTayCamId(tc.getMaTayCam());
        model.addAttribute("avgRating", avg != null ? avg : 0.0);

        Double discountPct = tc.getPreorderDiscount() != null ? tc.getPreorderDiscount() : 0.0;
        double giaGoc = tc.getGia() != null ? tc.getGia() : 0.0;
        double giaUuDai = giaGoc;
        boolean truocMoBan = false;
        String releaseText = null;

        if (tc.getReleaseDate() != null) {
            LocalDateTime now = LocalDateTime.now();
            truocMoBan = tc.getReleaseDate().isAfter(now);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            releaseText = tc.getReleaseDate().format(fmt);
        }

        if (truocMoBan && discountPct > 0) {
            giaUuDai = giaGoc * (1.0 - discountPct / 100.0);
        }

        model.addAttribute("priceOriginal", giaGoc);
        model.addAttribute("priceDisplayed", giaUuDai);
        model.addAttribute("preorderDiscountPct", discountPct);
        model.addAttribute("truocMoBan", truocMoBan);
        model.addAttribute("releaseText", releaseText);
        model.addAttribute("cart", session.getAttribute("cartItems"));
        model.addAttribute("user", session.getAttribute("user"));

        return "tay-cam-details";
    }

    @GetMapping("/edit/{maTayCam}")
    public String hienThiFormEdit(@PathVariable("maTayCam") Integer maTayCam, Model model) {
        TayCam tayCam = tcrp.findById(maTayCam).orElse(null);
        model.addAttribute("tayCam", tayCam);
        return "tay-cam-edit";
    }

    @PostMapping("/update")
    public String capNhatTayCam(@ModelAttribute TayCam tayCam,
                                @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            if (!imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + Paths.get(imageFile.getOriginalFilename()).getFileName().toString();
                Path uploadDir = Paths.get("uploads");
                if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
                Files.copy(imageFile.getInputStream(),
                        uploadDir.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);
                tayCam.setHinhAnh("/uploads/" + fileName);
            } else {
                TayCam tayCamCu = tcrp.findById(tayCam.getMaTayCam()).orElse(null);
                if (tayCamCu != null) tayCam.setHinhAnh(tayCamCu.getHinhAnh());
            }
            tcrp.save(tayCam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/tay-cam/home";
    }

    @GetMapping("/search/by-price")
    public String timKiemTheoKhoangGia(Model model,
                                       @RequestParam(required = false) Double minPrice,
                                       @RequestParam(required = false) Double maxPrice,
                                       @RequestParam(defaultValue = "0") int page) {

        int pageSize = 8;
        Pageable pageable = PageRequest.of(Math.max(0, page), pageSize, Sort.by(Sort.Direction.ASC, "gia"));

        Page<TayCam> pageResult;
        if (minPrice == null && maxPrice == null) {
            pageResult = tcrp.findAll(pageable);
        } else {
            double min = (minPrice == null) ? 0.0 : minPrice;
            double max = (maxPrice == null) ? Double.MAX_VALUE : maxPrice;
            pageResult = tcrp.findByGiaBetween(min, max, pageable); // đây gọi đúng signature
        }

        List<TayCam> pageList = pageResult.getContent();

        Map<Integer, Double> avgRatings = tinhTrungBinhDanhGia(pageList);

        model.addAttribute("listTayCam", pageList);
        model.addAttribute("avgRatings", avgRatings);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("currentPage", pageResult.getNumber());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("tayCam", new TayCam());

        String currentUrl = buildCurrentUrl();
        model.addAttribute("currentUrl", currentUrl);
        model.addAttribute("separator", currentUrl.contains("?") ? "&" : "?");

        return "tay-cam-list";
    }

    @GetMapping("/search/advanced")
    public String searchAdvanced(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        List<TayCam> all = tcrp.findAll();

        Stream<TayCam> s = all.stream();
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.toLowerCase();
            s = s.filter(tc -> tc.getTenTayCam() != null && tc.getTenTayCam().toLowerCase().contains(k));
        }
        if (minPrice != null) s = s.filter(tc -> tc.getGia() != null && tc.getGia() >= minPrice);
        if (maxPrice != null) s = s.filter(tc -> tc.getGia() != null && tc.getGia() <= maxPrice);

        List<TayCam> filtered = s.collect(Collectors.collectingAndThen(
                Collectors.toMap(TayCam::getMaTayCam, Function.identity(), (a, b)->a),
                m -> new ArrayList<>(m.values())
        ));

        Map<Integer, Double> avgRatings = tinhTrungBinhDanhGia(filtered);
        if (minRating != null) {
            filtered = filtered.stream()
                    .filter(tc -> avgRatings.getOrDefault(tc.getMaTayCam(), 0.0) >= minRating)
                    .collect(Collectors.toList());
        }

        if ("priceAsc".equals(sort)) filtered.sort(Comparator.comparing(TayCam::getGia, Comparator.nullsLast(Double::compareTo)));
        else if ("priceDesc".equals(sort)) filtered.sort(Comparator.comparing(TayCam::getGia, Comparator.nullsLast(Double::compareTo)).reversed());

        int pageSize = 8;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, filtered.size());
        List<TayCam> pageList = start >= filtered.size() ? Collections.emptyList() : filtered.subList(start, end);
        Page<TayCam> finalPage = new PageImpl<>(pageList, PageRequest.of(page, pageSize), filtered.size());

        model.addAttribute("listTayCam", finalPage.getContent());
        model.addAttribute("avgRatings", avgRatings);
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("minRating", minRating);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", finalPage.getTotalPages());
        model.addAttribute("totalItems", finalPage.getTotalElements());
        model.addAttribute("tayCam", new TayCam());

        String currentUrl = buildCurrentUrl();
        model.addAttribute("currentUrl", currentUrl);
        model.addAttribute("separator", currentUrl.contains("?") ? "&" : "?");

        return "tay-cam-list";
    }

    private Map<Integer, Double> tinhTrungBinhDanhGia(List<TayCam> tayCams) {
        Map<Integer, Double> avgRatings = new HashMap<>();
        for (TayCam tc : tayCams) {
            if (tc == null || tc.getMaTayCam() == null) continue;
            Double avg = dgrp.findAverageRatingByTayCamId(tc.getMaTayCam());
            avgRatings.put(tc.getMaTayCam(), avg != null ? avg : 0.0);
        }
        return avgRatings;
    }

    private String buildCurrentUrl() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr == null) return "/tay-cam/home";
        String uri = attr.getRequest().getRequestURI();
        String query = attr.getRequest().getQueryString();
        if (query == null) return uri;
        return uri + "?" + query.replaceAll("&?page=\\d+", "");
    }
}
