package store.business_logic.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.ORM.DAO.InventoryDAO;
import store.ORM.DAO.PositionDAO;
import store.ORM.DAO.PositionProductDAO;
import store.business_logic.services.LogisticsService;
import store.domain_model.inventory.Clothes;
import store.domain_model.inventory.InventoryItem;
import store.domain_model.logistics.Position;
import store.domain_model.logistics.PositionProduct;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogisticsServiceTest {

    @Mock
    private PositionDAO positionDAO;

    @Mock
    private InventoryDAO inventoryDAO;

    @Mock
    private PositionProductDAO positionProductDAO;

    @InjectMocks
    private LogisticsService logisticsService;

    @Test
    void assignProductPositionNewPositionSuccess() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 0);
        Position destination = new Position("P00001", "Shelf A", "Zone 1", 100);

        when(positionDAO.getAvailableSpaceForPosition("P00001")).thenReturn(50);
        when(positionProductDAO.addProductToPosition("P00001", "CL0001", 10)).thenReturn(true);

        boolean result = logisticsService.assignProductPosition(destination, item, 10);

        assertTrue(result);
        assertEquals(1, item.getAllocations().size());
        assertEquals("P00001", item.getAllocations().get(0).getPosition().getId());
        assertEquals(10, item.getAllocations().get(0).getQuantity());
        assertEquals(10, item.getAllocatedQuantity());
        verify(inventoryDAO, times(1)).updateAllocatedQuantity("CL0001");
    }

    @Test
    void assignProductPositionExistingPositionSuccess() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 5);
        Position destination = new Position("P00001", "Shelf A", "Zone 1", 100);

        item.addAllocation(new PositionProduct(destination, product, 5));

        when(positionDAO.getAvailableSpaceForPosition("P00001")).thenReturn(50);
        when(positionProductDAO.addProductToPosition("P00001", "CL0001", 10)).thenReturn(true);

        boolean result = logisticsService.assignProductPosition(destination, item, 10);

        assertTrue(result);
        assertEquals(1, item.getAllocations().size());
        assertEquals(15, item.getAllocations().get(0).getQuantity());
        assertEquals(15, item.getAllocatedQuantity());
    }

    @Test
    void assignProductPositionPendingZone() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 0);
        Position pendingZone = new Position("P00054", "Backstock", "Pending Zone", 999);

        when(positionDAO.getAvailableSpaceForPosition("P00054")).thenReturn(100);
        when(positionProductDAO.addProductToPosition("P00054", "CL0001", 10)).thenReturn(true);

        boolean result = logisticsService.assignProductPosition(pendingZone, item, 10);

        assertTrue(result);
        assertEquals(1, item.getAllocations().size());
        assertEquals("P00054", item.getAllocations().get(0).getPosition().getId());
        assertEquals(10, item.getAllocations().get(0).getQuantity());
        assertEquals(0, item.getAllocatedQuantity());
        verify(inventoryDAO, never()).updateAllocatedQuantity(anyString());
    }

    @Test
    void assignProductPositionNoCapacity() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 0);
        Position destination = new Position("P00001", "Shelf A", "Zone 1", 100);

        when(positionDAO.getAvailableSpaceForPosition("P00001")).thenReturn(5);

        boolean result = logisticsService.assignProductPosition(destination, item, 10);

        assertFalse(result);
        assertEquals(0, item.getAllocations().size());
        assertEquals(0, item.getAllocatedQuantity());
        verify(positionProductDAO, never()).addProductToPosition(anyString(), anyString(), anyInt());
    }

    @Test
    void moveItemsFromPositionInvalidQuantity() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 20);
        Position sourcePos = new Position("P00001", "Shelf A", "description", 100);
        Position destPos = new Position("P00002", "Shelf B", "description", 100);

        assertThrows(IllegalArgumentException.class,
                () -> logisticsService.moveItemsFromPosition(sourcePos, destPos, item, -5, 10));

        verify(positionProductDAO, never()).moveItemsFromPosition(anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    void moveItemsFromPositionQuantityExceedsMaxMovable() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 20);
        Position sourcePos = new Position("P00001", "Shelf A", "description", 100);
        Position destPos = new Position("P00002", "Shelf B", "description", 100);

        assertThrows(IllegalArgumentException.class,
                () -> logisticsService.moveItemsFromPosition(sourcePos, destPos, item, 15, 10));

        verify(positionProductDAO, never()).moveItemsFromPosition(anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    void moveItemsFromPositionDestinationCapacityExceeded() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 20);
        Position sourcePos = new Position("P00001", "Shelf A", "description", 100);
        Position destPos = new Position("P00002", "Shelf B", "description", 100);

        item.addAllocation(new PositionProduct(sourcePos, product, 20));

        when(positionDAO.getAvailableSpaceForPosition("P00002")).thenReturn(5);

        boolean result = logisticsService.moveItemsFromPosition(sourcePos, destPos, item, 10, 20);

        assertFalse(result);
        assertEquals(1, item.getAllocations().size());
        assertEquals(20, item.getAllocations().get(0).getQuantity());
        verify(positionProductDAO, never()).moveItemsFromPosition(anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    void moveItemsFromPositionPartialMoveSuccess() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 20);
        Position sourcePos = new Position("P00001", "Shelf A", "description", 100);
        Position destPos = new Position("P00002", "Shelf B", "description", 100);

        item.addAllocation(new PositionProduct(sourcePos, product, 20));

        when(positionDAO.getAvailableSpaceForPosition("P00002")).thenReturn(50);
        when(positionProductDAO.moveItemsFromPosition("P00001", "P00002", "CL0001", 15)).thenReturn(true);

        boolean result = logisticsService.moveItemsFromPosition(sourcePos, destPos, item, 15, 20);

        assertTrue(result);
        assertEquals(2, item.getAllocations().size());

        PositionProduct updatedSource = item.getAllocations().stream().filter(p -> p.getPosition().getId().equals("P00001")).findFirst().get();
        assertEquals(5, updatedSource.getQuantity());

        PositionProduct newDest = item.getAllocations().stream().filter(p -> p.getPosition().getId().equals("P00002")).findFirst().get();
        assertEquals(15, newDest.getQuantity());

        assertEquals(20, item.getAllocatedQuantity());

        verify(positionProductDAO, times(1)).moveItemsFromPosition("P00001", "P00002", "CL0001", 15);
        verify(inventoryDAO, times(1)).updateAllocatedQuantity("CL0001");
    }

    @Test
    void moveItemsFromPositionTotalMoveSuccess() {
        Clothes product = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(product, 50, 10, 20);
        Position sourcePos = new Position("P00001", "Shelf A", "description", 100);
        Position destPos = new Position("P00002", "Shelf B", "description", 100);

        item.addAllocation(new PositionProduct(sourcePos, product, 20));

        when(positionDAO.getAvailableSpaceForPosition("P00002")).thenReturn(50);
        when(positionProductDAO.moveItemsFromPosition("P00001", "P00002", "CL0001", 20)).thenReturn(true);

        boolean result = logisticsService.moveItemsFromPosition(sourcePos, destPos, item, 20, 20);

        assertTrue(result);
        assertEquals(1, item.getAllocations().size());
        assertEquals("P00002", item.getAllocations().get(0).getPosition().getId());
        assertEquals(20, item.getAllocations().get(0).getQuantity());
        assertEquals(20, item.getAllocatedQuantity());

        verify(positionProductDAO, times(1)).moveItemsFromPosition("P00001", "P00002", "CL0001", 20);
    }
}
