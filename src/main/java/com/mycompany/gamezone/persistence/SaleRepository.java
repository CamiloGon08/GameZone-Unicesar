package com.mycompany.gamezone.persistence;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;

public class SaleRepository {
    private static final String FILE_PATH = "data/sales.txt";

    private String toLine(Sale sale) {
        StringBuilder productNames = new StringBuilder();
        List<Product> products = sale.getProducts();
        for (int i = 0; i < products.size(); i++) {
            if (i > 0) {
                productNames.append(";");
            }
            productNames.append(products.get(i).getTitle());
        }
        return sale.getId() + "," + sale.getDate() + ","
                + sale.getCustomer().getName() + "," + sale.getSeller().getName() +
                "," + productNames + "," + sale.getTotal();
    }

    public void save(List<Sale> sales) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Sale sale : sales) {
                writer.write(toLine(sale));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving sales: " + e.getMessage());
        }
    }

    public List<Sale> load() {
        return new ArrayList<>();
    }
}