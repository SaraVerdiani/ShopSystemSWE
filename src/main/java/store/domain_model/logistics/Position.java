package store.domain_model.logistics;

public class Position {
    private String shelfId;
    private String description;
    private String id;
    private int capacity;

    public Position(String id, String shelfId, String description, int capacity) {
        this.shelfId = shelfId;
        this.description = description;
        this.id = id;
        this.capacity = capacity;
    }

    public String getShelfId() {
        return shelfId;
    }

    public String getDescription() {
        return description;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getId() {
        return id;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setShelfId(String shelfId) {
        this.shelfId = shelfId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

}
