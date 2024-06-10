package com.holdemhavenus.holdemhaven.responseDTOs;

import com.holdemhavenus.holdemhaven.entities.HandRanking;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShowdownResponse {
    private char winner;
    private int playerHandRanking;
    private int dealerHandRanking;
    private String playerHandToString;
    private String dealerHandToString;

    public ShowdownResponse(char winner, int playerHandRanking, int dealerHandRanking, String playerHandToString, String dealerHandToString) {
        this.winner = winner;
        this.playerHandRanking = playerHandRanking;
        this.dealerHandRanking = dealerHandRanking;
        this.playerHandToString = playerHandToString;
        this.dealerHandToString = dealerHandToString;
    }
}
