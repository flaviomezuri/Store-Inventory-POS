/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// File: src/storeinventory/ProductsPanel.java
package storeinventory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

public final class ProductsPanel extends JPanel {

    private final AppFrame app;
    private final ProductRepository repo;

    private final ProductTableModel tableModel = new ProductTableModel();
    private final JTable table = new JTable(tableModel);

    private final JTextField skuField = new JTextField(14);
    private final JTextField nameField = new JTextField(18);
    private final JTextField priceField = new JTextField(10);
    private final JTextField stockField = new JTextField(10);

    private final JTextField searchSkuField = new JTextField(14);

    public ProductsPanel(AppFrame app) {
        this.app = app;
        this.repo = app.products();
        buildUi();
        reloadFromRepo();
    }

    private void buildUi() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            Product p = tableModel.getAt(row);
            skuField.setText(p.getSku());
            nameField.setText(p.getName());
            priceField.setText(Double.toString(p.getUnitPrice()));
            stockField.setText(Integer.toString(p.getStock()));
        });
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(10, 10));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton back = new JButton("Back");
        back.addActionListener(e -> app.showDashboard());
        nav.add(back);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> reloadFromRepo());
        nav.add(refresh);

        top.add(nav, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton sortName = new JButton("Sort: Name");
        sortName.addActionListener(e -> tableModel.setRows(Algorithms.sortProductsByName(tableModel.getRows())));
        actions.add(sortName);

        JButton sortStock = new JButton("Sort: Stock");
        sortStock.addActionListener(e -> tableModel.setRows(Algorithms.sortProductsByStock(tableModel.getRows())));
        actions.add(sortStock);

        JButton sortSku = new JButton("Sort: SKU");
        sortSku.addActionListener(e -> tableModel.setRows(Algorithms.sortProductsBySku(tableModel.getRows())));
        actions.add(sortSku);

        actions.add(new JLabel("Search SKU:"));
        actions.add(searchSkuField);

        JButton search = new JButton("Find");
        search.addActionListener(e -> onSearchSku());
        actions.add(search);

        top.add(actions, BorderLayout.CENTER);
        return top;
    }

    private JScrollPane buildTable() {
        return new JScrollPane(table);
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Product Form"));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0;
        gc.gridy = 0;
        form.add(new JLabel("SKU:"), gc);

        gc.gridx = 1;
        gc.gridy = 0;
        form.add(skuField, gc);

        gc.gridx = 2;
        gc.gridy = 0;
        form.add(new JLabel("Name:"), gc);

        gc.gridx = 3;
        gc.gridy = 0;
        form.add(nameField, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        form.add(new JLabel("Unit Price:"), gc);

        gc.gridx = 1;
        gc.gridy = 1;
        form.add(priceField, gc);

        gc.gridx = 2;
        gc.gridy = 1;
        form.add(new JLabel("Stock:"), gc);

        gc.gridx = 3;
        gc.gridy = 1;
        form.add(stockField, gc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> onAdd());
        buttons.add(addBtn);

        JButton updateBtn = new JButton("Update Selected");
        updateBtn.addActionListener(e -> onUpdateSelected());
        buttons.add(updateBtn);

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> onDeleteSelected());
        buttons.add(deleteBtn);

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> clearForm());
        buttons.add(clearBtn);

        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 4;
        form.add(buttons, gc);

        return form;
    }

    private void reloadFromRepo() {
        tableModel.setRows(repo.getAll());
        table.clearSelection();
    }

    private void clearForm() {
        skuField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        skuField.requestFocusInWindow();
    }

    private void onAdd() {
        Product p;
        try {
            p = buildProductFromFields(IdGenerator.newId("PRD"), null);
        } catch (NumberFormatException ex) {
            AppDialogs.showError(this, "Unit Price and Stock must be valid numbers.", ex);
            return;
        }

        List<String> errors = validateProduct(p);
        if (!errors.isEmpty()) {
            AppDialogs.showError(this, String.join("\n", errors));
            return;
        }

        try {
            repo.add(p);
            repo.saveAll();
            reloadFromRepo();
            clearForm();
            AppDialogs.showInfo(this, "Product added.");
        } catch (RuntimeException ex) {
            AppDialogs.showError(this, "Failed to add product.", ex);
        }
    }

    private void onUpdateSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            AppDialogs.showError(this, "Select a product to update.");
            return;
        }

        Product existing = tableModel.getAt(row);

        Product updated;
        try {
            updated = buildProductFromFields(existing.getId(), existing.getCreatedAt().toString());
        } catch (NumberFormatException ex) {
            AppDialogs.showError(this, "Unit Price and Stock must be valid numbers.", ex);
            return;
        }

        List<String> errors = validateProduct(updated);
        if (!errors.isEmpty()) {
            AppDialogs.showError(this, String.join("\n", errors));
            return;
        }

        try {
            repo.update(updated);
            repo.saveAll();
            reloadFromRepo();
            AppDialogs.showInfo(this, "Product updated.");
        } catch (RuntimeException ex) {
            AppDialogs.showError(this, "Failed to update product.", ex);
        }
    }

    private void onDeleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            AppDialogs.showError(this, "Select a product to delete.");
            return;
        }

        Product existing = tableModel.getAt(row);

        try {
            boolean ok = repo.deleteById(existing.getId());
            repo.saveAll();
            reloadFromRepo();
            clearForm();
            if (ok) AppDialogs.showInfo(this, "Product deleted.");
            else AppDialogs.showError(this, "Delete failed (not found).");
        } catch (RuntimeException ex) {
            AppDialogs.showError(this, "Failed to delete product.", ex);
        }
    }

    private void onSearchSku() {
        String sku = searchSkuField.getText() == null ? "" : searchSkuField.getText().trim();
        if (sku.isEmpty()) {
            AppDialogs.showError(this, "Enter a SKU to search.");
            return;
        }

        List<Product> sorted = Algorithms.sortProductsBySku(tableModel.getRows());
        tableModel.setRows(sorted);

        int idx = Algorithms.binarySearchProductBySku(sorted, sku);
        if (idx < 0) {
            AppDialogs.showError(this, "SKU not found: " + sku);
            table.clearSelection();
            return;
        }

        table.setRowSelectionInterval(idx, idx);
        table.scrollRectToVisible(table.getCellRect(idx, 0, true));
    }

    private Product buildProductFromFields(String id, String createdAtIsoOrNull) {
        String sku = skuField.getText() == null ? "" : skuField.getText().trim().toUpperCase();
        String name = nameField.getText() == null ? "" : nameField.getText().trim();

        double unitPrice = Double.parseDouble(priceField.getText().trim());
        int stock = Integer.parseInt(stockField.getText().trim());

        if (createdAtIsoOrNull == null) {
            return new Product(id, sku, name, unitPrice, stock);
        }

        return new Product(id, java.time.LocalDateTime.parse(createdAtIsoOrNull), sku, name, unitPrice, stock);
    }

    private List<String> validateProduct(Product p) {
        List<String> errors = new ArrayList<>();
        errors.addAll(p.validate());

        if (!p.getSku().isBlank() && !Validators.isValidSku(p.getSku())) {
            errors.add("SKU format invalid. Example: ABC-1234-Z9");
        }

        return errors;
    }

    private static final class ProductTableModel extends AbstractTableModel {

        private final String[] cols = {"ID", "SKU", "Name", "Unit Price", "Stock", "Created At"};
        private List<Product> rows = new ArrayList<>();

        public List<Product> getRows() {
            return new ArrayList<>(rows);
        }

        public void setRows(List<Product> newRows) {
            this.rows = newRows == null ? new ArrayList<>() : new ArrayList<>(newRows);
            fireTableDataChanged();
        }

        public Product getAt(int row) {
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
            Product p = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> p.getId();
                case 1 -> p.getSku();
                case 2 -> p.getName();
                case 3 -> p.getUnitPrice();
                case 4 -> p.getStock();
                case 5 -> p.getCreatedAt().toString();
                default -> "";
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}

