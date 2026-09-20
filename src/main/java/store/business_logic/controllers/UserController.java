package store.business_logic.controllers;

import store.business_logic.filter.ProductFilter;
import store.business_logic.services.InventoryService;
import store.business_logic.services.LogisticsService;
import store.business_logic.services.WorkerService;
import store.domain_model.inventory.InventoryItem;
import store.domain_model.inventory.Product;
import store.domain_model.logistics.Position;
import store.domain_model.logistics.PositionProduct;

import java.util.List;
import java.util.Map;

public class UserController {
    private final InventoryService inventoryService;
    private final LogisticsService logisticsService;
    private final WorkerService workerService;

    public UserController(InventoryService inventoryService, LogisticsService logisticsService, WorkerService workerService) {
        this.inventoryService = inventoryService;
        this.logisticsService = logisticsService;
        this.workerService = workerService;
    }

    public Map<InventoryItem, List<PositionProduct>> searchItem(String id, ProductFilter filter) {
        return workerService.searchProduct(id, filter);
    }

    public boolean restockItem(InventoryItem item, int quantity) {
        return inventoryService.restockItem(item, quantity);
    }

    public boolean decreaseStock(InventoryItem item, int quantity, int maxRemovable) {
        return inventoryService.decreaseStock(item, quantity, maxRemovable);
    }

    public void autoAllocateToBackstock(String productId, int quantity) {
        logisticsService.autoAllocateToBackstock(productId, quantity);
    }

    public boolean placeItem(Position position, InventoryItem item, int quantity) {
        return logisticsService.assignProductPosition(position, item, quantity);
    }

    public boolean removeItem(String positionId, String productId, int quantity) {
        return logisticsService.removeItemsFromPosition(positionId, productId, quantity);
    }

    public boolean moveItem(Position sourcePos, Position destPos, InventoryItem item, int quantity, int maxMovable) {
        return logisticsService.moveItemsFromPosition(sourcePos, destPos, item, quantity, maxMovable);
    }

    public boolean editItem(InventoryItem originalItem, Product newProduct) {
        return workerService.editItem(originalItem, newProduct);
    }

    public int getTotalPlacedQuantityInPosition(String id){
        return inventoryService.getTotalPlacedQuantityInPosition(id);
    }

    public void registerItem(InventoryItem item) {
        inventoryService.addItemToInventory(item);
    }

    public List<InventoryItem> getLowStockItems() {
        return inventoryService.getLowStockItems();
    }

    public List<Position> getAvailablePositions() {
        return logisticsService.getAvailablePositions();
    }

    public List<String> getUnallocatedItems() {
        return inventoryService.getUnallocatedItems();
    }

    public void updateAllocatedQuantity(String productId) {
        inventoryService.updateAllocatedQuantity(productId);
    }

    public InventoryItem getItemById(String id) {
        return inventoryService.getItemById(id);
    }
}
