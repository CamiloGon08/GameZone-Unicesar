package com.mycompany.gamezone.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

import com.mycompany.gamezone.exceptions.BusinessRuleException;
import com.mycompany.gamezone.exceptions.InvalidDataException;
import com.mycompany.gamezone.exceptions.PersistenceException;
import com.mycompany.gamezone.exceptions.ResourceNotFoundException;

/**
 * Console input and error output shared by every menu: reads lines and
 * numbers typed by the user, splits comma-separated lists and prints the
 * Spanish message that corresponds to each type of failure.
 */
class ConsoleIO {

    private final Scanner scanner;
    private final Map<Class<? extends RuntimeException>, Function<RuntimeException, String>> messages =
            new LinkedHashMap<>();

    /**
     * Creates the helper reading from the standard input and registers the
     * message of each type of failure. A new exception type only adds one
     * entry here.
     */
    ConsoleIO() {
        this.scanner = new Scanner(System.in);
        messages.put(ResourceNotFoundException.class,
                e -> "No se encontró el recurso solicitado: " + e.getMessage());
        messages.put(BusinessRuleException.class,
                e -> "No se puede completar la operación: " + e.getMessage());
        messages.put(InvalidDataException.class,
                e -> "Los datos ingresados no son válidos: " + e.getMessage());
        messages.put(PersistenceException.class,
                e -> "Ocurrió un problema al acceder a los datos del sistema. "
                        + "Contacte al administrador.");
    }

    /**
     * Reads the next line typed by the user.
     *
     * @return the line read
     */
    String readLine() {
        return scanner.nextLine();
    }

    /**
     * Reads the next line typed by the user as a whole number.
     *
     * @return the number read, or -1 if the line is not a valid number
     */
    int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Splits a comma-separated text into its non-empty, trimmed parts.
     *
     * @param raw the text typed by the user
     * @return the list of parts, empty if the text is blank
     */
    List<String> parseCommaSeparated(String raw) {
        List<String> result = new ArrayList<>();
        if (raw == null || raw.trim().isEmpty()) {
            return result;
        }
        for (String part : raw.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * Prints a Spanish message according to the type of failure. The first
     * registered type that matches the exception decides the message.
     *
     * @param e the failure to report
     */
    void handleException(RuntimeException e) {
        for (Map.Entry<Class<? extends RuntimeException>, Function<RuntimeException, String>> entry
                : messages.entrySet()) {
            if (entry.getKey().isInstance(e)) {
                System.out.println(entry.getValue().apply(e));
                return;
            }
        }
        // Modules not yet migrated still throw IllegalArgumentException
        System.out.println("Error: " + e.getMessage());
    }
}
