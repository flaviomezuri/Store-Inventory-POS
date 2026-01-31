/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// File: src/storeinventory/OrdersPanel.java
package storeinventory;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

public final class OrdersPanel extends JPanel {

    private final AppFrame app;
    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;

    // Create / Draft section
    private final JComboBox<Customer> customerCombo = new JComboBox<>();
    private final JComboBox<Product> productCombo = new JComboBox<>();
    private final JTextField qtyField = new JTextField(8);

    private final DraftItemTableModel draftModel = new DraftItemTableModel();
    private final JTable draftTable = new JTable(draftModel);
    private final JLabel draftTotalLabel = new JLabel("Total: 0.00");

    // Saved orders section
    private final OrdersTableModel ordersModel = new OrdersTableModel();
    private final JTable ordersTable = new JTable(ordersModel);

    public OrdersPanel(AppFrame app) {
        this.app = app;
        this.productRepo = app.products();
        this.customerRepo = app.customers();
        this.orderRepo = app.orders();

        buildUi();
        reloadCombos();
        reloadOrdersTable();
        resetDraft();
    }

    private void buildUi() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildTopBar(), BorderLayout.NORTH);

        JPanel draftPanel = buildDraftPanel();
        JPanel savedPanel = buildSavedOrdersPanel();

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, draftPanel, savedPanel);
        split.setResizeWeight(0.50);
        split.setDividerLocation(300);
        split.setOneTouchExpandable(true);

        add(split, BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(10, 10));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton back = new JButton("Back");
        back.addActionListener(e -> app.showDashboard());
        left.add(back);

        JButton refresh = new JButton("Refresh Lists");
        refresh.addActionListener(e -> {
            reloadCombos();
            reloadOrdersTable();
        });
        left.add(refresh);

        top.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(new JLabel("Status: DRAFT (on Save)"));
        right.add(draftTotalLabel);
        top.add(right, BorderLayout.EAST);

        return top;
    }

    private JPanel buildDraftPanel() {
        JPanel draftPanel = new JPanel(new BorderLayout(10, 10));
        draftPanel.setBorder(BorderFactory.createTitledBorder("Create / Draft Order"));

        draftTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        draftPanel.add(new JScrollPane(draftTable), BorderLayout.CENTER);
        draftPanel.add(buildDraftControls(), BorderLayout.SOUTH);

        return draftPanel;
    }

    private JPanel buildDraftControls() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        installDisplayRenderer(customerCombo);
        installDisplayRenderer(productCombo);

        gc.gridx = 0;
        gc.gridy = 0;
        form.add(new JLabel("Customer:"), gc);

        gc.gridx = 1;
        gc.gridy = 0;
        gc.gridwidth = 3;
        form.add(customerCombo, gc);
        gc.gridwidth = 1;

        gc.gridx = 0;
        gc.gridy = 1;
        form.add(new JLabel("Product:"), gc);

        gc.gridx = 1;
        gc.gridy = 1;
        gc.gridwidth = 2;
        form.add(productCombo, gc);
        gc.gridwidth = 1;

        gc.gridx = 3;
        gc.gridy = 1;
        form.add(new JLabel("Qty:"), gc);

        gc.gridx = 4;
        gc.gridy = 1;
        form.add(qtyField, gc);

        JButton addItem = new JButton("Add Item");
        addItem.addActionListener(e -> onAddItem());

        JButton removeItem = new JButton("Remove Selected Item");
        removeItem.addActionListener(e -> onRemoveSelectedDraftItem());

        gc.gridx = 5;
        gc.gridy = 1;
        form.add(addItem, gc);

        gc.gridx = 6;
        gc.gridy = 1;
        form.add(removeItem, gc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveDraft = new JButton("Save Draft");
        saveDraft.addActionListener(e -> onSaveDraft());
        actions.add(saveDraft);

        JButton clear = new JButton("Clear Draft");
        clear.addActionListener(e -> resetDraft());
        actions.add(clear);

        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 7;
        form.add(actions, gc);

        return form;
    }

    private JPanel buildSavedOrdersPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createTitledBorder("Saved Orders"));

        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p.add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton payDraft = new JButton("Pay Selected Draft Order");
        payDraft.addActionListener(e -> onPaySelectedDraftOrder());
        actions.add(payDraft);

        JButton cancelPaid = new JButton("Cancel Selected Paid Order");
        cancelPaid.addActionListener(e -> onCancelSelectedPaidOrder());
        actions.add(cancelPaid);

        JButton deleteDraft = new JButton("Delete Draft Order");
        deleteDraft.addActionListener(e -> onDeleteSelectedDraftOrder());
        actions.add(deleteDraft);

        JButton reload = new JButton("Reload Orders");
        reload.addActionListener(e -> reloadOrdersTable());
        actions.add(reload);

        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private void reloadCombos() {
        DefaultComboBoxModel<Customer> cm = new DefaultComboBoxModel<>();
        for (Customer c : customerRepo.getAll()) cm.addElement(c);
        customerCombo.setModel(cm);

        DefaultComboBoxModel<Product> pm = new DefaultComboBoxModel<>();
        for (Product pr : productRepo.getAll()) pm.addElement(pr);
        productCombo.setModel(pm);
    }

    private void reloadOrdersTable() {
        List<Order> orders = new ArrayList<>(orderRepo.getAll());
        orders.sort(Comparator.comparing(Order::getCreatedAt).reversed());
        ordersModel.setRows(orders);
        ordersTable.clearSelection();
    }

    private void resetDraft() {
        draftModel.setRows(new ArrayList<>());
        qtyField.setText("");
        updateDraftTotal();
    }

    private void onAddItem() {
        Customer customer = (Customer) customerCombo.getSelectedItem();
        if (customer == null) {
            AppDialogs.showError(this, "Select a customer first.");
            return;
        }

        Product product = (Product) productCombo.getSelectedItem();
        if (product == null) {
            AppDialogs.showError(this, "Select a product first.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyField.getText().trim());
        } catch (NumberFormatException ex) {
            AppDialogs.showError(this, "Quantity must be a valid integer.", ex);
            return;
        }

        if (qty <= 0) {
            AppDialogs.showError(this, "Quantity must be > 0.");
            return;
        }

        if (product.getStock() < qty) {
            AppDialogs.showError(this, "Not enough stock. Available: " + product.getStock());
            return;
        }

        List<OrderItem> rows = draftModel.getRows();
        for (int i = 0; i < rows.size(); i++) {
            OrderItem it = rows.get(i);
            if (it.getProductId().equals(product.getId())) {
                int newQty = it.getQuantity() + qty;
                if (product.getStock() < newQty) {
                    AppDialogs.showError(this, "Not enough stock for combined quantity. Available: " + product.getStock());
                    return;
                }
                rows.set(i, new OrderItem(it.getProductId(), it.getProductName(), newQty, it.getUnitPrice()));
                draftModel.setRows(rows);
                qtyField.setText("");
                updateDraftTotal();
                return;
            }
        }

        rows.add(new OrderItem(product.getId(), product.getName(), qty, product.getUnitPrice()));
        draftModel.setRows(rows);
        qtyField.setText("");
        updateDraftTotal();
    }

    private void onRemoveSelectedDraftItem() {
        int row = draftTable.getSelectedRow();
        if (row < 0) {
            AppDialogs.showError(this, "Select an item to remove.");
            return;
        }
        List<OrderItem> rows = draftModel.getRows();
        rows.remove(row);
        draftModel.setRows(rows);
        updateDraftTotal();
    }

    // Upper section: SAVE AS DRAFT (no stock change)
    private void onSaveDraft() {
        Customer customer = (Customer) customerCombo.getSelectedItem();
        if (customer == null) {
            AppDialogs.showError(this, "Select a customer.");
            return;
        }

        List<OrderItem> items = draftModel.getRows();
        if (items.isEmpty()) {
            AppDialogs.showError(this, "Add at least one item.");
            return;
        }

        String orderId = IdGenerator.newId("ORD");
        Order draftOrder = new Order(
                orderId,
                customer.getId(),
                customer.getName(),
                items,
                OrderStatus.DRAFT
        );

        List<String> errors = draftOrder.validate();
        if (!errors.isEmpty()) {
            AppDialogs.showError(this, String.join("\n", errors));
            return;
        }

        try {
            orderRepo.add(draftOrder);
            orderRepo.saveAll();

            AppDialogs.showInfo(this, "Draft order saved. Total: " + String.format("%.2f", draftOrder.total()));
            resetDraft();
            reloadOrdersTable();
        } catch (RuntimeException ex) {
            AppDialogs.showError(this, "Failed to save draft order.", ex);
        }
    }

    // Lower section: PAY SELECTED DRAFT (deduct stock + set PAID)
    private void onPaySelectedDraftOrder() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) {
            AppDialogs.showError(this, "Select a DRAFT order to pay.");
            return;
        }

        Order selected = ordersModel.getAt(row);

        if (selected.getStatus() != OrderStatus.DRAFT) {
            AppDialogs.showError(this, "Only DRAFT orders can be paid.");
            return;
        }

        // Re-check stock (current inventory)
        for (OrderItem it : selected.getItems()) {
            Product p = productRepo.findById(it.getProductId());
            if (p == null) {
                AppDialogs.showError(this, "Product missing: " + it.getProductName());
                return;
            }
            if (p.getStock() < it.getQuantity()) {
                AppDialogs.showError(this, "Not enough stock for: " + p.getName() + " (available " + p.getStock() + ")");
                return;
            }
        }

        try {
            // Deduct stock
            for (OrderItem it : selected.getItems()) {
                Product p = productRepo.findById(it.getProductId());
                p.setStock(p.getStock() - it.getQuantity());
                productRepo.update(p);
            }

            // Update order status -> PAID (keep same id/createdAt/items)
            Order paid = new Order(
                    selected.getId(),
                    selected.getCreatedAt(),
                    selected.getCustomerId(),
                    selected.getCustomerName(),
                    selected.getItems(),
                    OrderStatus.PAID
            );

            productRepo.saveAll();
            orderRepo.update(paid);
            orderRepo.saveAll();

            AppDialogs.showInfo(this, "Order paid. Total: " + String.format("%.2f", paid.total()));
            reloadCombos();
            reloadOrdersTable();
        } catch (RuntimeException ex) {
            AppDialogs.showError(this, "Failed to pay order.", ex);
        }
    }

    private void onCancelSelectedPaidOrder() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) {
            AppDialogs.showError(this, "Select an order to cancel.");
            return;
        }

        Order selected = ordersModel.getAt(row);
        if (selected.getStatus() != OrderStatus.PAID) {
            AppDialogs.showError(this, "Only PAID orders can be cancelled.");
            return;
        }

        try {
            // Restock
            for (OrderItem it : selected.getItems()) {
                Product p = productRepo.findById(it.getProductId());
                if (p == null) {
                    AppDialogs.showError(this, "Missing product for restock: " + it.getProductName());
                    return;
                }
                p.setStock(p.getStock() + it.getQuantity());
                productRepo.update(p);
            }

            // Update order status -> CANCELLED
            Order cancelled = new Order(
                    selected.getId(),
                    selected.getCreatedAt(),
                    selected.getCustomerId(),
                    selected.getCustomerName(),
                    selected.getItems(),
                    OrderStatus.CANCELLED
            );

            productRepo.saveAll();
            orderRepo.update(cancelled);
            orderRepo.saveAll();

            AppDialogs.showInfo(this, "Order cancelled and stock restored.");
            reloadCombos();
            reloadOrdersTable();
        } catch (RuntimeException ex) {
            AppDialogs.showError(this, "Failed to cancel order.", ex);
        }
    }

    private void onDeleteSelectedDraftOrder() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) {
            AppDialogs.showError(this, "Select an order to delete.");
            return;
        }

        Order selected = ordersModel.getAt(row);
        if (selected.getStatus() != OrderStatus.DRAFT) {
            AppDialogs.showError(this, "Only DRAFT orders can be deleted.");
            return;
        }

        try {
            orderRepo.deleteById(selected.getId());
            orderRepo.saveAll();
            AppDialogs.showInfo(this, "Draft order deleted.");
            reloadOrdersTable();
        } catch (RuntimeException ex) {
            AppDialogs.showError(this, "Failed to delete draft order.", ex);
        }
    }

    private void updateDraftTotal() {
        Order tmp = new Order("TMP", "TMP", "TMP", draftModel.getRows(), OrderStatus.DRAFT);
        draftTotalLabel.setText("Total: " + String.format("%.2f", tmp.total()));
    }

    private static void installDisplayRenderer(JComboBox<?> combo) {
        combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                String text;
                if (value == null) {
                    text = "";
                } else if (value instanceof Displayable) {
                    text = ((Displayable) value).displayName();
                } else {
                    text = value.toString();
                }
                return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        });
    }

    private static final class DraftItemTableModel extends AbstractTableModel {

        private final String[] cols = {"Product ID", "Product Name", "Qty", "Unit Price", "Line Total"};
        private List<OrderItem> rows = new ArrayList<>();

        public List<OrderItem> getRows() {
            return new ArrayList<>(rows);
        }

        public void setRows(List<OrderItem> newRows) {
            this.rows = newRows == null ? new ArrayList<>() : new ArrayList<>(newRows);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int column) {
            return cols[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            OrderItem it = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> it.getProductId();
                case 1 -> it.getProductName();
                case 2 -> it.getQuantity();
                case 3 -> it.getUnitPrice();
                case 4 -> it.lineTotal();
                default -> "";
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }

    private static final class OrdersTableModel extends AbstractTableModel {

        private final String[] cols = {"Order ID", "Customer", "Status", "Total", "Created At"};
        private List<Order> rows = new ArrayList<>();

        public void setRows(List<Order> newRows) {
            this.rows = newRows == null ? new ArrayList<>() : new ArrayList<>(newRows);
            fireTableDataChanged();
        }

        public Order getAt(int row) {
            return rows.get(row);
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int column) {
            return cols[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Order o = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> o.getId();
                case 1 -> o.getCustomerName();
                case 2 -> o.getStatus().name();
                case 3 -> String.format("%.2f", o.total());
                case 4 -> o.getCreatedAt().toString();
                default -> "";
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}
