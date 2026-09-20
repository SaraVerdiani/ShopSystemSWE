package store.domain_model.logistics;

import store.domain_model.inventory.Product;

public class PositionProduct {
    private Position position;
    private Product product;
    private int quantity;

    public PositionProduct(Position position, Product product, int quantity) {
        this.position = position;
        this.product = product;
        this.quantity = quantity;
    }

    public Position getPosition() { return position; }
    public Product getProduct() { return product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
