package store.business_logic.services;

import store.ORM.DAO.*;
import store.business_logic.filter.ProductFilter;
import store.domain_model.inventory.*;
import store.domain_model.logistics.PositionProduct;

import java.util.*;


public class WorkerService {

    public final InventoryDAO inventoryDAO;
    public final ProductDAO productDAO;
    public final PositionDAO positionDAO;
    public final ClothingDAO clothingDAO;
    public final FurnitureDAO furnitureDAO;
    public final FoodDAO foodDAO;
    public final PositionProductDAO positionProductDAO;

    public WorkerService(InventoryDAO inventoryDAO,
                         ProductDAO productDAO,
                         PositionDAO positionDAO,
                         ClothingDAO clothingDAO,
                         FurnitureDAO furnitureDAO,
                         FoodDAO foodDAO,
                         PositionProductDAO positionProductDAO
    )
    {
        this.inventoryDAO = inventoryDAO;
        this.productDAO = productDAO;
        this.positionDAO = positionDAO;
        this.clothingDAO = clothingDAO;
        this.foodDAO = foodDAO;
        this.furnitureDAO = furnitureDAO;
        this.positionProductDAO = positionProductDAO;

    }

    public Map<InventoryItem, List<PositionProduct>> searchProduct(String id, ProductFilter filter) {
        Map<InventoryItem, List<PositionProduct>> foundItems = new HashMap<>();
        if(filter == null) {
            InventoryItem item = inventoryDAO.getItemByid(id);
            if (item != null) {
                List<PositionProduct> allocations = positionProductDAO.getAllocationsForProduct(item.getProduct());
                foundItems.put(item, allocations);
                for(PositionProduct alloc : allocations) {
                    item.addAllocation(alloc);
                }
            }
        } else {
            List<String> foundIds = productDAO.searchDynamic(id, filter);
            for(String foundId: foundIds) {
                InventoryItem item = inventoryDAO.getItemByid(foundId);
                if(item != null) {
                    List<PositionProduct> allocations = positionProductDAO.getAllocationsForProduct(item.getProduct());
                    foundItems.put(item, allocations);

                    for(PositionProduct alloc : allocations) {
                        item.addAllocation(alloc);
                    }
                }
            }
        }
        return foundItems;
    }

    public boolean editItem(InventoryItem originalItem, Product newProduct) {
        
        if (newProduct.getName() == null || newProduct.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (newProduct.getBrand() == null || newProduct.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("Product brand cannot be empty.");
        }
        if (newProduct.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        if (newProduct instanceof Furniture fr) {
            if (fr.getHeight() <= 0 || fr.getWidth() <= 0 || fr.getLength() <= 0) {
                throw new IllegalArgumentException("Furniture dimensions must be greater than zero.");
            }
        }
        
        boolean productUpdate = productDAO.update(newProduct);
        boolean specificUpdate = false;

        switch (newProduct) {
            case Clothes c -> specificUpdate = clothingDAO.update(c);
            case Food fd -> specificUpdate = foodDAO.update(fd);
            case Furniture fr -> specificUpdate = furnitureDAO.update(fr);
            default -> throw new IllegalStateException("Unexpected value: " + newProduct);
        }
        
        if (productUpdate && specificUpdate) {
            originalItem.setProduct(newProduct);
            return true;
        }

        return false;
    }
}
