package com.gamezone.model;

import com.mycompany.gamezone.model.Accesory;

public class Cable extends Accessory {

    private double lengthInMeters;
    private String connectorType;

    
    public Cable(String id, String title, double price, int stock,
                 double lengthInMeters, String connectorType) {
        super(id, title, price, stock);
        this.lengthInMeters = lengthInMeters;
        this.connectorType = connectorType;
    }

    
    @Override
    public String getDescription() {
        return String.format("%s [Cable] - Length: %.1fm, Connector: %s, Price: %.2f, Stock: %d",
                getTitle(), lengthInMeters, connectorType, getPrice(), getStock());
    }

    
    public double getLengthInMeters() {
        return lengthInMeters;
    }


    public void setLengthInMeters(double lengthInMeters) {
        this.lengthInMeters = lengthInMeters;
    }

    
    public String getConnectorType() {
        return connectorType;
    }


    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }
}