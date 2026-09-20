package store.ORM.DAO;

import store.ORM.connection.ConnectionManager;
import store.domain_model.logistics.Position;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PositionDAO {

    Connection connection;
    private StorageStructureDAO storageStructureDAO;

    public PositionDAO() {
        this.connection = ConnectionManager.getInstance().getConnection();
        this.storageStructureDAO = new StorageStructureDAO();

    }

    public Position getPositionById(String id) {
        String sql = """
                SELECT *
                FROM public."Position"
                WHERE id = ?
                """;
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Position(
                        rs.getString("id"),
                        rs.getString("storage_structure"),
                        rs.getString("description"),
                        rs.getInt("capacity")
                );
            }
        } catch (SQLException e) {
            System.err.printf(e.getMessage());
        }
        return null;
    }

    public boolean checkPositionExist(String id) {
        String sql = """
                SELECT *
                FROM public."Position"
                WHERE id = ?
                """;
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.err.printf(e.getMessage());
        }
        return false;
    }


    public List<Position> getAvailableBackstockPositions() {
        List<Position> availableBackstockPositions = new ArrayList<>();
        String sql = """
                SELECT p.id, p.storage_structure, p.description, p.capacity,
                (SUM(pp.quantity)) AS occupied_space
                FROM public."Storage_structure" ss
                JOIN public."Position" p ON ss.id = p.storage_structure
                LEFT JOIN public."Position_Product" pp ON p.id = pp.position_id
                WHERE ss.type = 'backstock'
                GROUP BY p.id, p.storage_structure, p.description, p.capacity
                HAVING p.capacity - (SUM(pp.quantity)) > 0
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                Position pos = new Position(
                        rs.getString("id"),
                        rs.getString("storage_structure"),
                        rs.getString("description"),
                        rs.getInt("capacity")
                );
                availableBackstockPositions.add(pos);
            }
        } catch (SQLException e) {
            System.err.printf(e.getMessage());
            return null;
        }
        return availableBackstockPositions;
    }

    public int getAvailableSpaceForPosition(String positionId) {
        String sql = """
            SELECT p.capacity - COALESCE(SUM(pp.quantity), 0) AS available_space
            FROM public."Position" p
            LEFT JOIN public."Position_Product" pp ON p.id = pp.position_id
            WHERE p.id = ?
            GROUP BY p.capacity
            """;

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, positionId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("available_space");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return -1;
    }

    public List<Position> getAvailablePositions() {
        List<Position> availablePositions = new ArrayList<>();
        String sql = """
                SELECT p.*
                FROM public."Position" p
                LEFT JOIN public."Position_Product" pp ON p.id = pp.position_id
                GROUP BY
                    p.id,
                    p.storage_structure,
                    p.description,
                    p.capacity
                HAVING (p.capacity - COALESCE(SUM(pp.quantity), 0)) > 0;
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                Position pos = new Position(
                        rs.getString("id"),
                        rs.getString("storage_structure"),
                        rs.getString("description"),
                        rs.getInt("capacity")
                );
                availablePositions.add(pos);
            }
        } catch (SQLException e) {
            System.err.printf(e.getMessage());
            return null;
        }
        return availablePositions;
    }
}
