package com.holdemhavenus.holdemhaven.controllers;


import com.holdemhavenus.holdemhaven.entities.UTHTable;
import com.holdemhavenus.holdemhaven.requestDTOs.PlayerActionRequest;
import com.holdemhavenus.holdemhaven.responseDTOs.DealHandResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.GetDealerHandResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.PlayerActionResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.ShowdownResponse;
import com.holdemhavenus.holdemhaven.services.UTHTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/table")
@SessionAttributes("table")
public class TableController {
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
        System.out.println(request.getAction());
        System.out.println(request.getBetAmount());
        return UTHTableService.playerAction(UTHTable, request);
    }

    @PostMapping("/showdown")
    public ShowdownResponse showdown(@ModelAttribute("table") UTHTable UTHTable) {
        return UTHTableService.showdown(UTHTable);
    }

    @PostMapping("/get-dealer-hand")
    public GetDealerHandResponse getDealerHand(@ModelAttribute("table") UTHTable UTHTable) {
        return UTHTableService.getDealerHand(UTHTable);
    }

    @PostMapping("/end-hand")
    public void endHand(@ModelAttribute("table") UTHTable UTHTable) {
        UTHTableService.endHand(UTHTable);
    }
}
