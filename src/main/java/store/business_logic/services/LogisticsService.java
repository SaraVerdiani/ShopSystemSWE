package store.business_logic.services;

import store.ORM.DAO.InventoryDAO;
import store.ORM.DAO.PositionDAO;
import store.ORM.DAO.PositionProductDAO;
import store.domain_model.inventory.InventoryItem;
import store.domain_model.logistics.Position;
import store.domain_model.logistics.PositionProduct;

import java.util.List;
import java.util.Objects;

public class LogisticsService {

    public final PositionDAO positionDAO;
    public final InventoryDAO inventoryDAO;
    public final PositionProductDAO positionProductDAO;


    public LogisticsService(PositionDAO positionDAO, InventoryDAO inventoryDAO, PositionProductDAO positionProductDAO) {
        this.positionDAO = positionDAO;
        this.inventoryDAO = inventoryDAO;
        this.positionProductDAO = positionProductDAO;
    }

    public void autoAllocateToBackstock(String productId, int quantity) {
        List<Position> availablePositions = positionDAO.getAvailableBackstockPositions();
        int remainingToAllocate = quantity;

        for (Position pos : availablePositions) {
            if (remainingToAllocate <= 0) break;

            int availableSpace = positionDAO.getAvailableSpaceForPosition(pos.getId());

            if(availableSpace > 0) {
                int quantityToAdd = Math.min(remainingToAllocate, availableSpace);

                positionProductDAO.addProductToPosition(pos.getId(), productId, quantityToAdd);

                remainingToAllocate -= quantityToAdd;

                inventoryDAO.updateAllocatedQuantity(productId);

            }
        }

        if(remainingToAllocate > 0) {
            positionProductDAO.addProductToPosition("P00054", productId, remainingToAllocate);
        }
    }

    public boolean assignProductPosition(Position destPosition, InventoryItem item, int quantity) {
        int availableSpace = positionDAO.getAvailableSpaceForPosition(destPosition.getId());

        if (availableSpace >= quantity) {

            boolean success = positionProductDAO.addProductToPosition(destPosition.getId(), item.getProduct().getId(), quantity);

            if (success) {
                if (!Objects.equals(destPosition.getId(), "P00054")) {
                    inventoryDAO.updateAllocatedQuantity(item.getProduct().getId());
                    item.increaseAllocated(quantity);
                }

                boolean positionAlreadyExists = false;
                for (PositionProduct alloc : item.getAllocations()) {
                    if (alloc.getPosition().getId().equals(destPosition.getId())) {
                        alloc.setQuantity(alloc.getQuantity() + quantity);
                        positionAlreadyExists = true;
                        break;
                    }
                }

                if (!positionAlreadyExists) {
                    item.addAllocation(new PositionProduct(destPosition, item.getProduct(), quantity));
                }

                return true;
            }
        }
        return false;
    }

    public boolean removeItemsFromPosition(String posId, String productId, int quantity) {
        if(positionProductDAO.removeItemsFromPosition(posId, productId, quantity)) {
            inventoryDAO.updateAllocatedQuantity(productId);
            return true;
        }
        return false;
    }

    public boolean checkPositionExist(String id) {
        return positionDAO.checkPositionExist(id);
    }

    public boolean moveItemsFromPosition(Position sourcePosition, Position destPosition, InventoryItem item, int quantity, int maxMovable) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (quantity > maxMovable) {
            throw new IllegalArgumentException("Quantity to move exceeds item quantity in the source position.");
        }

        int availableSpace = positionDAO.getAvailableSpaceForPosition(destPosition.getId());
        if (availableSpace < quantity) {
            return false;
        }

        if (positionProductDAO.moveItemsFromPosition(sourcePosition.getId(), destPosition.getId(), item.getProduct().getId(), quantity)) {

            inventoryDAO.updateAllocatedQuantity(item.getProduct().getId());

            item.getAllocations().removeIf(alloc -> {
                if (alloc.getPosition().getId().equals(sourcePosition.getId())) {
                    alloc.setQuantity(alloc.getQuantity() - quantity);
                    return alloc.getQuantity() == 0;
                }
                return false;
            });

            boolean destExists = false;
            for (PositionProduct alloc : item.getAllocations()) {
                if (alloc.getPosition().getId().equals(destPosition.getId())) {
                    alloc.setQuantity(alloc.getQuantity() + quantity);
                    destExists = true;
                    break;
                }
            }
            if (!destExists) {
                item.addAllocation(new PositionProduct(destPosition, item.getProduct(), quantity));
            }

            boolean fromPending = sourcePosition.getId().equals("P00054");
            boolean toPending = destPosition.getId().equals("P00054");

            if (fromPending && !toPending) {
                item.increaseAllocated(quantity);
            } else if (!fromPending && toPending) {
                item.decreaseAllocated(quantity);
            }

            return true;
        }
        return false;
    }

    public List<Position> getAvailablePositions() {
        return positionDAO.getAvailablePositions();
    }

}
