/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.awt.Component;
import javax.swing.JOptionPane;
/**
 *
 * @author User
 */
public final class AppDialogs {
    
    
    private AppDialogs() {}

    public static void showInfo(Component parent, String message) {
        System.out.println("[INFO] " + message);
        JOptionPane.showMessageDialog(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showInfo(Component parent, String title, String message) {
        System.out.println("[INFO] " + title + ": " + message);
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        System.err.println("[ERROR] " + message);
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showError(Component parent, String message, Exception ex) {
        System.err.println("[ERROR] " + message);
        if (ex != null) ex.printStackTrace(System.err);

        String details = (ex == null || ex.getMessage() == null) ? "" : ("\n\nDetails: " + ex.getMessage());
        JOptionPane.showMessageDialog(parent, message + details, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
