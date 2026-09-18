package com.mycompany.gamezone.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Person;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.service.PersonService;
import com.mycompany.gamezone.service.ProductService;
import com.mycompany.gamezone.service.SaleService;

/**
 * Console-based user interface for GameZone Unicesar.
 * Displays the main menu, reads the user's selections and dispatches
 * each option to the corresponding service operation. All the messages
 * shown to the user are written in Spanish, while identifiers and
 * documentation follow the English convention required by the project.
 */
public class ConsoleMenu {

    private final Scanner scanner;
    private final ProductService productService;
    private final PersonService personService;
    private final SaleService saleService;
    private final ArrayList<Person> persons;

    /**
     * Creates a console menu with the services required to perform the
     * user operations.
     *
     * @param productService service that handles product-related operations
     * @param personService  service that handles person-related operations
     * @param saleService    service that handles sale-related operations
     */
    public ConsoleMenu(ProductService productService, PersonService personService, SaleService saleService) {
        this.scanner = new Scanner(System.in);
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.persons = personService.listPersons();
    }

    /**
     * Starts the main interaction loop of the console menu. Displays the
     * main menu repeatedly and dispatches each user selection to the
     * corresponding operation until the user chooses to exit.
     */
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
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
        System.out.println("¡Hasta luego!");
    }

    private void printMainMenu() {
        System.out.println("\n===== GameZone Unicesar =====");
        System.out.println("1. Registrar videojuego");
        System.out.println("2. Registrar consola");
        System.out.println("3. Listar todos los productos");
        System.out.println("4. Registrar cliente");
        System.out.println("5. Listar clientes");
        System.out.println("6. Listar vendedores");
        System.out.println("7. Registrar una nueva venta");
        System.out.println("8. Ver todas las ventas");
        System.out.println("9. Ver ventas por cliente");
        System.out.println("10. Ver ventas por vendedor");
        System.out.println("0. Salir");
        System.out.print("Elija una opción: ");
    }

    private void registerVideoGame() {
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Título: ");
        String title = scanner.nextLine();
        System.out.print("Precio: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Plataforma: ");
        String platform = scanner.nextLine();
        System.out.print("Género: ");
        String genre = scanner.nextLine();
        System.out.print("Clasificación de edad: ");
        String ageRating = scanner.nextLine();

        try {
            productService.registerVideoGame(id, title, price, stock, platform, genre, ageRating);
            System.out.println("Videojuego registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registerConsole() {
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Título: ");
        String title = scanner.nextLine();
        System.out.print("Precio: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Marca: ");
        String brand = scanner.nextLine();
        System.out.print("Modelo: ");
        String model = scanner.nextLine();
        System.out.print("Generación: ");
        String generation = scanner.nextLine();

        try {
            productService.registerConsole(id, title, price, stock, brand, model, generation);
            System.out.println("Consola registrada exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listProducts() {
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

    private void registerCustomer() {
        System.out.print("Nombre: ");
        String name = scanner.nextLine();
        System.out.print("Identificación: ");
        String id = scanner.nextLine();
        System.out.print("Teléfono (solo números): ");
        long phone;
        try {
            phone = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Teléfono inválido.");
            return;
        }
        System.out.print("Correo electrónico: ");
        String email = scanner.nextLine();

        try {
            personService.registerCustomer(persons, email, name, id, phone);
            personService.savePersons(persons);
            System.out.println("Cliente registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listCustomers() {
        List<Customer> customers = getCustomers();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes registrados aún.");
            return;
        }
        System.out.println("--- Clientes registrados ---");
        for (Customer customer : customers) {
            System.out.println("Nombre: " + customer.getName() + " | ID: " + customer.getiD() +
                    " | Teléfono: " + customer.getContactNumber() + " | Correo: " + customer.geteMail());
        }
    }

    private void listSellers() {
        List<Seller> sellers = getSellers();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores registrados aún.");
            return;
        }
        System.out.println("--- Vendedores registrados ---");
        for (Seller seller : sellers) {
            System.out.println("Nombre: " + seller.getName() + " | ID: " + seller.getiD() +
                    " | Código de empleado: " + seller.getEmployeeCode() + " | Turno: " + seller.getShift());
        }
    }

    private void registerSale() {
        List<Customer> customers = getCustomers();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes disponibles. Registre un cliente primero.");
            return;
        }
        System.out.println("Seleccione un cliente:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName() + " (ID: " + customers.get(i).getiD() + ")");
        }
        int customerIndex = readInt() - 1;
        if (customerIndex < 0 || customerIndex >= customers.size()) {
            System.out.println("Selección de cliente inválida.");
            return;
        }
        Customer selectedCustomer = customers.get(customerIndex);

        List<Seller> sellers = getSellers();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores disponibles. Verifique que estén precargados.");
            return;
        }
        System.out.println("Seleccione un vendedor:");
        for (int i = 0; i < sellers.size(); i++) {
            System.out.println((i + 1) + ". " + sellers.get(i).getName() + " (Código: " + sellers.get(i).getEmployeeCode() + ")");
        }
        int sellerIndex = readInt() - 1;
        if (sellerIndex < 0 || sellerIndex >= sellers.size()) {
            System.out.println("Selección de vendedor inválida.");
            return;
        }
        Seller selectedSeller = sellers.get(sellerIndex);

        List<Product> allProducts = productService.listProducts();
        if (allProducts.isEmpty()) {
            System.out.println("No hay productos disponibles. Registre un producto primero.");
            return;
        }
        List<Product> selectedProducts = new ArrayList<>();
        boolean adding = true;
        while (adding) {
            System.out.println("Seleccione un producto para agregar (o 0 para terminar):");
            for (int i = 0; i < allProducts.size(); i++) {
                System.out.println((i + 1) + ". " + allProducts.get(i).getTitle() +
                        " (Stock: " + allProducts.get(i).getStock() + ", Precio: $" + allProducts.get(i).getPrice() + ")");
            }
            int productIndex = readInt() - 1;
            if (productIndex == -1) {
                adding = false;
            } else if (productIndex >= 0 && productIndex < allProducts.size()) {
                selectedProducts.add(allProducts.get(productIndex));
                System.out.println("Se agregó " + allProducts.get(productIndex).getTitle() + " a la venta.");
            } else {
                System.out.println("Selección de producto inválida.");
            }
        }

        if (selectedProducts.isEmpty()) {
            System.out.println("No se seleccionó ningún producto. Venta cancelada.");
            return;
        }

        try {
            Sale sale = saleService.registerSale(selectedCustomer, selectedSeller, selectedProducts);
            System.out.println("¡Venta registrada exitosamente!");
            System.out.println("ID de venta: " + sale.getId() + " | Total: $" + String.format("%.2f", sale.getTotal()));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewAllSales() {
        List<Sale> sales = saleService.viewAllSales();
        if (sales.isEmpty()) {
            System.out.println("No hay ventas registradas aún.");
            return;
        }
        System.out.println("--- Todas las ventas ---");
        for (Sale sale : sales) {
            System.out.println(sale);
        }
    }

    private void viewSalesByCustomer() {
        List<Customer> customers = getCustomers();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes registrados aún.");
            return;
        }
        System.out.println("Seleccione un cliente:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName());
        }
        int customerIndex = readInt() - 1;
        if (customerIndex < 0 || customerIndex >= customers.size()) {
            System.out.println("Selección inválida.");
            return;
        }
        Customer selected = customers.get(customerIndex);
        List<Sale> sales = saleService.viewSalesByCustomer(selected);
        if (sales.isEmpty()) {
            System.out.println("No se encontraron ventas para este cliente.");
        } else {
            System.out.println("--- Ventas de " + selected.getName() + " ---");
            for (Sale sale : sales) {
                System.out.println(sale);
            }
        }
    }

    private void viewSalesBySeller() {
        List<Seller> sellers = getSellers();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores registrados aún.");
            return;
        }
        System.out.println("Seleccione un vendedor:");
        for (int i = 0; i < sellers.size(); i++) {
            System.out.println((i + 1) + ". " + sellers.get(i).getName());
        }
        int sellerIndex = readInt() - 1;
        if (sellerIndex < 0 || sellerIndex >= sellers.size()) {
            System.out.println("Selección inválida.");
            return;
        }
        Seller selected = sellers.get(sellerIndex);
        List<Sale> sales = saleService.viewSalesBySeller(selected);
        if (sales.isEmpty()) {
            System.out.println("No se encontraron ventas para este vendedor.");
        } else {
            System.out.println("--- Ventas atendidas por " + selected.getName() + " ---");
            for (Sale sale : sales) {
                System.out.println(sale);
            }
        }
    }

    private List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (Person person : persons) {
            if (person instanceof Customer customer) {
                customers.add(customer);
            }
        }
        return customers;
    }

    private List<Seller> getSellers() {
        List<Seller> sellers = new ArrayList<>();
        for (Person person : persons) {
            if (person instanceof Seller seller) {
                sellers.add(seller);
            }
        }
        return sellers;
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}