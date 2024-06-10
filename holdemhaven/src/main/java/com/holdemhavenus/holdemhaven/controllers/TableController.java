package com.holdemhavenus.holdemhaven.controllers;


import com.holdemhavenus.holdemhaven.entities.Table;
import com.holdemhavenus.holdemhaven.requestDTOs.PlayerActionRequest;
import com.holdemhavenus.holdemhaven.responseDTOs.DealHandResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.PlayerActionResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.ShowdownResponse;
import com.holdemhavenus.holdemhaven.services.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/table")
@SessionAttributes("table")
public class TableController {
    @Autowired
    private TableService tableService;

    @ModelAttribute("table")
    public Table table() {
        return tableService.getTable();
    }

    @PostMapping("/new-game")
    public String startNewGame(@ModelAttribute("table") Table table) {
        return "New game started";
    }

    @GetMapping
    public Table getTable(@ModelAttribute("table") Table table) {
        return table;
    }

    @PostMapping("/deal-hand")
    public DealHandResponse dealHand(@ModelAttribute("table") Table table) {
        return tableService.dealHoleCards(table);
    }

    @PostMapping("/player-action")
    public PlayerActionResponse playerAction(@ModelAttribute("table") Table table, @RequestBody PlayerActionRequest request) {
        System.out.println(request.getAction());
        System.out.println(request.getBetAmount());
        return tableService.playerAction(table, request);
    }

    @PostMapping("/showdown")
    public ShowdownResponse showdown(@ModelAttribute("table") Table table) {
        System.out.println("entered showdown response");
        return tableService.showdown(table);
    }
}
