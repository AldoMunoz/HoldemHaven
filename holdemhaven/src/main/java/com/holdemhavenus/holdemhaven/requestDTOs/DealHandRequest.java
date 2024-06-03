package com.holdemhavenus.holdemhaven.requestDTOs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Getter
@Setter
public class DealHandRequest {
    private BigDecimal anteBetAmount;
    private BigDecimal tripsBetAmount;
}
