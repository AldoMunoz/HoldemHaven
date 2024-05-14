package com.holdemhavenus.holdemhaven.controllers;

import com.holdemhavenus.holdemhaven.requestDTOs.LoginPlayerRequest;
import com.holdemhavenus.holdemhaven.requestDTOs.RegisterPlayerRequest;
import com.holdemhavenus.holdemhaven.entities.Player;
import com.holdemhavenus.holdemhaven.responseDTOs.LoginPlayerResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.RegisterPlayerResponse;
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
    public RegisterPlayerResponse registerPlayer(@RequestBody RegisterPlayerRequest request) {
        return playerService.verifyRegisterPlayerInformation(request);
    }

    @PostMapping("/signIn")
    public LoginPlayerResponse loginPlayer(@RequestBody LoginPlayerRequest request) {
        return playerService.verifySignInCredentials(request);
    }
}
