package com.mycompany.gamezone.ui;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.exceptions.GameZoneException;
import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.service.AccessoryService;
import com.mycompany.gamezone.service.PersonService;
import com.mycompany.gamezone.service.ProductService;
import com.mycompany.gamezone.service.SaleService;

/**
 * Console options of the main menu that register sales and list them by customer and by seller.
 * All the messages shown to the user are written in Spanish.
 */
class SaleMenu {

    private final ProductService productService;
    private final PersonService personService;
    private final SaleService saleService;
    private final AccessoryService accessoryService;
    private final ConsoleIO io;

    /**
     * Creates the menu with the collaborators it needs.
     *
     * @param productService service that handles product-related operations
     * @param personService service that handles person-related operations
     * @param saleService service that handles sale-related operations
     * @param accessoryService service that handles accessory-related operations
     * @param io helper that reads the input and prints error messages
     */
    SaleMenu(ProductService productService,
             PersonService personService,
             SaleService saleService,
             AccessoryService accessoryService,
             ConsoleIO io) {
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.accessoryService = accessoryService;
        this.io = io;
    }

    void registerSale() {
        List<Customer> customers = personService.listCustomers();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes disponibles. Registre un cliente primero.");
            return;
        }
        System.out.println("Seleccione un cliente:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName()
                    + " (ID: " + customers.get(i).getiD() + ")");
        }
        int customerIndex = io.readInt() - 1;
        if (customerIndex < 0 || customerIndex >= customers.size()) {
            System.out.println("Selección de cliente inválida.");
            return;
        }
        Customer selectedCustomer = customers.get(customerIndex);

        List<Seller> sellers = personService.listSellers();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores disponibles. Verifique que estén precargados.");
            return;
        }
        System.out.println("Seleccione un vendedor:");
        for (int i = 0; i < sellers.size(); i++) {
            System.out.println((i + 1) + ". " + sellers.get(i).getName()
                    + " (Código: " + sellers.get(i).getEmployeeCode() + ")");
        }
        int sellerIndex = io.readInt() - 1;
        if (sellerIndex < 0 || sellerIndex >= sellers.size()) {
            System.out.println("Selección de vendedor inválida.");
            return;
        }
        Seller selectedSeller = sellers.get(sellerIndex);

        List<Product> selectableItems = new ArrayList<>();
        selectableItems.addAll(productService.listProducts());
        selectableItems.addAll(accessoryService.listAllAccessories());

        if (selectableItems.isEmpty()) {
            System.out.println("No hay productos ni accesorios disponibles. Registre uno primero.");
            return;
        }

        List<Product> selectedItems = new ArrayList<>();
        boolean adding = true;
        while (adding) {
            System.out.println("Seleccione un producto o accesorio para agregar (o 0 para terminar):");
            for (int i = 0; i < selectableItems.size(); i++) {
                Product item = selectableItems.get(i);
                System.out.println((i + 1) + ". " + item.getTitle()
                        + " (Stock: " + item.getStock()
                        + ", Precio: $" + item.getPrice() + ")");
            }
            int itemIndex = io.readInt() - 1;
            if (itemIndex == -1) {
                adding = false;
            } else if (itemIndex >= 0 && itemIndex < selectableItems.size()) {
                selectedItems.add(selectableItems.get(itemIndex));
                System.out.println("Se agregó " + selectableItems.get(itemIndex).getTitle() + " a la venta.");
            } else {
                System.out.println("Selección inválida.");
            }
        }

        if (selectedItems.isEmpty()) {
            System.out.println("No se seleccionó ningún ítem. Venta cancelada.");
            return;
        }

        List<String> productIdsWithExtendedWarranty = new ArrayList<>();
        for (Product item : selectedItems) {
            if (item.allowsExtendedWarranty()) {
                System.out.print("¿Desea agregar garantía extendida a "
                        + item.getTitle() + "? (s/n): ");
                String respuesta = io.readLine();
                if (respuesta.equalsIgnoreCase("s")) {
                    productIdsWithExtendedWarranty.add(item.getId());
                }
            }
        }

        try {
            Sale sale = saleService.registerSale(selectedCustomer, selectedSeller, selectedItems,
                    productIdsWithExtendedWarranty);
            System.out.println("¡Venta registrada exitosamente!");
            System.out.println(sale.generateReceipt());
        } catch (GameZoneException | IllegalArgumentException e) {
            io.handleException(e);
        }
    }

    void viewAllSales() {
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

    void viewSalesByCustomer() {
        List<Customer> customers = personService.listCustomers();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes registrados aún.");
            return;
        }
        System.out.println("Seleccione un cliente:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName());
        }
        int customerIndex = io.readInt() - 1;
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

    void viewSalesBySeller() {
        List<Seller> sellers = personService.listSellers();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores registrados aún.");
            return;
        }
        System.out.println("Seleccione un vendedor:");
        for (int i = 0; i < sellers.size(); i++) {
            System.out.println((i + 1) + ". " + sellers.get(i).getName());
        }
        int sellerIndex = io.readInt() - 1;
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
}
