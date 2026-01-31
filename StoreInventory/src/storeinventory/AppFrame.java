/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public final class AppFrame extends JFrame {

    public static final String CARD_LOGIN = "LOGIN";
    public static final String CARD_DASHBOARD = "DASHBOARD";
    public static final String CARD_PRODUCTS = "PRODUCTS";
    public static final String CARD_CUSTOMERS = "CUSTOMERS";
    public static final String CARD_ORDERS = "ORDERS";
    public static final String CARD_REPORTS = "REPORTS";

    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    public AppFrame(ProductRepository productRepo, CustomerRepository customerRepo, OrderRepository orderRepo) {
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;

        setTitle("Store Inventory + POS");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 650));
        setLayout(new BorderLayout());

        cards.add(new LoginPanel(this), CARD_LOGIN);
        cards.add(new DashboardPanel(this), CARD_DASHBOARD);
        cards.add(new ProductsPanel(this), CARD_PRODUCTS);
        cards.add(new CustomersPanel(this), CARD_CUSTOMERS);
        cards.add(new OrdersPanel(this), CARD_ORDERS);
        cards.add(new ReportsPanel(this), CARD_REPORTS);

        add(cards, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                safeSaveAll();
                dispose();
                System.exit(0);
            }
        });

        pack();
        setLocationRelativeTo(null);
        showLogin();
    }

    public ProductRepository products() {
        return productRepo;
    }

    public CustomerRepository customers() {
        return customerRepo;
    }

    public OrderRepository orders() {
        return orderRepo;
    }

    public void showLogin() {
        cardLayout.show(cards, CARD_LOGIN);
    }

    public void showDashboard() {
        cardLayout.show(cards, CARD_DASHBOARD);
    }

    public void showProducts() {
        cardLayout.show(cards, CARD_PRODUCTS);
    }

    public void showCustomers() {
        cardLayout.show(cards, CARD_CUSTOMERS);
    }

    public void showOrders() {
        cardLayout.show(cards, CARD_ORDERS);
    }

    public void showReports() {
        cardLayout.show(cards, CARD_REPORTS);
    }

    private void safeSaveAll() {
        try {
            productRepo.saveAll();
            customerRepo.saveAll();
            orderRepo.saveAll();
        } catch (RuntimeException ex) {
            System.err.println("[EXIT] Failed saving data.");
            ex.printStackTrace(System.err);
            AppDialogs.showError(this, "Failed saving data on exit.", ex);
        }
    }
}
