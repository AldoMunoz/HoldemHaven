package com.holdemhavenus.holdemhaven.repositories;

import com.holdemhavenus.holdemhaven.entities.DBHand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface DBHandRepository extends JpaRepository<DBHand, Long> {
    @Query("SELECT h FROM DBHand h WHERE h.playerId = :playerId ORDER BY h.handId DESC")
    ArrayList<DBHand> find100LatestHands(@Param("playerId") Long playerId);
}
