package store.business_logic.services;

import store.ORM.DAO.InventoryDAO;
import store.ORM.DAO.PositionProductDAO;
import store.domain_model.inventory.InventoryItem;
import store.domain_model.inventory.Product;

import java.util.List;

public class InventoryService {
    private final InventoryDAO inventoryDAO;
    private final PositionProductDAO positionProductDAO;

    public InventoryService(InventoryDAO inventoryDAO, PositionProductDAO positionProductDAO) {
        this.inventoryDAO = inventoryDAO;
        this.positionProductDAO = positionProductDAO;
    }

    public List<InventoryItem> getLowStockItems() {
        return inventoryDAO.getLowStockItems();
    }

    public boolean restockItem(InventoryItem item, int quantityToOrder) {
        if (quantityToOrder <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        item.setTotalQuantity(item.getTotalQuantity() + quantityToOrder);
        return inventoryDAO.updateTotalQuantity(item);
    }

    public boolean decreaseStock(InventoryItem item, int quantityToRemove, int maxRemovable) {
        if (quantityToRemove <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (quantityToRemove > maxRemovable) {
            throw new IllegalArgumentException("Quantity to remove exceeds item quantity in the selected position.");
        }

        item.setTotalQuantity(item.getTotalQuantity() - quantityToRemove);
        return inventoryDAO.updateTotalQuantity(item);
    }

    public int getTotalPlacedQuantityInPosition(String posId) {
        return positionProductDAO.getTotalPlacedQuantityInPosition(posId);
    }

    public List<String> getUnallocatedItems() {
        return inventoryDAO.getUnallocatedItems();
    }

    public InventoryItem getItemById(String id) {
        return inventoryDAO.getItemByid(id);
    }

    public void updateAllocatedQuantity(String productId) {
        inventoryDAO.updateAllocatedQuantity(productId);
    }

    public void addItemToInventory(InventoryItem item) {
        if (item.getCriticalThreshold() < 0) {
            throw new IllegalArgumentException("Critical Threshold cannot be negative.");
        }

        Product product = item.getProduct();

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (product.getBrand() == null || product.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("Brand cannot be empty.");
        }
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        inventoryDAO.addItemToInventory(item);
    }

}
