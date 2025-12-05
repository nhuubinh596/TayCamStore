package com.example.asm_gd1.controller;

import com.example.asm_gd1.dto.RegisterForm;
import com.example.asm_gd1.model.User;
import com.example.asm_gd1.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserRepository userRepo, PasswordEncoder passwordEncoder){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegister(Model model){
        model.addAttribute("form", new RegisterForm());
        return "account/register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("form") RegisterForm form,
                             BindingResult br,
                             Model model){

        if(br.hasErrors()) return "account/register";

        if(!form.getPassword().equals(form.getConfirm())){
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "account/register";
        }

        if(userRepo.findByUsername(form.getUsername()).isPresent()){
            model.addAttribute("error", "Username đã tồn tại");
            return "account/register";
        }

        User u = new User();
        u.setUsername(form.getUsername());
        u.setPassword(passwordEncoder.encode(form.getPassword()));
        u.setRole("USER");
        u.setHoTen(form.getHoTen());
        u.setDiaChi(form.getDiaChi());
        u.setEmail(form.getEmail());
        u.setSoDienThoai(form.getSoDienThoai());

        userRepo.save(u);

        return "redirect:/login?success";
    }
}