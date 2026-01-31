/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 *
 * @author User
 */
public class Product extends AbstractEntity {
    
    // SKU is Stock Keeping Unit
    private String sku;
    private String name;
    private double unitPrice;
    private int stock;

    public Product(String id, String sku, String name, double unitPrice, int stock) {
        super(id);
        this.sku = Objects.requireNonNullElse(sku, "");
        this.name = Objects.requireNonNullElse(name, "");
        this.unitPrice = unitPrice;
        this.stock = stock;
    }

    public Product(String id, LocalDateTime createdAt, String sku, String name, double unitPrice, int stock) {
        super(id, createdAt);
        this.sku = Objects.requireNonNullElse(sku, "");
        this.name = Objects.requireNonNullElse(name, "");
        this.unitPrice = unitPrice;
        this.stock = stock;
    }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = Objects.requireNonNullElse(sku, ""); }

    public String getName() { return name; }
    public void setName(String name) { this.name = Objects.requireNonNullElse(name, ""); }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String displayName() {
        String n = name == null ? "" : name.trim();
        String s = sku == null ? "" : sku.trim();
        if (!n.isEmpty() && !s.isEmpty()) return n + " (" + s + ")";
        if (!n.isEmpty()) return n;
        if (!s.isEmpty()) return s;
        return getId();
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (sku == null || sku.isBlank()) errors.add("SKU is required.");
        if (name == null || name.isBlank()) errors.add("Name is required.");
        if (unitPrice < 0) errors.add("Unit price must be >= 0.");
        if (stock < 0) errors.add("Stock must be >= 0.");
        return errors;
    }

    @Override
    public String csvHeader() {
        return "id,createdAt,sku,name,unitPrice,stock";
    }

    @Override
    public String toCsv() {
        return String.join(",",
                CsvUtil.escape(getId()),
                CsvUtil.escape(getCreatedAt().toString()),
                CsvUtil.escape(sku),
                CsvUtil.escape(name),
                CsvUtil.escape(Double.toString(unitPrice)),
                CsvUtil.escape(Integer.toString(stock))
        );
    }

    @Override
    public String toString() {
        return displayName();
    }
    
    
}
