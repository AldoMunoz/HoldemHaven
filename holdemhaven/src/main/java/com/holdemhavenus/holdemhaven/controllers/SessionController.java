package com.holdemhavenus.holdemhaven.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class  SessionController {
    @GetMapping("/get-player-info")
    public Map<String, Object> getPlayerInfo(HttpSession session) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("username", session.getAttribute("username"));
        attributes.put("accountBalance", session.getAttribute("accountBalance"));
        attributes.put("playerId", session.getAttribute("playerId"));

        return attributes;
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        //ends the session upon logout
        session.invalidate();

        //resets fields
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");

        return response;
    }
}
