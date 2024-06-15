package com.holdemhavenus.holdemhaven.responseDTOs;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MoneyTransferResponse extends BaseResponse {
    private BigDecimal amount;

    public MoneyTransferResponse(boolean isSuccess, String message, BigDecimal amount) {
        super(isSuccess, message);
        this.amount = amount;
    }
    public MoneyTransferResponse(boolean isSuccess, String message) {
        super(isSuccess, message);
    }
}
