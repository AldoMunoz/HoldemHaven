package com.holdemhavenus.holdemhaven.repositories;

import com.holdemhavenus.holdemhaven.entities.DBHand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DBHandRepository extends JpaRepository<DBHand, Long> {
}
