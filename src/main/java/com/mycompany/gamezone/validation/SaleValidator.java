package com.mycompany.gamezone.validation;

import com.mycompany.gamezone.exceptions.BusinessRuleException;
import com.mycompany.gamezone.exceptions.InvalidDataException;
import com.mycompany.gamezone.exceptions.ResourceNotFoundException;
import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Seller;
import java.util.List;

/** Validates sale input without constructing sales or modifying inventory. */
public final class SaleValidator {
    private SaleValidator() { }

    /**
     * @param customer customer making the purchase
     * @param seller seller attending the purchase
     * @param products products selected for the sale
     * @throws InvalidDataException if a participant, list, or product is null
     * @throws BusinessRuleException if the sale has no products
     */
    public static void validateSaleData(Customer customer, Seller seller, List<Product> products) {
        if (customer == null) {
            throw new InvalidDataException("cliente", "no puede ser nulo.");
        }
        if (seller == null) {
            throw new InvalidDataException("vendedor", "no puede ser nulo.");
        }
        if (products == null) {
            throw new InvalidDataException("productos", "la lista no puede ser nula.");
        }
        if (products.isEmpty()) {
            throw new BusinessRuleException("La venta debe contener al menos un producto.");
        }
        for (Product product : products) {
            if (product == null) {
                throw new InvalidDataException("productos", "la lista contiene un producto nulo.");
            }
        }
    }

    /**
     * @param sale lookup result
     * @param saleId requested identifier
     * @throws ResourceNotFoundException if the lookup returned null
     */
    public static void validateSaleExists(Sale sale, String saleId) {
        if (sale == null) {
            throw new ResourceNotFoundException("venta", saleId);
        }
    }
}
