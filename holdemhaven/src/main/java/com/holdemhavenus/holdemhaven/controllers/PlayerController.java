package com.holdemhavenus.holdemhaven.controllers;

import com.holdemhavenus.holdemhaven.entities.Player;
import com.holdemhavenus.holdemhaven.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @PostMapping("/register")
    public Player registerUser(@RequestBody Player user) {
        return playerService.registerNewUserAccount(user);
    }
}
