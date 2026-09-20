package store.domain_model.inventory;

import store.domain_model.logistics.PositionProduct;

import java.util.ArrayList;
import java.util.List;

//Classe per la gestione dello stato del prodotto
public class InventoryItem {
    private Product product;
    private int totalQuantity;
    private int allocatedQuantity;
    private int criticalThreshold;
    private List<PositionProduct> allocations; //per cercare le posizioni dei prodotti attraverso searchProduct() in WorkerController

    public InventoryItem(Product product, int totalQuantity, int criticalThreshold, int allocatedQuantity) {
        this.product = product;
        this.totalQuantity = totalQuantity;
        this.criticalThreshold = criticalThreshold;
        this.allocatedQuantity = allocatedQuantity;
        this.allocations = new ArrayList<>();
    }

    public Product getProduct() {
        return product;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getAllocatedQuantity() { // Prodotti che sono stati posizionati
        return allocatedQuantity;
    }

    public int getCriticalThreshold() {
        return criticalThreshold;
    }

    public int getUnallocatedQuantity() { // Prodotti che non sono ancora stati posizionati
        return totalQuantity - allocatedQuantity;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setAllocatedQuantity(int allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
    }

    public void setCriticalThreshold(int criticalThreshold) {
        this.criticalThreshold = criticalThreshold;
    }

    public void setAllocations(List<PositionProduct> allocations) {
        this.allocations = allocations;
    }

    public boolean isUnderCriticalThreshold() {
        return totalQuantity < criticalThreshold;
    }

    public void increaseAllocated(int quantity) { // Aumenta la quantità di prodotti posizionati
        this.allocatedQuantity += quantity;
    }

    public void decreaseAllocated(int quantity) { // Diminuisce la quantità di prodotti posizionati
        this.allocatedQuantity -= quantity;
    }

    public void addStock(int amount) { // Aggiunge uno stock di prodotti
        this.totalQuantity += amount;
    }

    public void addAllocation(PositionProduct allocation) { // Aggiunge una posizione dell'item in caso di posizionamento
        if(!allocations.contains(allocation)) {
            allocations.add(allocation);
        }
    }

    public List<PositionProduct> getAllocations() {
        return allocations;
    }
}
