package com.example.asm_gd1.controller;

import com.example.asm_gd1.model.TayCam;
import com.example.asm_gd1.model.User;
import com.example.asm_gd1.repository.TayCamRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@RequestMapping("/admin/tay-cam")
@Controller
public class AdminTayCamController {

    @Autowired private TayCamRepository tayCamRepository;

    private boolean isAdmin(HttpSession session){
        Object u = session.getAttribute("user");
        return (u instanceof User) && "ADMIN".equalsIgnoreCase(((User)u).getRole());
    }

    @GetMapping({"","/"})
    public String list(@RequestParam(defaultValue="0") int page,
                       @RequestParam(defaultValue="10") int size,
                       @RequestParam(name="q", required=false) String q,
                       HttpSession session, Model model){
        if(!isAdmin(session)) return "redirect:/access-denied";
        Pageable pageable = PageRequest.of(Math.max(0,page), Math.max(1,size),
                Sort.by(Sort.Direction.ASC, "maTayCam"));

        Page<TayCam> p = (q!=null && !q.trim().isEmpty())
                ? tayCamRepository.findByTenTayCamContainingIgnoreCase(q.trim(), pageable)
                : tayCamRepository.findAll(pageable);

        model.addAttribute("pageObj", p);
        model.addAttribute("ds", p.getContent());
        model.addAttribute("currentPage", p.getNumber());
        model.addAttribute("totalPages", p.getTotalPages());
        model.addAttribute("totalItems", p.getTotalElements());
        model.addAttribute("pageSize", p.getSize());
        model.addAttribute("q", q);
        return "admin/taycam-list";
    }

    @GetMapping("/add")
    public String addForm(HttpSession session, Model model){
        if(!isAdmin(session)) return "redirect:/access-denied";
        model.addAttribute("tc", new TayCam());
        return "admin/taycam-add";
    }

    @PostMapping("/add")
    public String doAdd(@Valid @ModelAttribute("tc") TayCam tc,
                        BindingResult br,
                        @RequestParam(value = "file", required = false) MultipartFile file,
                        HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/access-denied";
        if (br.hasErrors()) {
            // Giữ lại form + hiện lỗi ở th:errors
            return "admin/taycam-add";
        }
        String saved = saveImageIfAny(file);
        if (saved != null) tc.setHinhAnh(saved);
        tayCamRepository.save(tc);
        return "redirect:/admin/tay-cam";
    }

    @PostMapping("/edit/{id:\\d+}")
    public String doEdit(@PathVariable Integer id,
                         @Valid @ModelAttribute("tc") TayCam form,
                         BindingResult br,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                         HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/access-denied";

        TayCam existed = tayCamRepository.findById(id).orElse(null);
        if (existed == null) return "redirect:/admin/tay-cam";

        if (br.hasErrors()) {
            model.addAttribute("tc", form);
            return "admin/taycam-edit";
        }

        existed.setTenTayCam(form.getTenTayCam());
        existed.setHangSanXuat(form.getHangSanXuat());
        existed.setGia(form.getGia());
        existed.setReleaseDate(form.getReleaseDate());
        existed.setPreorderDiscount(form.getPreorderDiscount());

        String saved = saveImageIfAny(file);
        if (saved != null) existed.setHinhAnh(saved);

        tayCamRepository.save(existed);
        return "redirect:/admin/tay-cam";
    }

    @GetMapping("/edit/{id:\\d+}")
    public String editForm(@PathVariable Integer id, HttpSession session, Model model){
        if(!isAdmin(session)) return "redirect:/access-denied";
        var tc = tayCamRepository.findById(id).orElse(null);
        if(tc==null) return "redirect:/admin/tay-cam";
        model.addAttribute("tc", tc);
        return "admin/taycam-edit";
    }

    @GetMapping("/delete/{id:\\d+}")
    public String delete(@PathVariable Integer id, HttpSession session){
        if(!isAdmin(session)) return "redirect:/access-denied";
        tayCamRepository.deleteById(id);
        return "redirect:/admin/tay-cam";
    }

    private String saveImageIfAny(MultipartFile file){
        try{
            if(file==null || file.isEmpty()) return null;

            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            if(filename.isBlank()) return null;

            Path base = Paths.get("uploads").resolve("images");
            Files.createDirectories(base);
            Path dest = base.resolve(filename).normalize();

            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/images/" + filename;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }



}
