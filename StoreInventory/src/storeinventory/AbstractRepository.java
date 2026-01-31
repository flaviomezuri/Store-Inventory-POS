/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
public abstract class AbstractRepository<T extends AbstractEntity> {

    private final Path dataDir;
    private final List<T> items = new ArrayList<>();

    protected AbstractRepository(Path dataDir) {
        this.dataDir = dataDir;
    }

    public final List<T> getAll() {
        return new ArrayList<>(items);
    }

    public final int size() {
        return items.size();
    }

    public final void add(T entity) {
        if (entity == null) throw new IllegalArgumentException("entity cannot be null");
        if (findById(entity.getId()) != null) {
            throw new IllegalArgumentException("Duplicate id: " + entity.getId());
        }
        items.add(entity);
    }

    public final void update(T entity) {
        if (entity == null) throw new IllegalArgumentException("entity cannot be null");
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(entity.getId())) {
                items.set(i, entity);
                return;
            }
        }
        throw new IllegalArgumentException("Not found id: " + entity.getId());
    }

    public final boolean deleteById(String id) {
        if (id == null) return false;
        for (int i = 0; i < items.size(); i++) {
            if (id.equals(items.get(i).getId())) {
                items.remove(i);
                return true;
            }
        }
        return false;
    }

    public final T findById(String id) {
        if (id == null) return null;
        for (T it : items) {
            if (id.equals(it.getId())) return it;
        }
        return null;
    }

    public final void clear() {
        items.clear();
    }

    public final void load() {
        items.clear();
        ensureDataDir();

        Path file = filePath();
        if (!Files.exists(file)) return;

        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line = br.readLine(); // header
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    T parsed = parse(line);
                    if (parsed != null) items.add(parsed);
                } catch (RuntimeException ex) {
                    System.err.println("[LOAD] Skipped bad line in " + file.getFileName() + ": " + line);
                    ex.printStackTrace(System.err);
                }
            }
        } catch (IOException ex) {
            System.err.println("[LOAD] Failed reading " + file.toAbsolutePath());
            ex.printStackTrace(System.err);
        }
    }

    public final void saveAll() {
        ensureDataDir();
        Path file = filePath();

        try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            bw.write(csvHeader());
            bw.newLine();

            for (T it : items) {
                bw.write(it.toCsv());
                bw.newLine();
            }
        } catch (IOException ex) {
            System.err.println("[SAVE] Failed writing " + file.toAbsolutePath());
            ex.printStackTrace(System.err);
        }
    }

    public final boolean deleteFile() {
        Path file = filePath();
        try {
            return Files.deleteIfExists(file);
        } catch (IOException ex) {
            System.err.println("[DELETE] Failed deleting " + file.toAbsolutePath());
            ex.printStackTrace(System.err);
            return false;
        }
    }

    protected final Path filePath() {
        return dataDir.resolve(fileName());
    }

    private void ensureDataDir() {
        try {
            Files.createDirectories(dataDir);
        } catch (IOException ex) {
            System.err.println("[DATA] Failed creating data directory: " + dataDir.toAbsolutePath());
            ex.printStackTrace(System.err);
        }
    }

    protected abstract String fileName();

    protected abstract String csvHeader();

    protected abstract T parse(String csvLine);
}