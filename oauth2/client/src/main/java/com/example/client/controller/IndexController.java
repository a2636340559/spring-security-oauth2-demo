package com.example.client.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("msg","欢迎，" +
                SecurityContextHolder.getContext().getAuthentication().getName() + "，登录系统A！");
        return "index";
    }
}
