/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author User
 */
public final class Customer extends Person {

    public Customer(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    public Customer(String id, LocalDateTime createdAt, String name, String email, String phone) {
        super(id, createdAt, name, email, phone);
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (getName() == null || getName().isBlank()) errors.add("Name is required.");
        if (getEmail() == null || getEmail().isBlank()) errors.add("Email is required.");
        if (getPhone() == null || getPhone().isBlank()) errors.add("Phone is required.");

        // Regex checks will be added once Validators exists:
        // if (!Validators.isValidEmail(getEmail())) errors.add("Email invalid.");
        // if (!Validators.isValidPhone(getPhone())) errors.add("Phone invalid.");

        return errors;
    }

    @Override
    public String csvHeader() {
        return "id,createdAt,name,email,phone";
    }

    @Override
    public String toCsv() {
        // Csv escaping will be added once CsvUtil exists.
        return String.join(",",
                getId(),
                getCreatedAt().toString(),
                getName(),
                getEmail(),
                getPhone()
        );
    }
}
