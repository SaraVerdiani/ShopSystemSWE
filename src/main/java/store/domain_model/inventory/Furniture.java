package store.domain_model.inventory;

public class Furniture extends Product {
    private String color;
    private float height;
    private float width;
    private float length;

    public Furniture(String id, String name, String brand, double price, float height, float width, float length, String color) {
        super(id, name, brand, price);
        this.height = height;
        this.width = width;
        this.length = length;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return length;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setLength(float length) {
        this.length = length;
    }
}
