/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// File: src/storeinventory/DashboardPanel.java
package storeinventory;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Main navigation screen.
 */
public final class DashboardPanel extends JPanel {

    private final AppFrame app;

    public DashboardPanel(AppFrame app) {
        this.app = app;
        buildUi();
    }

    private void buildUi() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Dashboard", SwingConstants.CENTER);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        add(title, gc);

        gc.gridwidth = 1;

        JButton productsBtn = new JButton("Products");
        productsBtn.addActionListener(e -> app.showProducts());
        gc.gridx = 0;
        gc.gridy = 1;
        add(productsBtn, gc);

        JButton customersBtn = new JButton("Customers");
        customersBtn.addActionListener(e -> app.showCustomers());
        gc.gridx = 1;
        gc.gridy = 1;
        add(customersBtn, gc);

        JButton ordersBtn = new JButton("Orders");
        ordersBtn.addActionListener(e -> app.showOrders());
        gc.gridx = 0;
        gc.gridy = 2;
        add(ordersBtn, gc);

        JButton reportsBtn = new JButton("Reports");
        reportsBtn.addActionListener(e -> app.showReports());
        gc.gridx = 1;
        gc.gridy = 2;
        add(reportsBtn, gc);

        JButton saveBtn = new JButton("Save All Data");
        saveBtn.addActionListener(e -> {
            try {
                app.products().saveAll();
                app.customers().saveAll();
                app.orders().saveAll();
                AppDialogs.showInfo(this, "Saved", "All data saved to local files.");
            } catch (RuntimeException ex) {
                AppDialogs.showError(this, "Failed to save data.", ex);
            }
        });
        gc.gridx = 0;
        gc.gridy = 3;
        add(saveBtn, gc);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> app.showLogin());
        gc.gridx = 1;
        gc.gridy = 3;
        add(logoutBtn, gc);
    }
}
