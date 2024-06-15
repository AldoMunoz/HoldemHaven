package com.holdemhavenus.holdemhaven.responseDTOs;

import com.holdemhavenus.holdemhaven.entities.Card;
import com.holdemhavenus.holdemhaven.entities.Player;
import com.holdemhavenus.holdemhaven.requestDTOs.PlayerActionRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class PlayerActionResponse extends BaseResponse {
    private char street;
    private ArrayList<Card> boardCards;
    private Card[] dealerHoleCards;

    public PlayerActionResponse(boolean isSuccess, String message) {
        super(isSuccess, message);
    }

    public PlayerActionResponse() {
        boardCards = new ArrayList<>();
        dealerHoleCards = new Card[2];
    }
}
