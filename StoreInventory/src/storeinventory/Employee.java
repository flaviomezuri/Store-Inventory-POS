/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Optional hierarchy to mirror your bank project:
 * Person -> Employee -> Cashier/InventoryClerk/etc.
 */
public abstract class Employee extends Person {

    private String role;
    private double hourlyRate;

    protected Employee(String id, String name, String email, String phone, String role, double hourlyRate) {
        super(id, name, email, phone);
        this.role = Objects.requireNonNullElse(role, "");
        this.hourlyRate = hourlyRate;
    }

    protected Employee(String id, LocalDateTime createdAt, String name, String email, String phone, String role, double hourlyRate) {
        super(id, createdAt, name, email, phone);
        this.role = Objects.requireNonNullElse(role, "");
        this.hourlyRate = hourlyRate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = Objects.requireNonNullElse(role, "");
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        errors.addAll(Validators.requireNonBlank("Name", getName(), 2));

        if (getEmail() == null || getEmail().isBlank()) errors.add("Email is required.");
        else if (!Validators.isValidEmail(getEmail())) errors.add("Email format invalid.");

        if (getPhone() == null || getPhone().isBlank()) errors.add("Phone is required.");
        else if (!Validators.isValidPhone(getPhone())) errors.add("Phone format invalid.");

        if (role == null || role.isBlank()) errors.add("Role is required.");
        if (hourlyRate < 0) errors.add("Hourly rate must be >= 0.");

        return errors;
    }
}

