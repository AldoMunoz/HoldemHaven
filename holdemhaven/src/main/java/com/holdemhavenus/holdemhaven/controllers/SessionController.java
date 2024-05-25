package com.holdemhavenus.holdemhaven.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SessionController {

    @GetMapping("/session-id")
    public String getSessionId(HttpSession session) {
        System.out.println("Returning Session ID: " + session.getAttribute("sessionId"));
        return (String) session.getAttribute("sessionId");
    }

    @GetMapping("/session-username-accBal")
    public Map<String, Object> getUsernameAndAccountBalance(HttpSession session) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("username", session.getAttribute("username"));
        attributes.put("accountBalance", session.getAttribute("accountBalance"));

        return attributes;
    }
}
