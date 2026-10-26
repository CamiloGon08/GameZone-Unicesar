package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Console;
import com.mycompany.gamezone.model.Product;

/** Keeps console record conversion in the persistence layer. */
public class ConsoleLineConverter implements ProductLineConverter {

    @Override
    public String getTypeKey() {
        return "CONSOLE";
    }

    @Override
    public boolean supports(Product product) {
        return product instanceof Console;
    }

    @Override
    public String toLine(Product product) {
        Console console = (Console) product;
        return getTypeKey() + "," + console.getId() + "," + console.getTitle() + ","
                + console.getPrice() + "," + console.getStock() + ","
                + console.getBrand() + "," + console.getModel() + ","
                + console.getGeneration();
    }

    @Override
    public Product toProduct(String id, String title, double price, int stock, String[] fields) {
        return new Console(id, title, price, stock, fields[5], fields[6], fields[7]);
    }
}
