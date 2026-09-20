package store.domain_model.logistics;

import java.util.ArrayList;
import java.util.List;

public class StorageStructure {
    private String id;
    private Type type;
    private List<Position> positions;

    public enum Type {
        DISPLAY,
        BACKSTOCK
    }

    public StorageStructure(String id, Type type) {
        this.id = id;
        this.type = type;
        this.positions = new ArrayList<Position>();
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void addPosition(Position p) {
        positions.add(p);
    }

}
