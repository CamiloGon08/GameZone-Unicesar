package com.mycompany.gamezone.ui;

import java.util.List;

import com.mycompany.gamezone.exceptions.GameZoneException;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.service.ProductService;

/**
 * Console options of the main menu that register and list video games and consoles.
 * All the messages shown to the user are written in Spanish.
 */
class ProductMenu {

    private final ProductService productService;
    private final ConsoleIO io;

    /**
     * Creates the menu with the collaborators it needs.
     *
     * @param productService service that handles product-related operations
     * @param io helper that reads the input and prints error messages
     */
    ProductMenu(ProductService productService,
                ConsoleIO io) {
        this.productService = productService;
        this.io = io;
    }

    void registerVideoGame() {
        System.out.print("ID: ");
        String id = io.readLine();
        System.out.print("Título: ");
        String title = io.readLine();
        System.out.print("Precio: ");
        double price = Double.parseDouble(io.readLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(io.readLine());
        System.out.print("Plataforma: ");
        String platform = io.readLine();
        System.out.print("Género: ");
        String genre = io.readLine();
        System.out.print("Clasificación de edad: ");
        String ageRating = io.readLine();

        try {
            productService.registerVideoGame(id, title, price, stock, platform, genre, ageRating);
            System.out.println("Videojuego registrado exitosamente.");
        } catch (GameZoneException | IllegalArgumentException e) {
            io.handleException(e);
        }
    }

    void registerConsole() {
        System.out.print("ID: ");
        String id = io.readLine();
        System.out.print("Título: ");
        String title = io.readLine();
        System.out.print("Precio: ");
        double price = Double.parseDouble(io.readLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(io.readLine());
        System.out.print("Marca: ");
        String brand = io.readLine();
        System.out.print("Modelo: ");
        String model = io.readLine();
        System.out.print("Generación: ");
        String generation = io.readLine();

        try {
            productService.registerConsole(id, title, price, stock, brand, model, generation);
            System.out.println("Consola registrada exitosamente.");
        } catch (GameZoneException | IllegalArgumentException e) {
            io.handleException(e);
        }
    }

    void listProducts() {
        List<Product> products = productService.listProducts();
        if (products.isEmpty()) {
            System.out.println("No hay productos registrados aún.");
            return;
        }
        System.out.println("--- Inventario de productos ---");
        for (Product product : products) {
            System.out.println(product.getDescription());
        }
    }
}
