package com.mycompany.gamezone.ui;

import java.util.List;

import com.mycompany.gamezone.exceptions.GameZoneException;
import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.factory.PersonFactory;
import com.mycompany.gamezone.service.PersonService;

/**
 * Console options of the main menu that register customers and list customers and sellers.
 * All the messages shown to the user are written in Spanish.
 */
class PersonMenu {

    private final PersonService personService;
    private final PersonFactory personFactory;
    private final ConsoleIO io;

    /**
     * Creates the menu with the collaborators it needs.
     *
     * @param personService service that handles person-related operations
     * @param personFactory factory that builds new customers
     * @param io helper that reads the input and prints error messages
     */
    PersonMenu(PersonService personService,
               PersonFactory personFactory,
               ConsoleIO io) {
        this.personService = personService;
        this.personFactory = personFactory;
        this.io = io;
    }

    void registerCustomer() {
        System.out.print("Nombre: ");
        String name = io.readLine();
        System.out.print("Identificación: ");
        String id = io.readLine();
        System.out.print("Teléfono (solo números): ");
        long phone;
        try {
            phone = Long.parseLong(io.readLine());
        } catch (NumberFormatException e) {
            System.out.println("Teléfono inválido.");
            return;
        }
        System.out.print("Correo electrónico: ");
        String email = io.readLine();

        try {
            personService.register(personFactory.createCustomer(email, name, id, phone));
            System.out.println("Cliente registrado exitosamente.");
        } catch (GameZoneException | IllegalArgumentException e) {
            io.handleException(e);
        }
    }

    void listCustomers() {
        List<Customer> customers = personService.listCustomers();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes registrados aún.");
            return;
        }
        System.out.println("--- Clientes registrados ---");
        for (Customer customer : customers) {
            System.out.println("Nombre: " + customer.getName()
                    + " | ID: " + customer.getiD()
                    + " | Teléfono: " + customer.getContactNumber()
                    + " | Correo: " + customer.geteMail());
        }
    }

    void listSellers() {
        List<Seller> sellers = personService.listSellers();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores registrados aún.");
            return;
        }
        System.out.println("--- Vendedores registrados ---");
        for (Seller seller : sellers) {
            System.out.println("Nombre: " + seller.getName()
                    + " | ID: " + seller.getiD()
                    + " | Código de empleado: " + seller.getEmployeeCode()
                    + " | Turno: " + seller.getShift());
        }
    }
}
