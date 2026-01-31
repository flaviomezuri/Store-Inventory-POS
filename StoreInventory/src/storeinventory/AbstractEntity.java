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
 *
 * @author User
 */
public abstract class AbstractEntity implements Displayable, Validatable {

    private final String id;
    private final LocalDateTime createdAt;

    protected AbstractEntity(String id) {
        this(id, LocalDateTime.now());
    }

    protected AbstractEntity(String id, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public final String getId() {
        return id;
    }

    public final LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * CSV header line for this entity type.
     */
    public abstract String csvHeader();

    /**
     * One CSV record line representing this entity.
     */
    public abstract String toCsv();

    @Override
    public String displayName() {
        return id;
    }

    @Override
    public List<String> validate() {
        return new ArrayList<>();
    }
}
