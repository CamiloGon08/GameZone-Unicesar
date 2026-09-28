package com.mycompany.gamezone.model;

public class Controller extends Accessory {

    private String connectionType;

    
    public Controller(String id, String title, double price, int stock, String connectionType) {
        super(id, title, price, stock);
        this.connectionType = connectionType;
    }

    
    @Override
    public String getDescription() {
        return String.format("%s [Controller] - Connection: %s, Price: %.2f, Stock: %d",
                getTitle(), connectionType, getPrice(), getStock());
    }

    
    public String getConnectionType() {
        return connectionType;
    }

    
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
}