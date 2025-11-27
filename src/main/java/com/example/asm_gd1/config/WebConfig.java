package com.example.asm_gd1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired(required=false)
    private LoginInterceptor loginInterceptor;

    @Autowired(required=false)
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        if (loginInterceptor != null) {
//            registry.addInterceptor(loginInterceptor)
//                    .addPathPatterns("/**")
//                    .excludePathPatterns("/login","/register","/css/**","/js/**","/images/**");
//        }
//        if (adminInterceptor != null) {
//            registry.addInterceptor(adminInterceptor)
//                    .addPathPatterns("/admin/**", "/admin/taycam/**")
//                    .excludePathPatterns(
//                            "/login", "/logout", "/", "/css/**", "/js/**", "/img/**", "/h2-console/**",
//                            "/cart/add/**"   // <-- thêm dòng này
//                    );
//
//        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // thư mục uploads đặt cùng cấp project (folder ngoài)
        String uploadPath = Paths.get("uploads").toAbsolutePath().toString().replace("\\", "/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/" + uploadPath + "/");

        // static resources
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }

}
