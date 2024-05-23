package com.holdemhavenus.holdemhaven.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    @GetMapping("/session-id")
    public String getSessionId(HttpSession session) {
        return (String) session.getAttribute("sessionId");
    }
}
