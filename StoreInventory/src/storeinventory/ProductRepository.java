/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author User
 */
public final class ProductRepository extends AbstractRepository<Product> {

    public ProductRepository(Path dataDir) {
        super(dataDir);
    }

    @Override
    protected String fileName() {
        return "products.csv";
    }

    @Override
    protected String csvHeader() {
        return "id,createdAt,sku,name,unitPrice,stock";
    }

    @Override
    protected Product parse(String csvLine) {
        String[] parts = CsvUtil.splitLine(csvLine);
        if (parts.length < 6) throw new IllegalArgumentException("Invalid product csv: " + csvLine);

        String id = parts[0];
        LocalDateTime createdAt = LocalDateTime.parse(parts[1]);
        String sku = parts[2];
        String name = parts[3];
        double unitPrice = Double.parseDouble(parts[4]);
        int stock = Integer.parseInt(parts[5]);

        return new Product(id, createdAt, sku, name, unitPrice, stock);
    }

    public Product findBySkuLinear(String sku) {
        if (sku == null) return null;
        String target = sku.trim();
        for (Product p : getAll()) {
            if (target.equalsIgnoreCase(p.getSku().trim())) return p;
        }
        return null;
    }

    public List<Product> findByNameContains(String token) {
        List<Product> out = new ArrayList<>();
        if (token == null || token.isBlank()) return out;

        String t = token.trim().toLowerCase();
        for (Product p : getAll()) {
            if (p.getName() != null && p.getName().toLowerCase().contains(t)) out.add(p);
        }
        return out;
    }
}
