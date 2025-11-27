package com.example.asm_gd1.controller;

import com.example.asm_gd1.model.DatTruoc;
import com.example.asm_gd1.model.User;
import com.example.asm_gd1.repository.DatTruocItemRepository;
import com.example.asm_gd1.repository.DatTruocRepository;
import com.example.asm_gd1.repository.TayCamRepository;
import com.example.asm_gd1.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private TayCamRepository tayCamRepository;
    @Autowired private DatTruocRepository datTruocRepository;
    @Autowired private DatTruocItemRepository datTruocItemRepository;
    @Autowired private UserRepository userRepository;

    private boolean isAdmin(HttpSession session) {
        Object u = session.getAttribute("user");
        return (u instanceof User) && "ADMIN".equalsIgnoreCase(((User) u).getRole());
    }

    @GetMapping({"", "/", "/home"})
    public String adminHome(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/access-denied";
        model.addAttribute("tongTayCam", tayCamRepository.count());
        model.addAttribute("tongDonDatTruoc", datTruocRepository.count());
        return "admin/home";
    }

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/access-denied";

        Page<User> p = userRepository.findAll(
                PageRequest.of(Math.max(page,0), Math.max(size,1), Sort.by("id").ascending())
        );
        model.addAttribute("usersPage", p);
        return "admin/users";
    }

    @GetMapping("/users/{id}")
    public String userDetail(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/access-denied";

        var user = userRepository.findById(id).orElse(null);
        if (user == null) return "redirect:/admin/users";

        var orders = datTruocRepository.findAll().stream()
                .filter(o -> o.getEmail() != null && o.getEmail().equalsIgnoreCase(user.getEmail()))
                .toList();

        model.addAttribute("u", user);
        model.addAttribute("orders", orders);
        return "admin/user-detail";
    }


    @GetMapping("/orders")
    public String orders(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/access-denied";

        Page<DatTruoc> p = datTruocRepository.findAll(
                PageRequest.of(Math.max(page,0), Math.max(size,1),
                        org.springframework.data.domain.Sort.by("id").ascending())
        );

        model.addAttribute("ordersPage", p);
        return "admin/orders";
    }



    @GetMapping("/orders/view/{id}")
    public String orderDetail(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/access-denied";
        var order = datTruocRepository.findById(id).orElse(null);
        if (order == null) return "redirect:/admin/orders";

        var items = datTruocItemRepository.findByDatTruoc_Id(id);
        double total = items.stream().mapToDouble(it -> it.getDonGia() * it.getSoLuong()).sum();

        model.addAttribute("order", order);
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "admin/order-details";
    }


    /* ===== DELETE ORDER ===== */
    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/access-denied";
        datTruocRepository.deleteById(id);
        return "redirect:/admin/orders";
    }
}
