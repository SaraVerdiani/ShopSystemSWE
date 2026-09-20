package store.ORM.DAO;

import store.ORM.connection.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StorageStructureDAO {
    Connection connection;

    public StorageStructureDAO() {
        this.connection = ConnectionManager.getInstance().getConnection();
    }

    public List<String> getStorageStructureByType(String type) {
        List<String> foundStorageStructureIds = new ArrayList<>();
        String sql = """
                SELECT id
                FROM public."Storage_structure"
                WHERE type = ?
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, type);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                String storageStructureId = rs.getString("id");
                foundStorageStructureIds.add(storageStructureId);
            }
        } catch(SQLException e) {
            System.err.printf(e.getMessage());
            return null;
        }
        return foundStorageStructureIds;
    }

}
