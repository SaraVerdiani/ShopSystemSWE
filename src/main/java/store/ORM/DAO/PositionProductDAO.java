package store.ORM.DAO;

import store.ORM.connection.ConnectionManager;
import store.domain_model.inventory.Product;
import store.domain_model.logistics.Position;
import store.domain_model.logistics.PositionProduct;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PositionProductDAO {

    Connection connection;

    public PositionProductDAO() {
        this.connection = ConnectionManager.getInstance().getConnection();
    }

    public boolean addProductToPosition(String positionId, String productId, int quantity) {
        String sql = """
        INSERT INTO public."Position_Product" (position_id, product_id, quantity)
        VALUES (?, ?, ?)
        ON CONFLICT (position_id, product_id)
        DO UPDATE SET quantity = public."Position_Product".quantity + EXCLUDED.quantity
        """;

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, positionId);
            pst.setString(2, productId);
            pst.setInt(3, quantity);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public List<PositionProduct> getAllocationsForProduct(Product product) {
        List<PositionProduct> allocations = new ArrayList<>();

        String sql = """
                SELECT pos.*, pp.quantity
                FROM public."Position" pos
                JOIN public."Position_Product" pp ON pos.id = pp.position_id
                WHERE pp.product_id = ?
                """;

        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, product.getId());
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                Position pos = new Position(
                        rs.getString("id"),
                        rs.getString("storage_structure"),
                        rs.getString("description"),
                        rs.getInt("capacity")
                );

                int quantity = rs.getInt("quantity");

                PositionProduct allocation = new PositionProduct(pos, product, quantity);
                allocations.add(allocation);
            }
        } catch(SQLException e) {
            System.err.println( e.getMessage());
        }
        return allocations;
    }

    public boolean removeItemsFromPosition(String posId, String productId, int quantity) {
        String updateSql = """
                UPDATE public."Position_Product"
                SET quantity = quantity - ?
                WHERE position_id = ? AND product_id = ?
                """;
        String deleteSql = """
                DELETE FROM public."Position_Product"
                WHERE position_id = ? AND product_id = ? AND quantity <= 0
                """;
        try {
            boolean isUpdated;

            try (PreparedStatement pstUpdate = connection.prepareStatement(updateSql)) {
                pstUpdate.setInt(1, quantity);
                pstUpdate.setString(2, posId);
                pstUpdate.setString(3, productId);

                isUpdated = pstUpdate.executeUpdate() > 0;
            }

            if (isUpdated) {
                try (PreparedStatement pstDelete = connection.prepareStatement(deleteSql)) {
                    pstDelete.setString(1, posId);
                    pstDelete.setString(2, productId);

                    pstDelete.executeUpdate();
                }
            }

            return isUpdated;

        } catch (SQLException e) {
            System.err.println("Errore in removeItemsFromPosition: " + e.getMessage());
            return false;
        }
    }

    public boolean moveItemsFromPosition(String sourcePosId, String destPosId, String productId, int quantity) {
        String sqlRemove = """
                UPDATE public."Position_Product"
                SET quantity = quantity - ?
                WHERE position_id = ? AND product_id = ?
                """;

        String sqlClean = """
                DELETE FROM public."Position_Product" 
                WHERE position_id = ? AND product_id = ? AND quantity <= 0
                """;

        String sqlAdd = """
                INSERT INTO public."Position_Product" (position_id, product_id, quantity)
                VALUES (?, ?, ?)
                ON CONFLICT (position_id, product_id)
                DO UPDATE SET quantity = public."Position_Product".quantity + EXCLUDED.quantity
                """;

        try {
            try (PreparedStatement pstRemove = connection.prepareStatement(sqlRemove)) {
                pstRemove.setInt(1, quantity);
                pstRemove.setString(2, sourcePosId);
                pstRemove.setString(3, productId);

                pstRemove.executeUpdate();
            }
            try (PreparedStatement pstClean = connection.prepareStatement(sqlClean)) {
                pstClean.setString(1, sourcePosId);
                pstClean.setString(2, productId);
                pstClean.executeUpdate();
            }
            try (PreparedStatement pstAdd = connection.prepareStatement(sqlAdd)) {
                pstAdd.setString(1, destPosId);
                pstAdd.setString(2, productId);
                pstAdd.setInt(3, quantity);
                pstAdd.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public int getTotalPlacedQuantityInPosition(String posId) {
        String sql = """
                SELECT SUM(quantity) AS total
                FROM public."Position_Product"
                WHERE position_id = ?
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, posId);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                return rs.getInt("total");
            }

        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return -1;
    }
}
