/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author User
 */
public final class CustomerRepository extends AbstractRepository<Customer> {

    public CustomerRepository(Path dataDir) {
        super(dataDir);
    }

    @Override
    protected String fileName() {
        return "customers.csv";
    }

    @Override
    protected String csvHeader() {
        return "id,createdAt,name,email,phone";
    }

    @Override
    protected Customer parse(String csvLine) {
        String[] parts = CsvUtil.splitLine(csvLine);
        if (parts.length < 5) throw new IllegalArgumentException("Invalid customer csv: " + csvLine);

        String id = parts[0];
        LocalDateTime createdAt = LocalDateTime.parse(parts[1]);
        String name = parts[2];
        String email = parts[3];
        String phone = parts[4];

        return new Customer(id, createdAt, name, email, phone);
    }

    public Customer findByEmailLinear(String email) {
        if (email == null) return null;
        String target = email.trim().toLowerCase();
        for (Customer c : getAll()) {
            String e = c.getEmail() == null ? "" : c.getEmail().trim().toLowerCase();
            if (target.equals(e)) return c;
        }
        return null;
    }

    public List<Customer> findByNameContains(String token) {
        List<Customer> out = new ArrayList<>();
        if (token == null || token.isBlank()) return out;

        String t = token.trim().toLowerCase();
        for (Customer c : getAll()) {
            String n = c.getName() == null ? "" : c.getName().toLowerCase();
            if (n.contains(t)) out.add(c);
        }
        return out;
    }
}
