package store.business_logic.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.ORM.DAO.*;
import store.business_logic.filter.ProductFilter;
import store.domain_model.inventory.Clothes;
import store.domain_model.inventory.Furniture;
import store.domain_model.inventory.InventoryItem;
import store.domain_model.inventory.Product;
import store.domain_model.logistics.Position;
import store.domain_model.logistics.PositionProduct;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkerServiceTest {

    @Mock private InventoryDAO inventoryDAO;
    @Mock private ProductDAO productDAO;
    @Mock private PositionDAO positionDAO;
    @Mock private ClothingDAO clothingDAO;
    @Mock private FurnitureDAO furnitureDAO;
    @Mock private FoodDAO foodDAO;
    @Mock private PositionProductDAO positionProductDAO;

    @InjectMocks
    private WorkerService workerService;

    @Test
    void searchProductNoFilterSuccess() {
        Clothes dummyProduct = new Clothes("CL0001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem expectedItem = new InventoryItem(dummyProduct, 50, 10, 20);

        Position dummyPosition = new Position("P00004", "A1-02", "description", 100);
        PositionProduct allocation = new PositionProduct(dummyPosition, dummyProduct, 20);
        List<PositionProduct> expectedAllocations = List.of(allocation);

        when(inventoryDAO.getItemByid("CL0001")).thenReturn(expectedItem);
        when(positionProductDAO.getAllocationsForProduct(dummyProduct)).thenReturn(expectedAllocations);

        Map<InventoryItem, List<PositionProduct>> result = workerService.searchProduct("CL0001", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(expectedItem));
        assertEquals(expectedAllocations, result.get(expectedItem));

        assertTrue(expectedItem.getAllocations().contains(allocation));

        verify(productDAO, never()).searchDynamic(anyString(), any());
    }

    @Test
    void searchProductWithFilterSuccess() {
        ProductFilter dummyFilter = new ProductFilter.Builder().withColor("Red").build();
        Clothes dummyProduct = new Clothes("CL0002", "Felpa", "Adidas", 49.99, "Red", "L");
        InventoryItem expectedItem = new InventoryItem(dummyProduct, 30, 5, 10);
        List<PositionProduct> expectedAllocations = List.of();

        when(productDAO.searchDynamic("CL", dummyFilter)).thenReturn(List.of("CL0002"));
        when(inventoryDAO.getItemByid("CL0002")).thenReturn(expectedItem);
        when(positionProductDAO.getAllocationsForProduct(dummyProduct)).thenReturn(expectedAllocations);

        Map<InventoryItem, List<PositionProduct>> result = workerService.searchProduct("CL", dummyFilter);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(expectedItem));
        verify(productDAO, times(1)).searchDynamic("CL", dummyFilter);
        verify(inventoryDAO, times(1)).getItemByid("CL0002");
    }

    @Test
    void searchProductNotFound() {
        when(inventoryDAO.getItemByid("WRONG_ID")).thenReturn(null);

        Map<InventoryItem, List<PositionProduct>> result = workerService.searchProduct("WRONG_ID", null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(positionProductDAO, never()).getAllocationsForProduct(any());
    }

    @Test
    void editItemSuccess() {
        Clothes originalProduct = new Clothes("CL0001", "T-Shirt Vecchia", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(originalProduct, 50, 10, 0);

        Clothes newData = new Clothes("CL0001", "T-Shirt Nuova", "Nike Pro", 35.99, "Blue", "L");

        when(productDAO.update(newData)).thenReturn(true);
        when(clothingDAO.update(newData)).thenReturn(true);

        boolean result = workerService.editItem(item, newData);

        assertTrue(result);

        Product updatedProduct = item.getProduct();

        assertInstanceOf(Clothes.class, updatedProduct);
        Clothes updatedClothes = (Clothes) updatedProduct;

        assertEquals("CL0001", updatedClothes.getId());
        assertEquals("T-Shirt Nuova", updatedClothes.getName());
        assertEquals("Nike Pro", updatedClothes.getBrand());
        assertEquals(35.99, updatedClothes.getPrice());
        assertEquals("Blue", updatedClothes.getColor());
        assertEquals("L", updatedClothes.getSize());

        assertEquals(50, item.getTotalQuantity());
        assertEquals(10, item.getCriticalThreshold());
        assertEquals(0, item.getAllocatedQuantity());

        verify(productDAO, times(1)).update(newData);
        verify(clothingDAO, times(1)).update(newData);

        verify(inventoryDAO, never()).updateTotalQuantity(any());
        verify(foodDAO, never()).update(any());
        verify(furnitureDAO, never()).update(any());
    }

    @Test
    void editItemEmptyNameError() {
        Clothes originalProduct = new Clothes("CL001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(originalProduct, 10, 5, 0);

        Clothes badData = new Clothes("CL001", "", "Nike", 29.99, "Red", "M");

        assertThrows(IllegalArgumentException.class, () -> workerService.editItem(item, badData));

        verify(productDAO, never()).update(any());

        Product intactProduct = item.getProduct();
        assertInstanceOf(Clothes.class, intactProduct);
        Clothes intactClothes = (Clothes) intactProduct;

        assertEquals("CL001", intactClothes.getId());
        assertEquals("T-Shirt", intactClothes.getName());
        assertEquals("Nike", intactClothes.getBrand());
        assertEquals(29.99, intactClothes.getPrice());
        assertEquals("Red", intactClothes.getColor());
        assertEquals("M", intactClothes.getSize());

        assertEquals(10, item.getTotalQuantity());
    }

    @Test
    void editItemEmptyBrandError() {
        Clothes originalProduct = new Clothes("CL001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(originalProduct, 10, 5, 0);

        Clothes badData = new Clothes("CL001", "T-Shirt", "   ", 29.99, "Red", "M");

        assertThrows(IllegalArgumentException.class, () -> workerService.editItem(item, badData));

        verify(productDAO, never()).update(any());
        assertEquals("Nike", item.getProduct().getBrand());
    }

    @Test
    void editItemInvalidPriceError() {
        Clothes originalProduct = new Clothes("CL001", "T-Shirt", "Nike", 29.99, "Red", "M");
        InventoryItem item = new InventoryItem(originalProduct, 10, 5, 0);

        Clothes badData = new Clothes("CL001", "T-Shirt", "Nike", -15.50, "Red", "M");

        assertThrows(IllegalArgumentException.class, () -> workerService.editItem(item, badData));

        verify(productDAO, never()).update(any());
        assertEquals(29.99, item.getProduct().getPrice());
    }

    @Test
    void editItemInvalidFurnitureDimensionError() {
        Furniture originalProduct = new Furniture("FR001", "Table", "IKEA", 99.99, 100.0f, 50.0f, 50.0f, "White");
        InventoryItem item = new InventoryItem(originalProduct, 10, 5, 0);

        Furniture badData = new Furniture("FR001", "Table", "IKEA", 99.99, -10.0f, 50.0f, 50.0f, "White");

        assertThrows(IllegalArgumentException.class, () -> workerService.editItem(item, badData));

        verify(productDAO, never()).update(any());

        Product intactProduct = item.getProduct();
        assertInstanceOf(Furniture.class, intactProduct);
        assertEquals(100.0f, ((Furniture) intactProduct).getHeight());
    }

    @Test
    void editItemFoodExpirationDateFormatError() {
        String invalidDate = "31/12/2026";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setLenient(false);

        assertThrows(ParseException.class, () -> formatter.parse(invalidDate));
    }
}