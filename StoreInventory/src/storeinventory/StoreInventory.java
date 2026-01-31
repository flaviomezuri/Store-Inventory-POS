// File: src/storeinventory/StoreInventory.java
package storeinventory;

import java.nio.file.Path;
import javax.swing.SwingUtilities;

public class StoreInventory {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Path dataDir = Path.of("data");

            ProductRepository productRepo = new ProductRepository(dataDir);
            CustomerRepository customerRepo = new CustomerRepository(dataDir);
            OrderRepository orderRepo = new OrderRepository(dataDir);

            productRepo.load();
            customerRepo.load();
            orderRepo.load();

            AppFrame frame = new AppFrame(productRepo, customerRepo, orderRepo);
            frame.setVisible(true);
        });
    }
}
