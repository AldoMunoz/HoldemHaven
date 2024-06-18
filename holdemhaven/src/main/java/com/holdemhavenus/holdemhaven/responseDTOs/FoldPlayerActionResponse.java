package com.holdemhavenus.holdemhaven.responseDTOs;

import com.holdemhavenus.holdemhaven.entities.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoldPlayerActionResponse extends BaseResponse {
    private Card[] dealerHoleCards;
    private char winner;
    private int playerHandRanking;
    private int dealerHandRanking;
    private String playerHandToString;
    private String dealerHandToString;

    public FoldPlayerActionResponse(boolean isSuccess, String message, Card[] dealerHoleCards, char winner, int playerHandRanking,
                                    int dealerHandRanking, String playerHandToString, String dealerHandToString) {
        super(isSuccess, message);
        this.dealerHoleCards = dealerHoleCards;
        this.winner = winner;
        this.playerHandRanking = playerHandRanking;
        this.dealerHandRanking = dealerHandRanking;
        this.playerHandToString = playerHandToString;
        this.dealerHandToString = dealerHandToString;
    }
}
