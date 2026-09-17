package com.gamezone.model;

import com.mycompany.gamezone.model.Accessory;

public class Memory extends Accessory {

    private int capacityInGb;
    private String memoryType;

    public Memory(String id, String title, double price, int stock,
                  int capacityInGb, String memoryType) {
        super(id, title, price, stock);
        this.capacityInGb = capacityInGb;
        this.memoryType = memoryType;
    }

    
    @Override
    public String getDescription() {
        return String.format("%s [Memory] - Capacity: %dGB, Type: %s, Price: %.2f, Stock: %d",
                getTitle(), capacityInGb, memoryType, getPrice(), getStock());
    }

   
    public int getCapacityInGb() {
        return capacityInGb;
    }

    
    public void setCapacityInGb(int capacityInGb) {
        this.capacityInGb = capacityInGb;
    }

    
    public String getMemoryType() {
        return memoryType;
    }

   
    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }
}