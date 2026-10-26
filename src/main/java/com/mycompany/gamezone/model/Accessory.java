package com.mycompany.gamezone.model;

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

    /** {@inheritDoc} */
    @Override
    public boolean matchesCategory(String category) {
        return "ACCESSORY".equals(category);
    }

    /**
     * Returns the accessory type label used by the existing type filter.
     *
     * @return Controller, Cable or Memory for the current accessory families
     */
    public abstract String getTypeName();
}
