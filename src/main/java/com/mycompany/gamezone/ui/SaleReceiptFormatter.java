package com.mycompany.gamezone.ui;

import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.SaleItem;

/**
 * Builds the Spanish receipt shown to the user after a sale. The rules of
 * the sale stay in {@link Sale}; this class only lays out its values.
 */
public class SaleReceiptFormatter {

    /**
     * Builds the receipt of a sale including the subtotal, the discount
     * applied (with the promotion name), the extended warranty cost and the
     * final total.
     *
     * @param sale the sale to describe
     * @return the formatted receipt
     */
    public String format(Sale sale) {
        StringBuilder sb = new StringBuilder();
        appendHeader(sb, sale);
        appendProducts(sb, sale);
        appendTotals(sb, sale);
        return sb.toString();
    }

    private void appendHeader(StringBuilder sb, Sale sale) {
        sb.append("===== RECIBO DE VENTA =====\n");
        sb.append("ID: ").append(sale.getId()).append("\n");
        sb.append("Fecha: ").append(sale.getDate()).append("\n");
        sb.append("Cliente: ").append(sale.getCustomer().getName()).append("\n");
        sb.append("Vendedor: ").append(sale.getSeller().getName()).append("\n");
        sb.append("Productos:\n");
    }

    private void appendProducts(StringBuilder sb, Sale sale) {
        for (SaleItem item : sale.getItems()) {
            sb.append("  - ").append(item.getProduct().getTitle())
            .append(": $").append(String.format("%.2f", item.getUnitPrice()))
            .append("\n");
        }
        sb.append("---------------------------\n");
    }

    private void appendTotals(StringBuilder sb, Sale sale) {
        sb.append("Subtotal:  $").append(String.format("%.2f", sale.getSubtotal())).append("\n");

        if (sale.getDiscountAmount() > 0) {
            sb.append("Descuento: $").append(String.format("%.2f", sale.getDiscountAmount()));
            if (sale.getAppliedPromotionName() != null) {
                sb.append("  (").append(sale.getAppliedPromotionName()).append(")");
            }
            sb.append("\n");
        }

        if (sale.getWarrantyCost() > 0) {
            sb.append("Garantía extendida: $").append(String.format("%.2f", sale.getWarrantyCost())).append("\n");
        }

        sb.append("TOTAL:     $").append(String.format("%.2f", sale.getTotal())).append("\n");
        sb.append("===========================");
    }
}
