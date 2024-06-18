package com.holdemhavenus.holdemhaven.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicationController {
    @GetMapping("/cashier")
    public String redirectToCashierPage() {
        return "redirect:/cashier.html";
    }

    @GetMapping("/account")
    public String redirectToAccountPage() {
        return "redirect:/account.html";
    }

    @GetMapping("/home")
    public String redirectToHomePage() {
        return "redirect:/index.html";
    }
}
