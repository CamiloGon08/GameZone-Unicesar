package com.gamezone.model;


import com.mycompany.gamezone.model.Product;
import java.util.ArrayList;
import java.util.List;

public abstract class Accessory extends Product {

    private final List<String> compatibleConsoleIds;

    
    public Accessory(String id, String title, double price, int stock) {
        super(id, title, price, stock);
        this.compatibleConsoleIds = new ArrayList<>();
    }

    public void addCompatibleConsole(String consoleId) {
        if (!compatibleConsoleIds.contains(consoleId)) {
            compatibleConsoleIds.add(consoleId);
        }
    }

    public List<String> getCompatibleConsoleIds() {
        return new ArrayList<>(compatibleConsoleIds);
    }

   
    public boolean isCompatibleWith(String consoleId) {
        return compatibleConsoleIds.contains(consoleId);
    }
}