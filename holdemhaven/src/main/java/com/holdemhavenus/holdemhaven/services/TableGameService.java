package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.entities.UTHTable;
import com.holdemhavenus.holdemhaven.responseDTOs.DealHandResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.GetDealerHandResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.ShowdownResponse;

//Interface includes methods that can be used for a variety of table games including:
//Blackjack, Three Card Poker, Spanish 21, etc.,
//which can be implemented into the program in the future.
public interface TableGameService {
    DealHandResponse dealHoleCards(UTHTable UTHTable);
    ShowdownResponse showdown(UTHTable UTHTable);
    void endHand(UTHTable UTHTable);
}
