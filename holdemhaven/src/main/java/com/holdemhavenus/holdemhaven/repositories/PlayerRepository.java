package com.holdemhavenus.holdemhaven.repositories;

import com.holdemhavenus.holdemhaven.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUsername(String username);
    Player findByEmail(String email);
}
