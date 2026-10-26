package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Product;

/** Converts one product family using the existing product-file format. */
public interface ProductLineConverter {

    /** @return the exact type token stored at the beginning of a record */
    String getTypeKey();

    /**
     * @param product the product to inspect
     * @return whether this converter handles the product's family
     */
    boolean supports(Product product);

    /**
     * @param product a product supported by this converter
     * @return its record, without a line separator
     */
    String toLine(Product product);

    /**
     * Reconstructs a product after the repository has parsed the shared fields.
     *
     * @param id parsed identifier
     * @param title parsed title
     * @param price parsed price
     * @param stock parsed stock
     * @param fields the trimmed record fields in their original order
     * @return the reconstructed product
     */
    Product toProduct(String id, String title, double price, int stock, String[] fields);
}
