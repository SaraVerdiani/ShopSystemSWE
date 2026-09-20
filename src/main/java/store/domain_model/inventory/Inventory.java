package store.domain_model.inventory;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<String, InventoryItem> products;

    public Inventory() {
        this.products = new HashMap<>();
    }

    public void addProduct(InventoryItem item) {
       products.put(item.getProduct().getId(), item);
    }

    public InventoryItem getItem(String id) {
        return products.get(id);
    }

    public Map<String, InventoryItem> getProducts() {
        return products;
    }

    public void setProducts(Map<String, InventoryItem> products) {
        this.products = products;
    }

    public void removeProduct(String id) {
        products.remove(id);
    }

    public boolean exists(String id) {
        return products.containsKey(id);
    }
}
