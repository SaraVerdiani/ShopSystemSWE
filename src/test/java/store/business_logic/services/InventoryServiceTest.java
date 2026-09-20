package store.business_logic.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.ORM.DAO.InventoryDAO;
import store.ORM.DAO.PositionProductDAO;
import store.business_logic.services.InventoryService;
import store.domain_model.inventory.Clothes;
import store.domain_model.inventory.InventoryItem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryDAO inventoryDAO;

    @Mock
    private PositionProductDAO positionProductDAO;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void restockItemFail() {
        Clothes dummyProduct = new Clothes("CL0002", "Jacket", "Adidas", 89.99, "Black", "L");
        InventoryItem item = new InventoryItem(dummyProduct, 50, 10, 0);

        when(inventoryDAO.updateTotalQuantity(item)).thenReturn(false);

        boolean result = inventoryService.restockItem(item, 5);

        assertFalse(result);
        verify(inventoryDAO, times(1)).updateTotalQuantity(item);
    }

    @Test
    void restockItemInvalidQuantity() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 0);

        assertThrows(IllegalArgumentException.class, () -> inventoryService.restockItem(item, -5));

        verify(inventoryDAO, never()).updateTotalQuantity(any());
    }

    @Test
    void restockItemSuccess() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 0);

        when(inventoryDAO.updateTotalQuantity(item)).thenReturn(true);

        boolean result = inventoryService.restockItem(item, 20);

        assertTrue(result);
        assertEquals(70, item.getTotalQuantity());
        verify(inventoryDAO, times(1)).updateTotalQuantity(item);
    }

    @Test
    void decreaseStockSuccess() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 0);

        int quantityToRemove = 15;
        int maxRemovable = 20;

        when(inventoryDAO.updateTotalQuantity(item)).thenReturn(true);

        boolean result = inventoryService.decreaseStock(item, quantityToRemove, maxRemovable);

        assertTrue(result);
        assertEquals(35, item.getTotalQuantity());
        verify(inventoryDAO, times(1)).updateTotalQuantity(item);
    }

    @Test
    void decreaseStockExceedMaxQuantity() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 0);

        assertThrows(IllegalArgumentException.class, () -> inventoryService.decreaseStock(item, 20, 10));

        verify(inventoryDAO, never()).updateTotalQuantity(any());
    }

    @Test
    void decreaseStockInvalidQuantity() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 0);

        assertThrows(IllegalArgumentException.class, () -> inventoryService.decreaseStock(item, -5, 10));

        verify(inventoryDAO, never()).updateTotalQuantity(any());
    }

    @Test
    void addItemToInventoryInvalidThreshold() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 0, -5, 0);

        assertThrows(IllegalArgumentException.class, () -> inventoryService.addItemToInventory(item));
        verify(inventoryDAO, never()).addItemToInventory(any());
    }

    @Test
    void addItemToInventoryEmptyNameOrBrand() {
        Clothes product = new Clothes("CL0002", "", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 0, 10, 0);

        assertThrows(IllegalArgumentException.class, () -> inventoryService.addItemToInventory(item));
        verify(inventoryDAO, never()).addItemToInventory(any());
    }

    @Test
    void addItemToInventoryInvalidPrice() {
        Clothes product = new Clothes("CL0003", "T-Shirt", "Nike", -10.00, "Red", "M");
        InventoryItem item = new InventoryItem(product, 0, 10, 0);

        assertThrows(IllegalArgumentException.class, () -> inventoryService.addItemToInventory(item));
        verify(inventoryDAO, never()).addItemToInventory(any());
    }

}