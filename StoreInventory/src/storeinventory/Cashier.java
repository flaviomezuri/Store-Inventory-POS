/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Cashier extends Employee {

    private String shift; // e.g. "Morning", "Evening"

    public Cashier(String id, String name, String email, String phone, double hourlyRate, String shift) {
        super(id, name, email, phone, "Cashier", hourlyRate);
        this.shift = Objects.requireNonNullElse(shift, "");
    }

    public Cashier(String id, LocalDateTime createdAt, String name, String email, String phone, double hourlyRate, String shift) {
        super(id, createdAt, name, email, phone, "Cashier", hourlyRate);
        this.shift = Objects.requireNonNullElse(shift, "");
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = Objects.requireNonNullElse(shift, "");
    }

    @Override
    public String csvHeader() {
        return "id,createdAt,name,email,phone,role,hourlyRate,shift";
    }

    @Override
    public String toCsv() {
        return String.join(",",
                CsvUtil.escape(getId()),
                CsvUtil.escape(getCreatedAt().toString()),
                CsvUtil.escape(getName()),
                CsvUtil.escape(getEmail()),
                CsvUtil.escape(getPhone()),
                CsvUtil.escape(getRole()),
                CsvUtil.escape(Double.toString(getHourlyRate())),
                CsvUtil.escape(shift)
        );
    }

    @Override
    public String displayName() {
        return getName() + " - Cashier";
    }
}

