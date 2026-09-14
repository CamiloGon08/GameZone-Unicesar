package com.mycompany.gamezone.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.service.PersonService;
import com.mycompany.gamezone.service.ProductService;
import com.mycompany.gamezone.service.SaleService;

public class ConsoleMenu {

    private final Scanner scanner;
    private final ProductService productService;
    private final PersonService personService;
    private final SaleService saleService;

    public ConsoleMenu(ProductService productService, PersonService personService, SaleService saleService) {
        this.scanner = new Scanner(System.in);
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int option = readInt();
            switch (option) {
                case 1 -> registerVideoGame();
                case 2 -> registerConsole();
                case 3 -> listProducts();
                case 4 -> registerCustomer();
                case 5 -> listCustomers();
                case 6 -> listSellers();
                case 7 -> registerSale();
                case 8 -> viewAllSales();
                case 9 -> viewSalesByCustomer();
                case 10 -> viewSalesBySeller();
                case 0 -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    private void printMainMenu() {
        System.out.println("\n===== GameZone Unicesar =====");
        System.out.println("1. Register video game");
        System.out.println("2. Register console");
        System.out.println("3. List all products");
        System.out.println("4. Register customer");
        System.out.println("5. List all customers");
        System.out.println("6. List all sellers");
        System.out.println("7. Register a new sale");
        System.out.println("8. View all sales");
        System.out.println("9. View sales by customer");
        System.out.println("10. View sales by seller");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private void registerVideoGame() {
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Price: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Platform: ");
        String platform = scanner.nextLine();
        System.out.print("Genre: ");
        String genre = scanner.nextLine();
        System.out.print("Age rating: ");
        String ageRating = scanner.nextLine();

        productService.registerVideoGame(id, title, price, stock, platform, genre, ageRating);
        System.out.println("Video game registered successfully.");
    }

    private void registerConsole() {
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Price: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Brand: ");
        String brand = scanner.nextLine();
        System.out.print("Model: ");
        String model = scanner.nextLine();
        System.out.print("Generation: ");
        String generation = scanner.nextLine();

        productService.registerConsole(id, title, price, stock, brand, model, generation);
        System.out.println("Console registered successfully.");
    }

    private void listProducts() {
        List<Product> products = productService.listProducts();
        if (products.isEmpty()) {
            System.out.println("No products registered yet.");
            return;
        }
        System.out.println("--- Product Inventory ---");
        for (Product product : products) {
            System.out.println(product.getDescription());
        }
    }

    private void registerCustomer() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Identification: ");
        String id = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        Customer customer = new Customer(name, id, phone, email);
        personService.registerCustomer(customer);
        System.out.println("Customer registered successfully.");
    }

    private void listCustomers() {
        List<Customer> customers = personService.listCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers registered yet.");
            return;
        }
        System.out.println("--- Registered Customers ---");
        for (Customer customer : customers) {
            System.out.println("Name: " + customer.getName() + " | ID: " + customer.getIdentification() +
                    " | Phone: " + customer.getPhone() + " | Email: " + customer.getEmail());
        }
    }

    private void listSellers() {
        List<Seller> sellers = personService.listSellers();
        if (sellers.isEmpty()) {
            System.out.println("No sellers registered yet.");
            return;
        }
        System.out.println("--- Registered Sellers ---");
        for (Seller seller : sellers) {
            System.out.println("Name: " + seller.getName() + " | ID: " + seller.getIdentification() +
                    " | Employee Code: " + seller.getEmployeeCode() + " | Shift: " + seller.getShift());
        }
    }

    private void registerSale() {
        List<Customer> customers = personService.listCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers available. Please register a customer first.");
            return;
        }
        System.out.println("Select a customer:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName() + " (ID: " + customers.get(i).getIdentification() + ")");
        }
        int customerIndex = readInt() - 1;
        if (customerIndex < 0 || customerIndex >= customers.size()) {
            System.out.println("Invalid customer selection.");
            return;
        }
        Customer selectedCustomer = customers.get(customerIndex);

        List<Seller> sellers = personService.listSellers();
        if (sellers.isEmpty()) {
            System.out.println("No sellers available. Please ensure sellers are preloaded.");
            return;
        }
        System.out.println("Select a seller:");
        for (int i = 0; i < sellers.size(); i++) {
            System.out.println((i + 1) + ". " + sellers.get(i).getName() + " (Code: " + sellers.get(i).getEmployeeCode() + ")");
        }
        int sellerIndex = readInt() - 1;
        if (sellerIndex < 0 || sellerIndex >= sellers.size()) {
            System.out.println("Invalid seller selection.");
            return;
        }
        Seller selectedSeller = sellers.get(sellerIndex);

        List<Product> allProducts = productService.listProducts();
        if (allProducts.isEmpty()) {
            System.out.println("No products available. Please register a product first.");
            return;
        }
        List<Product> selectedProducts = new ArrayList<>();
        boolean adding = true;
        while (adding) {
            System.out.println("Select a product to add (or 0 to finish):");
            for (int i = 0; i < allProducts.size(); i++) {
                System.out.println((i + 1) + ". " + allProducts.get(i).getTitle() +
                        " (Stock: " + allProducts.get(i).getStock() + ", Price: $" + allProducts.get(i).getPrice() + ")");
            }
            int productIndex = readInt() - 1;
            if (productIndex == -1) {
                adding = false;
            } else if (productIndex >= 0 && productIndex < allProducts.size()) {
                selectedProducts.add(allProducts.get(productIndex));
                System.out.println("Added " + allProducts.get(productIndex).getTitle() + " to the sale.");
            } else {
                System.out.println("Invalid product selection.");
            }
        }

        if (selectedProducts.isEmpty()) {
            System.out.println("No products selected. Sale cancelled.");
            return;
        }

        try {
            Sale sale = saleService.registerSale(selectedCustomer, selectedSeller, selectedProducts);
            System.out.println("Sale registered successfully!");
            System.out.println("Sale ID: " + sale.getId() + " | Total: $" + String.format("%.2f", sale.getTotal()));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewAllSales() {
        List<Sale> sales = saleService.viewAllSales();
        if (sales.isEmpty()) {
            System.out.println("No sales registered yet.");
            return;
        }
        System.out.println("--- All Sales ---");
        for (Sale sale : sales) {
            System.out.println(sale);
        }
    }

    private void viewSalesByCustomer() {
        List<Customer> customers = personService.listCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers registered yet.");
            return;
        }
        System.out.println("Select a customer:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName());
        }
        int customerIndex = readInt() - 1;
        if (customerIndex < 0 || customerIndex >= customers.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        Customer selected = customers.get(customerIndex);
        List<Sale> sales = saleService.viewSalesByCustomer(selected);
        if (sales.isEmpty()) {
            System.out.println("No sales found for this customer.");
        } else {
            System.out.println("--- Sales for " + selected.getName() + " ---");
            for (Sale sale : sales) {
                System.out.println(sale);
            }
        }
    }

    private void viewSalesBySeller() {
        List<Seller> sellers = personService.listSellers();
        if (sellers.isEmpty()) {
            System.out.println("No sellers registered yet.");
            return;
        }
        System.out.println("Select a seller:");
        for (int i = 0; i < sellers.size(); i++) {
            System.out.println((i + 1) + ". " + sellers.get(i).getName());
        }
        int sellerIndex = readInt() - 1;
        if (sellerIndex < 0 || sellerIndex >= sellers.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        Seller selected = sellers.get(sellerIndex);
        List<Sale> sales = saleService.viewSalesBySeller(selected);
        if (sales.isEmpty()) {
            System.out.println("No sales found for this seller.");
        } else {
            System.out.println("--- Sales attended by " + selected.getName() + " ---");
            for (Sale sale : sales) {
                System.out.println(sale);
            }
        }
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}