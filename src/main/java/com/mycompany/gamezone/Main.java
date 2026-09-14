package com.mycompany.gamezone;

import com.mycompany.gamezone.persistence.ProductRepository;
import com.mycompany.gamezone.persistence.SaleRepository;
import com.mycompany.gamezone.service.PersonService;
import com.mycompany.gamezone.service.ProductService;
import com.mycompany.gamezone.service.SaleService;
import com.mycompany.gamezone.ui.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        ProductRepository productRepository = new ProductRepository("data/products.txt");
        ProductService productService = new ProductService(productRepository);

        PersonService personService = new PersonService(); // ⚠️ confirmar constructor real

        SaleRepository saleRepository = new SaleRepository();
        SaleService saleService = new SaleService(saleRepository);

        ConsoleMenu menu = new ConsoleMenu(productService, personService, saleService);
        menu.start();
    }
}