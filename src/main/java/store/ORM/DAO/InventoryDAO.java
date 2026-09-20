package store.ORM.DAO;

import store.ORM.connection.ConnectionManager;
import store.domain_model.inventory.InventoryItem;
import store.domain_model.inventory.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    Connection connection;
    private ProductDAO productDAO;

    public InventoryDAO() {
        this.connection = ConnectionManager.getInstance().getConnection();
        this.productDAO = new ProductDAO();
    }

    public InventoryItem getItemByid(String id) {
        Product product = productDAO.getProductById(id);
        if(product == null ) {
            System.out.printf("Product not registered%n");
            return null;
        }
        String sql = """
                SELECT i.*
                FROM public."Inventory" i, public."Product" p
                WHERE i.product_id = p.id AND id = ?
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                return new InventoryItem(
                        product,
                        rs.getInt("total_quantity"),
                        rs.getInt("critical_threshold"),
                        rs.getInt("allocated_quantity")
                );
            }
        } catch (SQLException e) {
            System.err.printf("ERROR: " + e.getMessage());
        }
        return null;
    }

    public void addItemToInventory(InventoryItem item) {
        if(!productDAO.checkProductId(item.getProduct())) {
            return;
        }
        productDAO.addProduct(item.getProduct());

        String sql = """
                INSERT INTO public."Inventory" (product_id, total_quantity, critical_threshold) VALUES
                (?, ?, ?)
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1,item.getProduct().getId());
            pst.setInt(2, item.getTotalQuantity());
            pst.setInt(3,item.getCriticalThreshold());

            int rowsAffected = pst.executeUpdate();

            if(rowsAffected > 0) {
                System.out.printf("Product added to inventory%n");
            }
        } catch(SQLException e) {
            System.err.printf("ERROR WHILE ADDING ITEM TO INVENTORY: " + e.getMessage());
        }
    }

    public boolean updateTotalQuantity(InventoryItem item) {
        String sql = """
                UPDATE public."Inventory" SET total_quantity = ?
                WHERE product_id = ?
                """;
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, item.getTotalQuantity());
            pst.setString(2, item.getProduct().getId());

            return pst.executeUpdate() > 0;
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public List<InventoryItem> getLowStockItems() {
        String sql = """
                SELECT product_id, total_quantity, critical_threshold, allocated_quantity
                FROM public."Inventory"
                WHERE total_quantity < critical_threshold
                """;
        List<InventoryItem> foundItems = new ArrayList<>();
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                Product product = productDAO.getProductById(rs.getString("product_id"));
                InventoryItem item = new InventoryItem(
                        product,
                        rs.getInt("total_quantity"),
                        rs.getInt("critical_threshold"),
                        rs.getInt("allocated_quantity")
                );
                foundItems.add(item);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return foundItems;
    }

    public boolean updateAllocatedQuantity(String productId) {
        String sql = """
            UPDATE public."Inventory" i
            SET allocated_quantity = (
                SELECT COALESCE(SUM(quantity), 0)
                FROM public."Position_Product"
                WHERE product_id = ? AND position_id != 'P00054'
            )
            WHERE product_id = ?
            """;
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, productId);
            pst.setString(2, productId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    public List<String> getUnallocatedItems() {
        List<String> foundItems = new ArrayList<>();
        String sql = """
                SELECT product_id
                FROM public."Inventory"
                WHERE total_quantity - allocated_quantity != 0
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                foundItems.add(rs.getString("product_id"));
            }
        } catch(SQLException e) {
            System.err.printf(e.getMessage());
        }
        return  foundItems;
    }

}
