// File: src/storeinventory/LoginPanel.java
package storeinventory;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public final class LoginPanel extends JPanel {

    private final AppFrame app;

    private final JTextField usernameOrEmailField = new JTextField(24);
    private final JPasswordField passwordField = new JPasswordField(24);

    private record Credential(String username, String password) {}

    // 3 fixed users (edit these)
    private static final List<Credential> USERS = List.of(
            new Credential("admin", "Admin@123!"),
            new Credential("cashier", "Cashier@123!"),
            new Credential("manager", "Manager@123!")
    );

    public LoginPanel(AppFrame app) {
        this.app = app;
        buildUi();
    }

    private void buildUi() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        add(new JLabel("Store Inventory Login"), gc);

        gc.gridwidth = 1;

        gc.gridx = 0;
        gc.gridy = 1;
        add(new JLabel("Username:"), gc);

        gc.gridx = 1;
        gc.gridy = 1;
        add(usernameOrEmailField, gc);

        gc.gridx = 0;
        gc.gridy = 2;
        add(new JLabel("Password:"), gc);

        gc.gridx = 1;
        gc.gridy = 2;
        add(passwordField, gc);

        JButton loginBtn = new JButton("Login");
        JButton clearBtn = new JButton("Clear");

        loginBtn.addActionListener(e -> onLogin());
        clearBtn.addActionListener(e -> onClear());

        gc.gridx = 1;
        gc.gridy = 3;
        add(loginBtn, gc);

        gc.gridx = 0;
        gc.gridy = 3;
        add(clearBtn, gc);

        gc.gridx = 0;
        gc.gridy = 4;
        gc.gridwidth = 2;
    }

    private void onClear() {
        usernameOrEmailField.setText("");
        passwordField.setText("");
        usernameOrEmailField.requestFocusInWindow();
    }

    private void onLogin() {
        String user = usernameOrEmailField.getText() == null ? "" : usernameOrEmailField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty()) {
            AppDialogs.showError(this, "Username is required.");
            return;
        }
        if (pass.isEmpty()) {
            AppDialogs.showError(this, "Password is required.");
            return;
        }

        // Optional: keep your strong password requirement
        if (!Validators.isStrongPassword(pass)) {
            AppDialogs.showError(this, "Password must be strong (upper/lower/digit/special, >= 8).");
            return;
        }

        boolean ok = false;
        for (Credential c : USERS) {
            if (c.username().equalsIgnoreCase(user) && c.password().equals(pass)) {
                ok = true;
                break;
            }
        }

        if (!ok) {
            AppDialogs.showError(this, "Invalid username or password.");
            System.err.println("[LOGIN] FAIL user=" + user);
            return;
        }

        System.out.println("[LOGIN] OK user=" + user);
        AppDialogs.showInfo(this, "Login successful.");
        app.showDashboard();
    }
}
