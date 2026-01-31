/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// File: src/storeinventory/ReportsPanel.java
package storeinventory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class ReportsPanel extends JPanel {

    private final AppFrame app;

    private final JComboBox<ReportType> reportTypeCombo = new JComboBox<>(ReportType.values());
    private final JTextArea output = new JTextArea(22, 80);

    public ReportsPanel(AppFrame app) {
        this.app = app;
        buildUi();
        refresh();
    }

    private void buildUi() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new BorderLayout(10, 10));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton back = new JButton("Back");
        back.addActionListener(e -> app.showDashboard());
        nav.add(back);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refresh());
        nav.add(refresh);

        top.add(nav, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controls.add(reportTypeCombo);

        JButton generate = new JButton("Generate");
        generate.addActionListener(e -> refresh());
        controls.add(generate);

        top.add(controls, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        add(new JScrollPane(output), BorderLayout.CENTER);
    }

    private void refresh() {
        ReportType type = (ReportType) reportTypeCombo.getSelectedItem();
        if (type == null) type = ReportType.INVENTORY_SUMMARY;

        String text = switch (type) {
            case INVENTORY_SUMMARY -> buildInventorySummary();
            case SALES_SUMMARY -> buildSalesSummary();
            case TOP_PRODUCTS -> buildTopProducts();
            case TOP_CUSTOMERS -> buildTopCustomers();
        };

        output.setText(text);
        output.setCaretPosition(0);
    }

    private String buildInventorySummary() {
        List<Product> products = app.products().getAll();

        int totalSkus = products.size();
        int totalUnits = 0;
        double totalValue = 0.0;

        for (Product p : products) {
            totalUnits += p.getStock();
            totalValue += p.getStock() * p.getUnitPrice();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("INVENTORY SUMMARY\n");
        sb.append("-----------------\n");
        sb.append("Total products: ").append(totalSkus).append('\n');
        sb.append("Total units in stock: ").append(totalUnits).append('\n');
        sb.append("Estimated inventory value: ").append(String.format("%.2f", totalValue)).append('\n');
        sb.append('\n');

        products.sort(Comparator.comparingInt(Product::getStock));
        sb.append("Lowest stock items (up to 5):\n");
        int start = Math.max(0, products.size() - Math.min(5, products.size()));
        for (int i = start; i < products.size(); i++) {
            Product p = products.get(i);
            sb.append("- ").append(p.displayName())
              .append(" | stock=").append(p.getStock())
              .append(" | price=").append(String.format("%.2f", p.getUnitPrice()))
              .append('\n');
        }

        return sb.toString();
    }

    private String buildSalesSummary() {
        List<Order> orders = app.orders().getAll();

        int totalOrders = orders.size();
        int paidOrders = 0;
        double paidRevenue = 0.0;

        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.PAID) {
                paidOrders++;
                paidRevenue += o.total(); // recursion inside Order
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SALES SUMMARY\n");
        sb.append("------------\n");
        sb.append("Total orders: ").append(totalOrders).append('\n');
        sb.append("Paid orders: ").append(paidOrders).append('\n');
        sb.append("Revenue (PAID only): ").append(String.format("%.2f", paidRevenue)).append('\n');

        return sb.toString();
    }

    private String buildTopProducts() {
        List<Order> orders = app.orders().getAll();

        Map<String, Integer> qtyByProductId = new HashMap<>();
        Map<String, String> nameByProductId = new HashMap<>();

        for (Order o : orders) {
            if (o.getStatus() != OrderStatus.PAID) continue;
            for (OrderItem it : o.getItems()) {
                qtyByProductId.merge(it.getProductId(), it.getQuantity(), Integer::sum);
                nameByProductId.putIfAbsent(it.getProductId(), it.getProductName());
            }
        }

        List<Map.Entry<String, Integer>> entries = new java.util.ArrayList<>(qtyByProductId.entrySet());
        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        StringBuilder sb = new StringBuilder();
        sb.append("TOP PRODUCTS (by quantity sold)\n");
        sb.append("------------------------------\n");

        if (entries.isEmpty()) {
            sb.append("No PAID orders.\n");
            return sb.toString();
        }

        int limit = Math.min(10, entries.size());
        for (int i = 0; i < limit; i++) {
            String pid = entries.get(i).getKey();
            int qty = entries.get(i).getValue();
            String name = nameByProductId.getOrDefault(pid, pid);
            sb.append(i + 1).append(") ").append(name).append(" | qty=").append(qty).append('\n');
        }

        return sb.toString();
    }

    private String buildTopCustomers() {
        List<Order> orders = app.orders().getAll();

        Map<String, Double> spendByCustomerId = new HashMap<>();
        Map<String, String> nameByCustomerId = new HashMap<>();

        for (Order o : orders) {
            if (o.getStatus() != OrderStatus.PAID) continue;
            spendByCustomerId.merge(o.getCustomerId(), o.total(), Double::sum);
            nameByCustomerId.putIfAbsent(o.getCustomerId(), o.getCustomerName());
        }

        List<Map.Entry<String, Double>> entries = new java.util.ArrayList<>(spendByCustomerId.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        StringBuilder sb = new StringBuilder();
        sb.append("TOP CUSTOMERS (by spend)\n");
        sb.append("------------------------\n");

        if (entries.isEmpty()) {
            sb.append("No PAID orders.\n");
            return sb.toString();
        }

        int limit = Math.min(10, entries.size());
        for (int i = 0; i < limit; i++) {
            String cid = entries.get(i).getKey();
            double amt = entries.get(i).getValue();
            String name = nameByCustomerId.getOrDefault(cid, cid);
            sb.append(i + 1).append(") ").append(name).append(" | spend=").append(String.format("%.2f", amt)).append('\n');
        }

        return sb.toString();
    }

    public enum ReportType {
        INVENTORY_SUMMARY,
        SALES_SUMMARY,
        TOP_PRODUCTS,
        TOP_CUSTOMERS
    }
}

