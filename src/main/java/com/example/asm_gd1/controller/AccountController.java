package com.example.asm_gd1.controller;

import com.example.asm_gd1.dto.UserProfileForm;
import com.example.asm_gd1.model.User;
import com.example.asm_gd1.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // <— cần bean encoder
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping
    public String accountHome(HttpSession session, Model model){
        User u = (User) session.getAttribute("user");
        if(u == null) return "redirect:/login";
        model.addAttribute("user", u);
        return "account/account";
    }

    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        // Lấy user từ session hoặc security principal
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        UserProfileForm form = new UserProfileForm();
        form.setUsername(user.getUsername());
        form.setHoTen(user.getHoTen());
        form.setEmail(user.getEmail());
        form.setSoDienThoai(user.getSoDienThoai());
        form.setDiaChi(user.getDiaChi());

        model.addAttribute("form", form);
        model.addAttribute("securityForm", new SecurityForm());
        return "account/profile";
    }

    @PostMapping("/profile")
    public String saveProfile(@Valid @ModelAttribute("form") UserProfileForm form,
                              BindingResult br,
                              HttpSession session,
                              RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "account/profile";
        }

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        user.setHoTen(form.getHoTen());
        user.setEmail(form.getEmail());
        user.setSoDienThoai(form.getSoDienThoai());
        user.setDiaChi(form.getDiaChi());
        userRepo.save(user);

        session.setAttribute("user", user);
        ra.addFlashAttribute("msgSuccess", "Cập nhật thông tin thành công");
        return "redirect:/account/profile";
    }

    @PostMapping("/profile/security")
    public String updateSecurity(@RequestParam String currentPassword,
                                 @RequestParam(required = false) String newUsername,
                                 @RequestParam(required = false) String newPassword,
                                 @RequestParam(required = false) String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";

        User db = userRepo.findById(u.getId()).orElse(null);
        if (db == null) {
            ra.addFlashAttribute("error", "Không tìm thấy tài khoản.");
            return "redirect:/account/profile";
        }

        if (!passwordEncoder.matches(currentPassword, db.getPassword())) {
            ra.addFlashAttribute("error", "Mật khẩu hiện tại không đúng.");
            return "redirect:/account/profile";
        }

        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(db.getUsername())) {
            if (userRepo.existsByUsername(newUsername.trim())) {
                ra.addFlashAttribute("error", "Tên đăng nhập đã tồn tại.");
                return "redirect:/account/profile";
            }
            db.setUsername(newUsername.trim());
        }

        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 6) {
                ra.addFlashAttribute("error", "Mật khẩu mới tối thiểu 6 ký tự.");
                return "redirect:/account/profile";
            }
            if (!newPassword.equals(confirmPassword)) {
                ra.addFlashAttribute("error", "Mật khẩu nhập lại không khớp.");
                return "redirect:/account/profile";
            }
            db.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepo.save(db);
        session.setAttribute("user", db);
        ra.addFlashAttribute("success", "Đã cập nhật thông tin bảo mật.");
        return "redirect:/account/profile";
    }
}
