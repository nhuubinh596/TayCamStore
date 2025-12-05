package com.example.asm_gd1.controller;

import com.example.asm_gd1.model.CartItem;
import com.example.asm_gd1.model.DatTruoc;
import com.example.asm_gd1.model.DatTruocItem;
import com.example.asm_gd1.model.TayCam;
import com.example.asm_gd1.model.User;
import com.example.asm_gd1.repository.DatTruocRepository;
import com.example.asm_gd1.repository.TayCamRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private static final String CART_SESSION_KEY = "cartItems";

    @Autowired
    private TayCamRepository tayCamRepository;

    @Autowired
    private DatTruocRepository datTruocRepository;

    @Value("${preorder.save.enabled:true}")
    private boolean preorderSaveEnabled;

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        Object o = session.getAttribute(CART_SESSION_KEY);
        if (o instanceof List) return (List<CartItem>) o;
        List<CartItem> list = new ArrayList<>();
        session.setAttribute(CART_SESSION_KEY, list);
        return list;
    }

    private int computeTotalQty(List<CartItem> items) {
        return items.stream().mapToInt(i -> Math.max(i.getSoLuong(), 0)).sum();
    }

    private BigDecimal computeSubTotal(List<CartItem> items) {
        return items.stream()
                .map(i -> i.getThanhTien() != null ? i.getThanhTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Optional<CartItem> findItem(List<CartItem> items, Integer id) {
        return items.stream().filter(i -> Objects.equals(i.getMaTayCam(), id)).findFirst();
    }

    @PostMapping("/add/{id}")
    @ResponseBody
    public Map<String, Object> addToCart(@PathVariable("id") Integer id,
                                         @RequestParam(value = "qty", required = false, defaultValue = "1") int qty,
                                         HttpSession session) {
        Map<String, Object> resp = new HashMap<>();
        Optional<TayCam> opt = tayCamRepository.findById(id);
        if (!opt.isPresent()) {
            resp.put("ok", false);
            resp.put("error", "Sản phẩm không tồn tại");
            return resp;
        }

        TayCam tc = opt.get();

        double originalPrice = tc.getGia() != null ? tc.getGia() : 0.0;
        double finalPrice = originalPrice;

        if (tc.getReleaseDate() != null && tc.getReleaseDate().isAfter(LocalDateTime.now())) {
            double discountPct = tc.getPreorderDiscount() != null ? tc.getPreorderDiscount() : 0.0;
            if (discountPct > 0.0) {
                finalPrice = originalPrice * (1.0 - discountPct / 100.0);
            }
        }

        BigDecimal price = BigDecimal.valueOf(finalPrice);

        List<CartItem> cart = getCart(session);

        Optional<CartItem> existing = findItem(cart, id);
        if (existing.isPresent()) {
            CartItem it = existing.get();
            int newQty = Math.max(0, it.getSoLuong() + qty);
            it.setSoLuong(newQty);
        } else {
            CartItem it = new CartItem(tc.getMaTayCam(), tc.getTenTayCam(), tc.getHinhAnh(), price, Math.max(1, qty));
            cart.add(it);
        }

        session.setAttribute(CART_SESSION_KEY, cart);
        resp.put("ok", true);
        resp.put("count", computeTotalQty(cart));
        return resp;
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        model.addAttribute("cartItems", cart);
        model.addAttribute("cartTotalQty", computeTotalQty(cart));
        model.addAttribute("cartSubTotal", computeSubTotal(cart));
        Object usr = session.getAttribute("user");
        if (usr instanceof User) model.addAttribute("user", (User) usr);
        return "cart/index";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("productId") Integer productId,
                             @RequestParam("quantity") Integer quantity,
                             HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (productId != null && quantity != null) {
            for (CartItem it : cart) {
                if (Objects.equals(it.getMaTayCam(), productId)) {
                    it.setSoLuong(Math.max(0, quantity));
                    break;
                }
            }
            cart.removeIf(i -> i.getSoLuong() <= 0);
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return "redirect:/cart";
    }

    @PostMapping("/delete/{id}")
    public String deleteItem(@PathVariable("id") Integer id, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(i -> Objects.equals(i.getMaTayCam(), id));
        session.setAttribute(CART_SESSION_KEY, cart);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutForm(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        if (cart == null || cart.isEmpty()) {
            model.addAttribute("cartEmpty", true);
            return "cart/confirm";
        }
        model.addAttribute("cartItems", cart);
        model.addAttribute("totalQty", computeTotalQty(cart));
        model.addAttribute("total", computeSubTotal(cart));
        Object usr = session.getAttribute("user");
        if (usr instanceof User) model.addAttribute("user", (User) usr);
        return "cart/confirm";
    }

    @Transactional
    @PostMapping("/place")
    public String placeOrderGuest(@RequestParam Map<String, String> form, HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        if (!preorderSaveEnabled) {
            session.removeAttribute(CART_SESSION_KEY);
            return "redirect:/cart/success";
        }

        try {
            DatTruoc order = new DatTruoc();
            String customerName = Optional.ofNullable(form.get("customerName")).orElse("").trim();
            if (customerName.isEmpty()) customerName = "Khách lẻ";
            order.setTenKhachHang(customerName);

            String phone = Optional.ofNullable(form.get("phone")).orElse("").trim();
            if (!isValidPhone(phone)) phone = "000000000";
            order.setSoDienThoai(phone);

            String address = Optional.ofNullable(form.get("address")).orElse("").trim();
            if (address.isEmpty()) address = "Chưa cập nhật";
            order.setDiaChi(address);

            String email = Optional.ofNullable(form.get("email")).orElse("").trim();
            if (!isValidEmail(email)) email = "noemail@example.com";
            order.setEmail(email);

            order.setTrangThai("PREORDER");
            order.setNgayDat(LocalDateTime.now());

            BigDecimal total = BigDecimal.ZERO;
            for (CartItem ci : cart) {
                DatTruocItem it = new DatTruocItem();
                tayCamRepository.findById(ci.getMaTayCam()).ifPresent(it::setTayCam);

                it.setTenSanPham(Optional.ofNullable(ci.getTenTayCam()).filter(s->!s.isEmpty())
                        .orElseGet(() -> it.getTayCam()!=null ? it.getTayCam().getTenTayCam() : "Sản phẩm"));

                int qty = Math.max(ci.getSoLuong(), 0);
                it.setSoLuong(qty);

                double price = ci.getDonGia() != null ? ci.getDonGia().doubleValue() : (ci.getThanhTien()!=null && qty>0 ? ci.getThanhTien().doubleValue()/qty : 0.0);
                it.setDonGia(price);

                it.setDatTruoc(order);
                order.getItems().add(it);

                total = total.add(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(qty)));
            }

            order.setGiaDatTruoc(total.doubleValue());
            datTruocRepository.save(order);
            log.debug("Saved preorder (guest) id={} total={}", order.getId(), order.getGiaDatTruoc());
        } catch (Exception ex) {
            log.error("Error saving preorder (guest): ", ex);
            return "redirect:/cart";
        }

        session.removeAttribute(CART_SESSION_KEY);
        return "redirect:/cart/success";
    }

    @Transactional
    @PostMapping("/confirm")
    public String confirmOrderLogged(HttpSession session) {
        Object usr = session.getAttribute("user");
        if (usr == null) return "redirect:/login";

        List<CartItem> cart = getCart(session);
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        if (!preorderSaveEnabled) {
            session.removeAttribute(CART_SESSION_KEY);
            return "redirect:/cart/success";
        }

        try {
            DatTruoc order = new DatTruoc();

            if (usr instanceof User) {
                order.setUser((User) usr);
            }

            String fullname = firstNonEmpty(
                    getUserPropertyAsString(usr, "fullName"),
                    getUserPropertyAsString(usr, "hoTen"),
                    getUserPropertyAsString(usr, "name"),
                    getUserPropertyAsString(usr, "username")
            );
            if (fullname.isEmpty() && usr instanceof User) fullname = ((User) usr).getUsername();
            if (fullname.isEmpty()) fullname = "Khách đăng nhập";
            order.setTenKhachHang(fullname);

            String email = firstNonEmpty(getUserPropertyAsString(usr, "email"));
            if (email.isEmpty() && usr instanceof User) {
                try { email = ((User) usr).getEmail(); } catch (Exception ignored) {}
            }
            if (!isValidEmail(email)) email = "noemail@example.com";
            order.setEmail(email);

            String phone = firstNonEmpty(getUserPropertyAsString(usr, "phone"), getUserPropertyAsString(usr, "sdt"));
            if (!isValidPhone(phone)) phone = "000000000";
            order.setSoDienThoai(phone);

            String address = firstNonEmpty(getUserPropertyAsString(usr, "address"), getUserPropertyAsString(usr, "diaChi"));
            if (address.isEmpty()) address = "Chưa cập nhật";
            order.setDiaChi(address);

            order.setTrangThai("PREORDER");
            order.setNgayDat(LocalDateTime.now());

            BigDecimal total = BigDecimal.ZERO;
            for (CartItem ci : cart) {
                DatTruocItem it = new DatTruocItem();
                tayCamRepository.findById(ci.getMaTayCam()).ifPresent(it::setTayCam);

                it.setTenSanPham(Optional.ofNullable(ci.getTenTayCam()).filter(s->!s.isEmpty())
                        .orElseGet(() -> it.getTayCam()!=null ? it.getTayCam().getTenTayCam() : "Sản phẩm"));

                int qty = Math.max(ci.getSoLuong(), 0);
                it.setSoLuong(qty);

                double price = ci.getDonGia() != null ? ci.getDonGia().doubleValue()
                        : (ci.getThanhTien()!=null && qty>0 ? ci.getThanhTien().doubleValue()/qty : 0.0);
                it.setDonGia(price);

                it.setDatTruoc(order);
                order.getItems().add(it);

                total = total.add(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(qty)));
            }

            order.setGiaDatTruoc(total.doubleValue());
            datTruocRepository.save(order);
            log.debug("Saved preorder (user) id={} total={}", order.getId(), order.getGiaDatTruoc());
        } catch (Exception ex) {
            log.error("Error saving preorder (user): ", ex);
            return "redirect:/cart";
        }

        session.removeAttribute(CART_SESSION_KEY);
        return "redirect:/cart/success";
    }

    @GetMapping("/success")
    public String successPage() {
        return "cart/success";
    }

    private String getUserPropertyAsString(Object obj, String propertyName) {
        if (obj == null) return "";
        try {
            BeanWrapper bw = new BeanWrapperImpl(obj);
            Object val = bw.getPropertyValue(propertyName);
            return val != null ? String.valueOf(val) : "";
        } catch (Exception ex) {
            return "";
        }
    }

    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return phone.matches("^[0-9]{9,11}$");
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private String firstNonEmpty(String... arr) {
        for (String s : arr) if (s != null && !s.trim().isEmpty()) return s.trim();
        return "";
    }
}