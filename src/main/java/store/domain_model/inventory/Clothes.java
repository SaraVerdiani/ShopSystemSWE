package store.domain_model.inventory;

public class Clothes extends Product {
    private String size;
    private String color;


    public Clothes(String id, String name, String brand, double price, String color, String size) {
        super(id, name, brand, price);
        this.color = color;
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
