/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// File: src/storeinventory/CustomersPanel.java
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

public final class CustomersPanel extends JPanel {

    private final AppFrame app;
    private final CustomerRepository repo;

    private final CustomerTableModel tableModel = new CustomerTableModel();
    private final JTable table = new JTable(tableModel);

    private final JTextField nameField = new JTextField(18);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(16);

    private final JTextField searchField = new JTextField(18);

    public CustomersPanel(AppFrame app) {
        this.app = app;
        this.repo = app.customers();
        buildUi();
        reloadFromRepo();
    }

    private void buildUi() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildTopBar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            Customer c = tableModel.getAt(row);
            nameField.setText(c.getName());
            emailField.setText(c.getEmail());
            phoneField.setText(c.getPhone());
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

        JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        search.add(new JLabel("Search Name:"));
        search.add(searchField);

        JButton findBtn = new JButton("Find");
        findBtn.addActionListener(e -> onSearch());
        search.add(findBtn);

        JButton clearSearchBtn = new JButton("Clear Search");
        clearSearchBtn.addActionListener(e -> {
            searchField.setText("");
            reloadFromRepo();
        });
        search.add(clearSearchBtn);

        top.add(search, BorderLayout.CENTER);
        return top;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Customer Form"));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0;
        gc.gridy = 0;
        form.add(new JLabel("Name:"), gc);

        gc.gridx = 1;
        gc.gridy = 0;
        form.add(nameField, gc);

        gc.gridx = 2;
        gc.gridy = 0;
        form.add(new JLabel("Email:"), gc);

        gc.gridx = 3;
        gc.gridy = 0;
        form.add(emailField, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        form.add(new JLabel("Phone:"), gc);

        gc.gridx = 1;
        gc.gridy = 1;
        form.add(phoneField, gc);

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
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        nameField.requestFocusInWindow();
    }

    private void onAdd() {
        Customer c = new Customer(
                IdGenerator.newId("CST"),
                nameField.getText(),
                normalizeEmail(emailField.getText()),
                normalizePhone(phoneField.getText())
        );

        List<String> errors = validateCustomer(c);
        if (!errors.isEmpty()) {
            AppDialogs.showError(this, String.join("\n", errors));
            return;
        }

        try {
            repo.add(c);
            repo.saveAll();
            reloadFromRepo();
            clearForm();
            AppDialogs.showInfo(this, "Customer added.");
        } catch (RuntimeException ex) {
            AppDialogs.showError(this, "Failed to add customer.", ex);
        }
    }

    private void onUpdateSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            AppDialogs.showError(this, "Select a customer to update.");
            return;
        }

        Customer existing = tableModel.getAt(row);

        Customer updated = new Customer(
                existing.getId(),
                existing.getCreatedAt(),
                nameField.getText(),
                normalizeEmail(emailField.getText()),
                normalizePhone(phoneField.getText())
        );

        List<String> errors = validateCustomer(updated);
        if (!errors.isEmpty()) {
            AppDialogs.showError(this, String.join("\n", errors));
            return;
        }

        try {
            repo.update(updated);
            repo.saveAll();
            reloadFromRepo();
            AppDialogs.showInfo(this, "Customer updated.");
        } catch (RuntimeException ex) {
            AppDialogs.showError(this, "Failed to update customer.", ex);
        }
    }

    private void onDeleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            AppDialogs.showError(this, "Select a customer to delete.");
            return;
        }

        Customer existing = tableModel.getAt(row);

        try {
            boolean ok = repo.deleteById(existing.getId());
            repo.saveAll();
            reloadFromRepo();
            clearForm();
            if (ok) AppDialogs.showInfo(this, "Customer deleted.");
            else AppDialogs.showError(this, "Delete failed (not found).");
        } catch (RuntimeException ex) {
            AppDialogs.showError(this, "Failed to delete customer.", ex);
        }
    }

    private void onSearch() {
        String token = searchField.getText() == null ? "" : searchField.getText().trim();
        if (token.isEmpty()) {
            AppDialogs.showError(this, "Enter a name token to search.");
            return;
        }
        tableModel.setRows(repo.findByNameContains(token));
        table.clearSelection();
    }

    private List<String> validateCustomer(Customer c) {
        List<String> errors = new ArrayList<>();
        errors.addAll(c.validate());

        if (c.getEmail() != null && !c.getEmail().isBlank() && !Validators.isValidEmail(c.getEmail())) {
            errors.add("Email format invalid.");
        }
        if (c.getPhone() != null && !c.getPhone().isBlank() && !Validators.isValidPhone(c.getPhone())) {
            errors.add("Phone format invalid.");
        }
        return errors;
    }

    private static String normalizeEmail(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private static String normalizePhone(String s) {
        return s == null ? "" : s.trim();
    }

    private static final class CustomerTableModel extends AbstractTableModel {

        private final String[] cols = {"ID", "Name", "Email", "Phone", "Created At"};
        private List<Customer> rows = new ArrayList<>();

        public void setRows(List<Customer> newRows) {
            this.rows = newRows == null ? new ArrayList<>() : new ArrayList<>(newRows);
            fireTableDataChanged();
        }

        public Customer getAt(int row) {
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
            Customer c = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> c.getId();
                case 1 -> c.getName();
                case 2 -> c.getEmail();
                case 3 -> c.getPhone();
                case 4 -> c.getCreatedAt().toString();
                default -> "";
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}
