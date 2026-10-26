package com.mycompany.gamezone.ui;

import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Return;

/**
 * Builds the Spanish receipt shown to the user for a return. The refund
 * rules stay in {@link Return}; this class only lays out the values.
 */
public class ReturnReceiptFormatter {

    /**
     * Builds the receipt showing, for each returned item, the charged price,
     * the proportional discount and the refunded amount, followed by the
     * refund for cancelled warranties and the total refund.
     *
     * @param returnRecord the return to describe
     * @return the formatted receipt
     */
    public String format(Return returnRecord) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("===== Recibo de Devolucion =====\n");
        receipt.append("Identificador: ").append(returnRecord.getId()).append("\n");
        receipt.append("Fecha: ").append(returnRecord.getDate()).append("\n");
        receipt.append("Venta original: ").append(returnRecord.getOriginalSale().getId()).append("\n");
        receipt.append("Motivo: ").append(returnRecord.getReason()).append("\n");
        receipt.append("Productos devueltos:\n");
        for (Product product : returnRecord.getReturnedProducts()) {
            double listPrice = returnRecord.listPriceOf(product);
            double proportionalDiscount = returnRecord.proportionalDiscountOf(product);
            double refunded = listPrice - proportionalDiscount;
            receipt.append("  - ").append(product.getTitle())
                   .append(" | Precio de lista: $").append(String.format("%.2f", listPrice))
                   .append(" | Descuento proporcional: $").append(String.format("%.2f", proportionalDiscount))
                   .append(" | Reembolsado: $").append(String.format("%.2f", refunded))
                   .append("\n");
        }
        receipt.append("Reembolso por garantias anuladas: $")
               .append(String.format("%.2f", returnRecord.getWarrantyRefundAmount())).append("\n");
        receipt.append("Monto total reembolsado: $")
               .append(String.format("%.2f", returnRecord.getRefundAmount())).append("\n");
        receipt.append("=================================");
        return receipt.toString();
    }
}
