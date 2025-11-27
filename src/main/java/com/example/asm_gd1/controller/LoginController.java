package com.example.asm_gd1.controller;

import com.example.asm_gd1.model.User;
import com.example.asm_gd1.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false) String error, Model model) {
        model.addAttribute("error", error != null);
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session) {

        if(username == null || username.isBlank() ||
                password == null || password.isBlank()){
            return "redirect:/login?error=empty";
        }

        User userEntity = userRepo.findByUsername(username).orElse(null);

        if (userEntity == null) {
            return "redirect:/login?error=user-not-found";
        }

        if(!password.equals(userEntity.getPassword())) {
            return "redirect:/login?error=wrong-pass";
        }

        session.setAttribute("user", userEntity);
        session.setAttribute("role", userEntity.getRole());

        if ("ADMIN".equals(userEntity.getRole())) {
            return "redirect:/admin/home";
        } else {
            return "redirect:/tay-cam/home";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
