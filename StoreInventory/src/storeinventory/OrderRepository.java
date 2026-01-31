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
public final class OrderRepository extends AbstractRepository<Order> {

    public OrderRepository(Path dataDir) {
        super(dataDir);
    }

    @Override
    protected String fileName() {
        return "orders.csv";
    }

    @Override
    protected String csvHeader() {
        return "id,createdAt,customerId,customerName,status,items";
    }

    @Override
    protected Order parse(String csvLine) {
        String[] parts = CsvUtil.splitLine(csvLine);
        if (parts.length < 6) throw new IllegalArgumentException("Invalid order csv: " + csvLine);

        String id = parts[0];
        LocalDateTime createdAt = LocalDateTime.parse(parts[1]);
        String customerId = parts[2];
        String customerName = parts[3];
        OrderStatus status = OrderStatus.valueOf(parts[4]);

        List<OrderItem> items = parseItems(parts[5]);

        return new Order(id, createdAt, customerId, customerName, items, status);
    }

    private static List<OrderItem> parseItems(String encoded) {
        List<OrderItem> out = new ArrayList<>();
        if (encoded == null || encoded.isBlank()) return out;

        // Format: productId;productName;qty;unitPrice|productId;productName;qty;unitPrice
        String[] itemTokens = encoded.split("\\|");
        for (String token : itemTokens) {
            if (token.isBlank()) continue;

            String[] fields = token.split(";", -1);
            if (fields.length < 4) continue;

            String productId = fields[0];
            String productName = fields[1];

            int qty;
            double unitPrice;
            try {
                qty = Integer.parseInt(fields[2].trim());
                unitPrice = Double.parseDouble(fields[3].trim());
            } catch (NumberFormatException ex) {
                System.err.println("[ORDER PARSE] Bad item numbers: " + token);
                continue;
            }

            out.add(new OrderItem(productId, productName, qty, unitPrice));
        }

        return out;
    }

    public List<Order> findByCustomerId(String customerId) {
        List<Order> out = new ArrayList<>();
        if (customerId == null || customerId.isBlank()) return out;

        String target = customerId.trim();
        for (Order o : getAll()) {
            if (target.equals(o.getCustomerId())) out.add(o);
        }
        return out;
    }
}