/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.util.Objects;
/**
 *
 * @author User
 */
public class OrderItem {
    private final String productId;
    private final String productName;
    private final int quantity;
    private final double unitPrice;

    public OrderItem(String productId, String productName, int quantity, double unitPrice) {
        this.productId = Objects.requireNonNull(productId, "productId");
        this.productName = Objects.requireNonNullElse(productName, "");
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double lineTotal() {
        return unitPrice * quantity;
    }
}
