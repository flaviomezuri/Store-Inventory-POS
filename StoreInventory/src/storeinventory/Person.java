/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.time.LocalDateTime;
import java.util.Objects;
/**
 *
 * @author User
 */
public abstract class Person extends AbstractEntity {

    private String name;
    private String email;
    private String phone;

    protected Person(String id, String name, String email, String phone) {
        super(id);
        this.name = Objects.requireNonNullElse(name, "");
        this.email = Objects.requireNonNullElse(email, "");
        this.phone = Objects.requireNonNullElse(phone, "");
    }

    protected Person(String id, LocalDateTime createdAt, String name, String email, String phone) {
        super(id, createdAt);
        this.name = Objects.requireNonNullElse(name, "");
        this.email = Objects.requireNonNullElse(email, "");
        this.phone = Objects.requireNonNullElse(phone, "");
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = Objects.requireNonNullElse(name, "");
    }

    public final String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        this.email = Objects.requireNonNullElse(email, "");
    }

    public final String getPhone() {
        return phone;
    }

    public final void setPhone(String phone) {
        this.phone = Objects.requireNonNullElse(phone, "");
    }

    @Override
    public String displayName() {
        String n = name == null ? "" : name.trim();
        return n.isEmpty() ? getId() : n;
    }

    @Override
    public String toString() {
        return displayName();
    }
    
    
}
