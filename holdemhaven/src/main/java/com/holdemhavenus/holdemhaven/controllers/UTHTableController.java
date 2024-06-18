package com.holdemhavenus.holdemhaven.controllers;


import com.holdemhavenus.holdemhaven.entities.UTHTable;
import com.holdemhavenus.holdemhaven.requestDTOs.PlayerActionRequest;
import com.holdemhavenus.holdemhaven.requestDTOs.SaveHandRequest;
import com.holdemhavenus.holdemhaven.responseDTOs.*;
import com.holdemhavenus.holdemhaven.services.UTHTableService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/table")
@SessionAttributes("table")
public class UTHTableController {
    @Autowired
    private UTHTableService UTHTableService;

    @ModelAttribute("table")
    public UTHTable table() {
        return UTHTableService.getTable();
    }

    @PostMapping("/new-game")
    public String startNewGame(@ModelAttribute("table") UTHTable UTHTable) {
        return "New game started";
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

    @PostMapping("/table/get-hand-history")
    public void getHandHistory(@ModelAttribute("table") UTHTable UTHTable, HttpSession session) {
        System.out.println(session.getAttribute("playerId"));
        //UTHTableService.getHandHistory(UTHTable, session.getAttribute("playerId"));
    }
}
