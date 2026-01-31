/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author User
 */
public final class IdGenerator {
    private static final SecureRandom RNG = new SecureRandom();
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private IdGenerator() {}

    public static String newId(String prefix) {
        String p = (prefix == null || prefix.isBlank()) ? "ID" : prefix.trim().toUpperCase();
        String ts = LocalDateTime.now().format(TS);
        int rand = RNG.nextInt(10_000);
        return p + "-" + ts + "-" + String.format("%04d", rand);
    }
}
