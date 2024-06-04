package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.entities.Table;
import org.springframework.stereotype.Service;

@Service
public class TableService {
    private Table table;

    public TableService() {
        this.table = new Table();
    }

    public Table getTable() {
        return new Table();
    }

    public void startNewGame(Table table) {
        //todo
    }
}
