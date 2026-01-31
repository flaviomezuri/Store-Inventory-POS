/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// File: src/storeinventory/InventoryClerk.java
package storeinventory;

import java.time.LocalDateTime;
import java.util.Objects;

public final class InventoryClerk extends Employee {

    private String section; // e.g. "Aisle 1", "Backroom"

    public InventoryClerk(String id, String name, String email, String phone, double hourlyRate, String section) {
        super(id, name, email, phone, "InventoryClerk", hourlyRate);
        this.section = Objects.requireNonNullElse(section, "");
    }

    public InventoryClerk(String id, LocalDateTime createdAt, String name, String email, String phone, double hourlyRate, String section) {
        super(id, createdAt, name, email, phone, "InventoryClerk", hourlyRate);
        this.section = Objects.requireNonNullElse(section, "");
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = Objects.requireNonNullElse(section, "");
    }

    @Override
    public String csvHeader() {
        return "id,createdAt,name,email,phone,role,hourlyRate,section";
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
                CsvUtil.escape(section)
        );
    }

    @Override
    public String displayName() {
        return getName() + " - Inventory";
    }
}
