package com.holdemhavenus.holdemhaven.responseDTOs;

import com.holdemhavenus.holdemhaven.entities.Card;
import com.holdemhavenus.holdemhaven.entities.Player;
import com.holdemhavenus.holdemhaven.requestDTOs.PlayerActionRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class PlayerActionResponse {
    private boolean isSuccess;
    private char street;
    private String message;
    private ArrayList<Card> boardCards;
    private ArrayList<Card> dealerHoleCards;

    public PlayerActionResponse(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public PlayerActionResponse() {
        boardCards = new ArrayList<>();
        dealerHoleCards = new ArrayList<>();
    }
}
