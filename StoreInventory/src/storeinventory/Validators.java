/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
/**
 *
 * @author User
 */
public final class Validators {
    private Validators() {}

    // Complex regex #1: email with length constraints + domain structure.
    private static final Pattern EMAIL = Pattern.compile(
            "^(?=.{1,254}$)(?=.{1,64}@)[A-Za-z0-9._%+-]+@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$"
    );

    // Complex regex #2: strong password: lower/upper/digit/special, length >= 8.
    private static final Pattern STRONG_PASSWORD = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$"
    );

    // SKU example: ABC-1234-Z9
    private static final Pattern SKU = Pattern.compile("^[A-Z]{3}-\\d{4}-[A-Z0-9]{2}$");

    // Phone: supports +country, optional area code, and separators.
    private static final Pattern PHONE = Pattern.compile(
            "^(?:" + "(?:\\+355|00355)\\s*(?:\\(0\\)\\s*)?" + "|" + "0" + ")" + "(?:" + "6\\d{1}" + "|" + "(?:[2-5]\\d{1,2})" + ")" + "[\\s-]*\\d{3}[\\s-]*\\d{4}$" 
    );

    // Overload #1
    public static List<String> requireNonBlank(String field, String value) {
        return requireNonBlank(field, value, 1);
    }

    // Overload #2
    public static List<String> requireNonBlank(String field, String value, int minLen) {
        List<String> errors = new ArrayList<>();
        if (value == null || value.isBlank()) {
            errors.add(field + " is required.");
            return errors;
        }
        if (value.trim().length() < minLen) {
            errors.add(field + " must be at least " + minLen + " characters.");
        }
        return errors;
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    public static boolean isStrongPassword(String password) {
        return password != null && STRONG_PASSWORD.matcher(password).matches();
    }

    public static boolean isValidSku(String sku) {
        return sku != null && SKU.matcher(sku.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE.matcher(phone.trim()).matches();
    }
}
