package com.holdemhavenus.holdemhaven.controllers;


import com.holdemhavenus.holdemhaven.entities.DBHand;
import com.holdemhavenus.holdemhaven.entities.UTHTable;
import com.holdemhavenus.holdemhaven.requestDTOs.PlayerActionRequest;
import com.holdemhavenus.holdemhaven.requestDTOs.SaveHandRequest;
import com.holdemhavenus.holdemhaven.responseDTOs.*;
import com.holdemhavenus.holdemhaven.services.UTHTableService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/table")
//Each table has its own session attribute "Table", which is a Table object
@SessionAttributes("table")
//Rest Controller used to call and return Table Service methods from the front-end to the back-end
public class UTHTableController {
    @Autowired
    private UTHTableService UTHTableService;

    @ModelAttribute("table")
    public UTHTable table() {
        return UTHTableService.getTable();
    }

    @GetMapping
    public UTHTable getTable(@ModelAttribute("table") UTHTable UTHTable) {
        return UTHTable;
    }

    @PostMapping("/deal-hand")
    public DealHandResponse dealHand(@ModelAttribute("table") UTHTable UTHTable) {
        return UTHTableService.dealHoleCards(UTHTable);
    }

    @PostMapping("/player-action")
    public PlayerActionResponse playerAction(@ModelAttribute("table") UTHTable UTHTable, @RequestBody PlayerActionRequest request) {
        return UTHTableService.playerAction(UTHTable, request);
    }

    @PostMapping("/fold-player-action")
    public FoldPlayerActionResponse foldPlayerAction(@ModelAttribute("table") UTHTable UTHTable, @RequestBody PlayerActionRequest request) {
        PlayerActionResponse paResponse = UTHTableService.playerAction(UTHTable, request);
        ShowdownResponse sResponse = UTHTableService.showdown(UTHTable);

        //when a player folds river, the playerAction and showdown are both called
        //their data is combined into FoldPlayerAction Response and sent to the front-end
        return new FoldPlayerActionResponse(paResponse.isSuccess(), paResponse.getMessage(), paResponse.getDealerHoleCards(),
                sResponse.getWinner(), sResponse.getPlayerHandRanking(), sResponse.getDealerHandRanking(), sResponse.getPlayerHandToString(), sResponse.getDealerHandToString());
    }

    @PostMapping("/showdown")
    public ShowdownResponse showdown(@ModelAttribute("table") UTHTable UTHTable) {
        return UTHTableService.showdown(UTHTable);
    }

    @PostMapping("/save-hand")
    public SaveHandResponse saveHand(@ModelAttribute("table") UTHTable UTHTable, @RequestBody SaveHandRequest request) {
        return UTHTableService.saveHand(UTHTable, request);
    }

    @PostMapping("/end-hand")
    public void endHand(@ModelAttribute("table") UTHTable UTHTable) {
        UTHTableService.endHand(UTHTable);
    }

    @PostMapping("/get-hand-history")
    public ArrayList<DBHand> getHandHistory(@ModelAttribute("table") UTHTable UTHTable, HttpSession session) {
        return UTHTableService.getHandHistory((Long) session.getAttribute("playerId"));
    }
}
