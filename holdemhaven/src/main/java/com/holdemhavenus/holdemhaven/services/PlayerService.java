package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.entities.Player;
import com.holdemhavenus.holdemhaven.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //checks input fields before saving a new Player to the repository
    public Player registerNewUserAccount(Player user) {
        if(emailExists(user.getEmail())) {
            throw new RuntimeException("An account already exists with the email address: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return playerRepository.save(user);
    }

    //checks if a user has already registered with the input email
    private boolean emailExists(String email) {
        return playerRepository.findByEmail(email) != null;
    }
}
