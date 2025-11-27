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

    // THÊM: encoder để verify mật khẩu hiện tại & encode mật khẩu mới
    @Autowired private PasswordEncoder passwordEncoder;

    // --- Các method cũ của bạn giữ nguyên ---
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
            return "redirect:/login"; // nếu chưa login
        }

        // map User -> UserProfileForm (tạo form object nếu cần)
        UserProfileForm form = new UserProfileForm();
        form.setUsername(user.getUsername());
        form.setHoTen(user.getHoTen());
        form.setEmail(user.getEmail());
        form.setSoDienThoai(user.getSoDienThoai());
        form.setDiaChi(user.getDiaChi());

        model.addAttribute("form", form);
        // tạo securityForm rỗng cho form đổi mật khẩu (nếu dùng)
        model.addAttribute("securityForm", new SecurityForm());
        return "account/profile";
    }

    @PostMapping("/profile")
    public String saveProfile(@Valid @ModelAttribute("form") UserProfileForm form,
                              BindingResult br,
                              HttpSession session,
                              RedirectAttributes ra) {
        if (br.hasErrors()) {
            // trả lại view — form lỗi sẽ hiển thị trong page
            return "account/profile";
        }

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // cập nhật thông tin và lưu
        user.setHoTen(form.getHoTen());
        user.setEmail(form.getEmail());
        user.setSoDienThoai(form.getSoDienThoai());
        user.setDiaChi(form.getDiaChi());
        userRepo.save(user);

        // cập nhật session
        session.setAttribute("user", user);
        ra.addFlashAttribute("msgSuccess", "Cập nhật thông tin thành công");
        return "redirect:/account/profile";
    }

    // --- THÊM: đổi username & password ---
    @PostMapping("/profile/security")
    public String updateSecurity(@RequestParam String currentPassword,
                                 @RequestParam(required = false) String newUsername,
                                 @RequestParam(required = false) String newPassword,
                                 @RequestParam(required = false) String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";

        // Lấy user mới nhất từ DB để đảm bảo đồng bộ
        User db = userRepo.findById(u.getId()).orElse(null);
        if (db == null) {
            ra.addFlashAttribute("error", "Không tìm thấy tài khoản.");
            return "redirect:/account/profile";
        }

        // Bắt buộc nhập mật khẩu hiện tại để xác thực
        if (!passwordEncoder.matches(currentPassword, db.getPassword())) {
            ra.addFlashAttribute("error", "Mật khẩu hiện tại không đúng.");
            return "redirect:/account/profile";
        }

        // Đổi username (nếu có nhập)
        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(db.getUsername())) {
            if (userRepo.existsByUsername(newUsername.trim())) {
                ra.addFlashAttribute("error", "Tên đăng nhập đã tồn tại.");
                return "redirect:/account/profile";
            }
            db.setUsername(newUsername.trim());
        }

        // Đổi password (nếu có nhập)
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
        session.setAttribute("user", db); // cập nhật lại session để header “Xin chào …” phản ánh ngay
        ra.addFlashAttribute("success", "Đã cập nhật thông tin bảo mật.");
        return "redirect:/account/profile";
    }
}
