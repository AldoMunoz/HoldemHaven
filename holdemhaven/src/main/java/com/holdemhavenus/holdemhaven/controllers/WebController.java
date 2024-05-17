package com.holdemhavenus.holdemhaven.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/start";
    }

    @GetMapping("/start")
    public String startApp(HttpSession session, Model model){
        model.addAttribute("sessionId", session.getId());
        return "index";
    }

    @GetMapping("/cashier")
    public String cashierPage(HttpSession session, Model model) {
        // Retrieve user details from the session
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("accountBalance", session.getAttribute("accountBalance"));
        return "cashier";
    }
}
