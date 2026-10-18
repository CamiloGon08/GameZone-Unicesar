package com.mycompany.gamezone.validation;

import com.mycompany.gamezone.exceptions.BusinessRuleException;
import com.mycompany.gamezone.exceptions.InvalidDataException;
import com.mycompany.gamezone.exceptions.ResourceNotFoundException;
import com.mycompany.gamezone.model.Product;

/** Stateless product checks that never access services or persistence. */
public final class ProductValidator {
    private ProductValidator() { }

    /**
     * Validates common product input without changing it.
     * @param id product identifier
     * @param title product title
     * @param price strictly positive finite unit price
     * @param stock nonnegative inventory quantity
     * @throws InvalidDataException if a field is missing or outside its range
     */
    public static void validateProductData(String id, String title, double price, int stock) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("identificador", "no puede estar vacío.");
        }
        if (title == null || title.isBlank()) {
            throw new InvalidDataException("título", "no puede estar vacío.");
        }
        if (!Double.isFinite(price) || price <= 0) {
            throw new InvalidDataException("precio", "debe ser un número finito mayor que cero.");
        }
        if (stock < 0) {
            throw new InvalidDataException("existencias", "no pueden ser negativas.");
        }
    }

    /**
     * @param product product whose inventory is checked
     * @param requestedQuantity strictly positive requested units
     * @throws InvalidDataException if the product is null or the quantity is invalid
     * @throws BusinessRuleException if the requested units are unavailable
     */
    public static void validateStockAvailability(Product product, int requestedQuantity) {
        if (product == null) {
            throw new InvalidDataException("producto", "no puede ser nulo.");
        }
        if (requestedQuantity <= 0) {
            throw new InvalidDataException("cantidad", "debe ser mayor que cero.");
        }
        if (product.getStock() < requestedQuantity) {
            throw new BusinessRuleException("Existencias insuficientes para el producto "
                    + product.getId() + ".");
        }
    }

    /**
     * @param product lookup result
     * @param productId requested identifier
     * @throws ResourceNotFoundException if the lookup returned null
     */
    public static void validateProductExists(Product product, String productId) {
        if (product == null) {
            throw new ResourceNotFoundException("producto", productId);
        }
    }
}
