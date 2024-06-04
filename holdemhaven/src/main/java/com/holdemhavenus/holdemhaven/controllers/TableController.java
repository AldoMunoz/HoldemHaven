package com.holdemhavenus.holdemhaven.controllers;


import com.holdemhavenus.holdemhaven.entities.Table;
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
        tableService.startNewGame(table);

        return "New game started";
    }

    @GetMapping
    public Table getTable(@ModelAttribute("table") Table table) {
        return table;
    }
}
