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
public final class Order extends AbstractEntity {
    
    private String customerId;
    private String customerName;
    private final List<OrderItem> items;
    private OrderStatus status;

    public Order(String id, String customerId, String customerName, List<OrderItem> items, OrderStatus status) {
        super(id);
        this.customerId = Objects.requireNonNullElse(customerId, "");
        this.customerName = Objects.requireNonNullElse(customerName, "");
        this.items = new ArrayList<>(Objects.requireNonNull(items, "items"));
        this.status = status == null ? OrderStatus.DRAFT : status;
    }

    public Order(String id, LocalDateTime createdAt, String customerId, String customerName, List<OrderItem> items, OrderStatus status) {
        super(id, createdAt);
        this.customerId = Objects.requireNonNullElse(customerId, "");
        this.customerName = Objects.requireNonNullElse(customerName, "");
        this.items = new ArrayList<>(Objects.requireNonNull(items, "items"));
        this.status = status == null ? OrderStatus.DRAFT : status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = Objects.requireNonNullElse(customerId, "");
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = Objects.requireNonNullElse(customerName, "");
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status == null ? OrderStatus.DRAFT : status;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public void addItem(OrderItem item) {
        items.add(Objects.requireNonNull(item, "item"));
    }

    public void removeItem(int index) {
        items.remove(index);
    }

    /**
     * Recursive total calculation to satisfy recursion requirement.
     */
    public double total() {
        return totalRec(items, 0);
    }

    private static double totalRec(List<OrderItem> items, int idx) {
        if (idx >= items.size()) return 0.0;
        return items.get(idx).lineTotal() + totalRec(items, idx + 1);
    }

    @Override
    public String displayName() {
        String c = customerName == null ? "" : customerName.trim();
        return c.isEmpty() ? ("Order " + getId()) : ("Order " + getId() + " - " + c);
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (customerId == null || customerId.isBlank()) errors.add("Customer is required.");
        if (items.isEmpty()) errors.add("Order must contain at least 1 item.");

        for (OrderItem it : items) {
            if (it.getQuantity() <= 0) errors.add("Item quantity must be > 0.");
            if (it.getUnitPrice() < 0) errors.add("Item unit price must be >= 0.");
        }

        return errors;
    }

    @Override
    public String csvHeader() {
        return "id,createdAt,customerId,customerName,status,items";
    }

    @Override
    public String toCsv() {
        // Csv escaping will be added once CsvUtil exists.
        // items encoded: productId;productName;qty;unitPrice|productId;...
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            OrderItem it = items.get(i);
            if (i > 0) sb.append("|");
            sb.append(it.getProductId()).append(";")
              .append(it.getProductName()).append(";")
              .append(it.getQuantity()).append(";")
              .append(it.getUnitPrice());
        }

        return String.join(",",
                getId(),
                getCreatedAt().toString(),
                customerId,
                customerName,
                status.name(),
                sb.toString()
        );
    }
}
