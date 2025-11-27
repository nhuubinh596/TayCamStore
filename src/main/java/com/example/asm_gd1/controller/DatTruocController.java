package com.example.asm_gd1.controller;

import com.example.asm_gd1.model.DatTruoc;
import com.example.asm_gd1.model.TayCam;
import com.example.asm_gd1.model.User;
import com.example.asm_gd1.repository.DatTruocRepository;
import com.example.asm_gd1.repository.TayCamRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/dat-truoc")
public class DatTruocController {

    @Autowired
    private DatTruocRepository dtrp;

    @Autowired
    private TayCamRepository tcrp;

    // ✅ Hiển thị form đặt trước
    @GetMapping("/form")
    public String hienThiFormDatTruoc(Model model,
                                      @RequestParam(value = "maTayCam", required = false) Integer maTayCam) {
        model.addAttribute("datTruocs", dtrp.findAll());
        model.addAttribute("tayCams", tcrp.findAll());
        model.addAttribute("datTruoc", new DatTruoc());

        if (maTayCam != null) {
            TayCam chonTayCam = tcrp.findById(maTayCam).orElse(null);
            model.addAttribute("chonTayCam", chonTayCam);
        }

        return "dat-truoc-form";
    }

    // ✅ Xử lý khi đặt hàng (chỉnh lại đoạn này)
    @PostMapping("/add")
    public String themDatTruoc(@ModelAttribute DatTruoc datTruoc,
                               @RequestParam("maTayCam") Integer maTayCam,
                               RedirectAttributes redirectAttributes) {

        TayCam tc = tcrp.findById(maTayCam).orElse(null);
        if (tc != null) {
            datTruoc.setTayCam(tc);

            if (tc.getReleaseDate() != null && tc.getReleaseDate().isAfter(LocalDateTime.now())) {
                double discount = (tc.getPreorderDiscount() != null) ? tc.getPreorderDiscount() : 0.0;
                datTruoc.setGiaDatTruoc(tc.getGia() * (1 - discount / 100));
            } else {
                datTruoc.setGiaDatTruoc(tc.getGia());
            }

            dtrp.save(datTruoc);

            // ✅ Thông báo thành công và quay lại trang chủ người dùng
            redirectAttributes.addFlashAttribute("successMessage", "🎉 Đặt hàng thành công!");
            return "redirect:/tay-cam/home";
        }

        // ❌ Nếu sản phẩm không tồn tại thì quay về form
        redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tay cầm!");
        return "redirect:/dat-truoc/form";
    }

    // Xoá 1 bản ghi (ADMIN hoặc chính chủ tuỳ bạn)
    @GetMapping("/xoa/{id}")
    public String delete(@PathVariable Integer id, HttpSession session) {
        Object u = session.getAttribute("user");
        if (!(u instanceof User)) return "redirect:/login";

        // đơn giản: cho ADMIN xoá, USER thì thôi (tuỳ bạn mở rộng)
        User user = (User) u;
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/dat-truoc/list";
        }

        dtrp.deleteById(id);
        return "redirect:/dat-truoc/list";
    }

    // ✅ Hiển thị form đặt trước theo mã tay cầm
    @GetMapping("/form/{id}")
    public String formDatTruocTheoTayCam(@PathVariable Integer id, Model model) {
        TayCam tc = tcrp.findById(id).orElse(null);
        if (tc == null) return "redirect:/tay-cam/home";

        // đảm bảo không null
        if (tc.getPreorderDiscount() == null) {
            tc.setPreorderDiscount(0.0);
        }

        DatTruoc datTruoc = new DatTruoc();
        datTruoc.setTayCam(tc);

        model.addAttribute("datTruoc", datTruoc);
        model.addAttribute("fixTayCam", tc);
        model.addAttribute("chonTayCam", tc); // truyền cả 2 tên để template an toàn
        return "dat-truoc-theo-tay-cam-form";
    }


    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       HttpSession session,
                       Model model) {

        Object u = session.getAttribute("user");
        if (!(u instanceof User)) return "redirect:/login";
        User user = (User) u;

        PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<com.example.asm_gd1.model.DatTruoc> p;

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            p = dtrp.findAllByOrderByIdDesc(pageable);
        } else {
            // USER: cần có email để lọc
            String email = user.getEmail();
            if (email == null || email.trim().isEmpty()) {
                return "redirect:/account/profile?needinfo=1";
            }
            p = dtrp.findByEmailOrderByIdDesc(email, pageable);
        }

        model.addAttribute("dsDatTruoc", p.getContent());
        model.addAttribute("currentPage", p.getNumber());
        model.addAttribute("totalPages", p.getTotalPages());
        model.addAttribute("totalItems", p.getTotalElements());

        return "dat-truoc-list"; // đúng với file bạn vừa gửi
    }

    @GetMapping("/dat-truoc-list")
    public String listForCurrentUser(HttpSession session, Model model) {
        Object u = session.getAttribute("user");
        if (!(u instanceof User)) return "redirect:/login";

        User user = (User) u;
        // nếu user chưa có email thì trả về trang profile để bổ sung
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return "redirect:/account/profile?needinfo=1";
        }

        model.addAttribute("items", dtrp.findByEmailOrderByIdDesc(user.getEmail()));
        return "dat-truoc-list"; // templates/dat-truoc-list.html
    }
}
