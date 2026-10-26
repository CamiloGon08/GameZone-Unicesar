package com.mycompany.gamezone.model;

/**
 * Represents one item of a sale. It keeps the product sold and the unit
 * price charged at the moment of the sale, so later changes to the price
 * of the product do not alter the sale.
 */
public class SaleItem {

    private final Product product;
    private final double unitPrice;

    /**
     * Creates a sale item for the given product, freezing its current price.
     *
     * @param product product sold
     */
    public SaleItem(Product product) {
        this.product = product;
        this.unitPrice = product.getPrice();
    }

    /**
     * @return the product sold
     */
    public Product getProduct() {
        return product;
    }

    /**
     * @return the unit price charged when the sale was made
     */
    public double getUnitPrice() {
        return unitPrice;
    }
}
