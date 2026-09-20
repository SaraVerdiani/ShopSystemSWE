package store.ORM.DAO;

import store.ORM.connection.ConnectionManager;
import store.domain_model.inventory.Clothes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClothingDAO {

    Connection connection;

    public ClothingDAO() {
        this.connection = ConnectionManager.getInstance().getConnection();
    }

    public Clothes getClothingById(String id) {
        String sql = """ 
                SELECT p.id, p.name, p.brand, p.price, c.color, c.size
                FROM public."Product" p, public."Clothing" c
                WHERE p.id = c.id AND c.id = ?
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1,id);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                return new Clothes(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getString("color"),
                        rs.getString("size")
                );
            }
        } catch(SQLException e) {
            System.err.printf("ERROR: " + e.getMessage());

        }
        return null;

    }

    public void addClothing(Clothes clothes) {
        String sql = """
                INSERT INTO public."Clothing" (id, size, color) VALUES
                (?, ?, ?)
                """;
        try(PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, clothes.getId());
            pst.setString(2, clothes.getSize());
            pst.setString(3, clothes.getColor());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.printf(e.getMessage());
        }
    }

    public boolean update(Clothes clothes) {
        String sql = """
                UPDATE public."Clothing" SET size = ?, color = ?
                WHERE id = ?
                """;
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, clothes.getSize());
            pst.setString(2, clothes.getColor());
            pst.setString(3, clothes.getId());

            return pst.executeUpdate() > 0;
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
}

