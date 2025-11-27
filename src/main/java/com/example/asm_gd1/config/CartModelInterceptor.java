package com.example.asm_gd1.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

public class CartModelInterceptor implements HandlerInterceptor {

    private static final String CART_SESSION_KEY = "cartItems";

    @SuppressWarnings("unchecked")
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null) return;

        HttpSession session = request.getSession(false);
        List<?> cart = null;
        if (session != null) {
            Object o = session.getAttribute(CART_SESSION_KEY);
            if (o instanceof List) cart = (List<?>) o;
        }
        if (cart == null) cart = new ArrayList<>();

        int totalQty = 0;
        BigDecimal subTotal = BigDecimal.ZERO;
        for (Object o : cart) {
            try {
                java.lang.reflect.Method mQty = o.getClass().getMethod("getSoLuong");
                Object qtyObj = mQty.invoke(o);
                int q = qtyObj != null ? ((Number) qtyObj).intValue() : 0;
                totalQty += q;

                java.lang.reflect.Method mThanhTien = null;
                try {
                    mThanhTien = o.getClass().getMethod("getThanhTien");
                } catch (NoSuchMethodException ex) {
                    // ignore
                }
                if (mThanhTien != null) {
                    Object t = mThanhTien.invoke(o);
                    if (t instanceof BigDecimal) subTotal = subTotal.add((BigDecimal) t);
                } else {
                    try {
                        java.lang.reflect.Method mDonGia = o.getClass().getMethod("getDonGia");
                        Object dg = mDonGia.invoke(o);
                        if (dg instanceof Number) {
                            BigDecimal price = BigDecimal.valueOf(((Number) dg).doubleValue());
                            subTotal = subTotal.add(price.multiply(BigDecimal.valueOf(q)));
                        } else if (dg instanceof BigDecimal) {
                            subTotal = subTotal.add(((BigDecimal) dg).multiply(BigDecimal.valueOf(q)));
                        }
                    } catch (NoSuchMethodException ignored) {}
                }
            } catch (Exception ignored) {}
        }

        modelAndView.addObject("cartTotalQty", totalQty);
        modelAndView.addObject("cartSubTotal", subTotal);
        modelAndView.addObject("cartItems", cart);
    }
}
